import enums.Direction;
import enums.MessageType;
import enums.OperationType;

import java.util.*;
import java.util.concurrent.*;

public class Agent implements Runnable {
    private int id, x, y, points;
    private String color;
    private Tile carriedTile;
    private Environment environment;
    private BlockingQueue<Message> messageQueue;

    public Agent(int id, int x, int y, String color, Environment environment) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.color = color;
        this.environment = environment;
        this.carriedTile = null;
        this.points = 0;
        this.messageQueue = new LinkedBlockingQueue<>();
    }

    public void run() {
        Random random = new Random();
        while (true) {
            try {
                // Example operation sequence (can be improved with actual logic)
                // 1. Move to a random direction
                Direction direction = Direction.values()[random.nextInt(Direction.values().length)];
                environment.submitOperation(new Operation(id, OperationType.MOVE, direction));

                // 2. Try to pick a tile of the agent's color
                environment.submitOperation(new Operation(id, OperationType.PICK, color));

                // 3. Try to use the carried tile in a random direction
                direction = Direction.values()[random.nextInt(Direction.values().length)];
                environment.submitOperation(new Operation(id, OperationType.USE_TILE, direction));

                // 4. Handle messages
                handleMessages();

                // Sleep for some time to simulate agent thinking/acting
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void handleMessages() throws InterruptedException {
        Message message = messageQueue.poll();
        if (message != null) {
            switch (message.getType()) {
                case REQUEST_TASK:
                    handleTaskRequest(message);
                    break;
                case OFFER_TASK:
                    handleTaskOffer(message);
                    break;
                case NEGOTIATE_POINTS:
                    handlePointNegotiation(message);
                    break;
                case CONFIRM_TASK:
                    handleTaskConfirmation(message);
                    break;
                case REJECT_TASK:
                    handleTaskRejection(message);
                    break;
            }
        }
    }

    private void handleTaskRequest(Message message) {
        // Handle task request from another agent
        // Example: Offer to perform the task for some points
        int taskPoints = 10; // Example points for task
        sendMessage(new Message(id, message.getSenderId(), MessageType.OFFER_TASK, "Task Offer", taskPoints));
    }

    private void handleTaskOffer(Message message) {
        // Handle task offer from another agent
        // Example: Accept the task and confirm
        sendMessage(new Message(id, message.getSenderId(), MessageType.CONFIRM_TASK, "Task Confirmed", message.getPoints()));
    }

    private void handlePointNegotiation(Message message) {
        System.out.println("Started Point Negotiation");
        environment.submitOperation(new Operation(id, OperationType.TRANSFER_POINTS, message.getSenderId(), message.getPoints()));
    }

    private void handleTaskConfirmation(Message message) {
        System.out.println("Task Confirmation");
    }

    private void handleTaskRejection(Message message) {
        System.out.println("Task Rejected");
    }

    public void sendMessage(Message message) {
        Agent receiver = environment.getAgentById(message.getReceiverId());
        receiver.receiveMessage(message);
    }

    public void receiveMessage(Message message) {
        messageQueue.offer(message);
    }

    public void notifyOperationSuccess() {
        System.out.println("Great success. The operation succeeded");
    }

    public void notifyOperationFailure() {

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getColor() {
        return color;
    }

    public Tile getCarriedTile() {
        return carriedTile;
    }

    public void setCarriedTile(Tile carriedTile) {
        this.carriedTile = carriedTile;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void subtractPoints(int points) {
        this.points -= points;
    }

    public int getId() {
        return id;
    }
}
