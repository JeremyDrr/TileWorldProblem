import enums.Direction;
import enums.OperationType;

public class Operation {
    private int agentId;
    private OperationType type;
    private Direction direction;
    private String color;
    private int targetAgentId;
    private int points;

    public Operation(int agentId, OperationType type, Direction direction) {
        this.agentId = agentId;
        this.type = type;
        this.direction = direction;
    }

    public Operation(int agentId, OperationType type, String color) {
        this.agentId = agentId;
        this.type = type;
        this.color = color;
    }

    public Operation(int agentId, OperationType type, int targetAgentId, int points) {
        this.agentId = agentId;
        this.type = type;
        this.targetAgentId = targetAgentId;
        this.points = points;
    }

    public int getAgentId() {
        return agentId;
    }

    public OperationType getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getColor() {
        return color;
    }

    public int getTargetAgentId() {
        return targetAgentId;
    }

    public int getPoints() {
        return points;
    }
}
