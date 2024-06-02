import enums.MessageType;

public class Message {
    private int senderId;
    private int receiverId;
    private MessageType type;
    private String content;
    private int points;

    public Message(int senderId, int receiverId, MessageType type, String content, int points) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.content = content;
        this.points = points;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public MessageType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public int getPoints() {
        return points;
    }
}
