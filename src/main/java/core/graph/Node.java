package core.graph;

public class Node {
    public String id;
    public String label;
    public int x;
    public int y;

    public Node() {
    }

    public Node(String id, String label, int x, int y) {
        this.id = id;
        this.label = label;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Node{id='" + id + "', label='" + label + "', x=" + x + ", y=" + y + "}";
    }
}