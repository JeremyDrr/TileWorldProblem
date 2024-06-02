import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String filename = (args.length > 0) ? args[0] : "system.txt";

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String[] params = br.readLine().split(" ");
            int N = Integer.parseInt(params[0]);
            int operationTime = Integer.parseInt(params[1]);
            int totalTime = Integer.parseInt(params[2]);
            int width = Integer.parseInt(params[3]);
            int height = Integer.parseInt(params[4]);

            Environment environment = new Environment(width, height, operationTime);

            String[] colors = br.readLine().split(" ");
            String[] positions = br.readLine().split(" ");
            for (int i = 0; i < N; i++) {
                int x = Integer.parseInt(positions[2 * i]);
                int y = Integer.parseInt(positions[2 * i + 1]);
                Agent agent = new Agent(i, x, y, colors[i], environment);
                environment.addAgent(agent);
                new Thread(agent).start();
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("OBSTACLES")) {
                    while ((line = br.readLine()) != null && !line.equals("TILES")) {
                        String[] obstaclePos = line.split(" ");
                        int x = Integer.parseInt(obstaclePos[0]);
                        int y = Integer.parseInt(obstaclePos[1]);
                        environment.addObstacle(x, y);
                    }
                }

                if (line.equals("TILES")) {
                    while ((line = br.readLine()) != null && !line.equals("HOLES")) {
                        String[] tileInfo = line.split(" ");
                        int count = Integer.parseInt(tileInfo[0]);
                        String color = tileInfo[1];
                        int x = Integer.parseInt(tileInfo[2]);
                        int y = Integer.parseInt(tileInfo[3]);
                        for (int i = 0; i < count; i++) {
                            environment.addTile(new Tile(color, x, y));
                        }
                    }
                }

                if (line.equals("HOLES")) {
                    while ((line = br.readLine()) != null) {
                        String[] holeInfo = line.split(" ");
                        int depth = Integer.parseInt(holeInfo[0]);
                        String color = holeInfo[1];
                        int x = Integer.parseInt(holeInfo[2]);
                        int y = Integer.parseInt(holeInfo[3]);
                        environment.addHole(new Hole(depth, color, x, y));
                    }
                }
            }

            new Thread(() -> {
                environment.processOperations();
            }).start();

            Thread.sleep(totalTime);
            // Display final points of all agents
            for (Agent agent : environment.getAgents()) {
                System.out.println("Agent " + agent.getColor() + " points: " + agent.getPoints());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
