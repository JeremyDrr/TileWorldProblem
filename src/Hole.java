public class Hole {
    private int depth;
    private String color;
    private int x, y;

    public Hole(int depth, String color, int x, int y) {
        this.depth = depth;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
