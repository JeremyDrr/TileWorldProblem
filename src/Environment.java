import enums.Direction;

import java.util.*;
import java.util.concurrent.locks.*;

public class Environment {
    private int width, height, operationTime;
    private List<Agent> agents;
    private Set<Point> obstacles;
    private Map<Point, List<Tile>> tiles;
    private Map<Point, Hole> holes;
    private Queue<Operation> operationQueue;
    private Lock lock;

    public Environment(int width, int height, int operationTime) {
        this.width = width;
        this.height = height;
        this.operationTime = operationTime;
        this.agents = new ArrayList<>();
        this.obstacles = new HashSet<>();
        this.tiles = new HashMap<>();
        this.holes = new HashMap<>();
        this.operationQueue = new LinkedList<>();
        this.lock = new ReentrantLock();
    }

    public void addAgent(Agent agent) {
        agents.add(agent);
    }

    public void addObstacle(int x, int y) {
        obstacles.add(new Point(x, y));
    }

    public void addTile(Tile tile) {
        tiles.computeIfAbsent(new Point(tile.getX(), tile.getY()), k -> new ArrayList<>()).add(tile);
    }

    public void addHole(Hole hole) {
        holes.put(new Point(hole.getX(), hole.getY()), hole);
    }

    public void processOperations() {
        while (true) {
            lock.lock();

            try {
                if (!operationQueue.isEmpty()) {
                    Operation op = operationQueue.poll();
                    executeOperation(op);
                }
            } finally {
                lock.unlock();
            }

            try {
                Thread.sleep(operationTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            printGrid();
        }
    }

    private void executeOperation(Operation op) {
        Agent agent = agents.get(op.getAgentId());
        switch (op.getType()) {
            case PICK:
                handlePickOperation(agent, op.getColor());
                break;
            case DROP:
                handleDropOperation(agent);
                break;
            case MOVE:
                handleMoveOperation(agent, op.getDirection());
                break;
            case USE_TILE:
                handleUseTileOperation(agent, op.getDirection());
                break;
            case TRANSFER_POINTS:
                handleTransferPointsOperation(agent, op.getTargetAgentId(), op.getPoints());
                break;
        }
    }

    private void handlePickOperation(Agent agent, String color) {
        Point currentPos = new Point(agent.getX(), agent.getY());
        List<Tile> tileList = tiles.get(currentPos);
        if (tileList != null) {
            for (Tile tile : tileList) {
                if (tile.getColor().equals(color)) {
                    agent.setCarriedTile(tile);
                    System.out.println("[PICK]: " + agent.getColor() + " Picked tile at: " + tile.getX() + " " + tile.getY());
                    tileList.remove(tile);
                    agent.notifyOperationSuccess();
                    return;
                }
            }
        }

        agent.notifyOperationFailure();
    }

    private void handleDropOperation(Agent agent) {
        Point currentPos = new Point(agent.getX(), agent.getY());
        Tile carriedTile = agent.getCarriedTile();
        if (carriedTile != null) {
            tiles.computeIfAbsent(currentPos, k -> new ArrayList<>()).add(carriedTile);
            System.out.println("[DROP]: " + agent.getColor() + " Dropped tile: " + carriedTile.getX() + " " + carriedTile.getY());
            agent.setCarriedTile(null);
            agent.notifyOperationSuccess();
        } else {
            agent.notifyOperationFailure();
        }
    }

    private void handleMoveOperation(Agent agent, Direction direction) {
        int newX = agent.getX(), newY = agent.getY();
        switch (direction) {
            case NORTH:
                newY--;
                break;
            case SOUTH:
                newY++;
                break;
            case EAST:
                newX++;
                break;
            case WEST:
                newX--;
                break;
        }
        Point newPos = new Point(newX, newY);
        if (isValidPosition(newPos) && !obstacles.contains(newPos)) {
            agent.setX(newX);
            agent.setY(newY);
            System.out.println("[MOVE]: " + agent.getColor() + " Moved to: " + newPos.x + ", " + newPos.y);
            agent.notifyOperationSuccess();
        } else {
            agent.notifyOperationFailure();
        }
    }

    private void handleUseTileOperation(Agent agent, Direction direction) {
        int adjX = agent.getX(), adjY = agent.getY();
        switch (direction) {
            case NORTH:
                adjY--;
                break;
            case SOUTH:
                adjY++;
                break;
            case EAST:
                adjX++;
                break;
            case WEST:
                adjX--;
                break;
        }
        Point adjPos = new Point(adjX, adjY);
        Hole hole = holes.get(adjPos);
        if (hole != null && hole.getDepth() > 0) {
            Tile carriedTile = agent.getCarriedTile();
            if (carriedTile != null) {
                hole.setDepth(hole.getDepth() - 1);
                if (carriedTile.getColor().equals(hole.getColor())) {
                    agent.addPoints(hole.getDepth() == 0 ? 50 : 10);
                }
                System.out.println("[USE]: " + agent.getColor() + " Used Tile at: " + agent.getX() + agent.getY());
                agent.setCarriedTile(null);
                agent.notifyOperationSuccess();
            } else {
                agent.notifyOperationFailure();
            }
        } else {
            agent.notifyOperationFailure();
        }
    }

    private void handleTransferPointsOperation(Agent agent, int targetAgentId, int points) {
        Agent targetAgent = agents.get(targetAgentId);
        if (agent.getPoints() >= points) {
            agent.subtractPoints(points);
            targetAgent.addPoints(points);
            agent.notifyOperationSuccess();
        } else {
            agent.notifyOperationFailure();
        }
    }

    private boolean isValidPosition(Point pos) {
        return pos.x >= 0 && pos.x < width && pos.y >= 0 && pos.y < height;
    }

    public void submitOperation(Operation op) {
        lock.lock();
        try {
            operationQueue.offer(op);
        } finally {
            lock.unlock();
        }
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public Agent getAgentById(int id) {
        for (Agent agent : agents) {
            if (agent.getId() == id) {
                return agent;
            }
        }
        return null;
    }

    public void printGrid() {
        char[][] grid = new char[height][width];

        // Fill grid with empty cells
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = '.'; // Empty cell representation
            }
        }

        // Place obstacles, tiles
        for (Point obstacle : obstacles) {
            grid[obstacle.y][obstacle.x] = 'X';
        }
        for (List<Tile> tileList : tiles.values()) {
            for (Tile tile : tileList) {
                grid[tile.getY()][tile.getX()] = tile.getColor().charAt(0);
            }
        }
        for(Hole hole : holes.values()){
            grid[hole.getY()][hole.getX()] = 'H';
        }

        // Place agents (handle overlaps)
        for (Agent agent : agents) {
            int x = agent.getX();
            int y = agent.getY();
            if (Character.isLetter(grid[y][x])) {
                grid[y][x] = '*'; // Overlap indicator
            } else {
                grid[y][x] = agent.getColor().charAt(0);
            }
        }

        // Print the grid to the console
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[y][x] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}
