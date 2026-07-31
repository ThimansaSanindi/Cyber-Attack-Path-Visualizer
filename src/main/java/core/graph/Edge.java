package core.graph;

public class Edge {
    public String sourceId;
    public String targetId;

    public Edge() {
    }

    public Edge(String sourceId, String targetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    @Override
    public String toString() {
        return "Edge{sourceId='" + sourceId + "', targetId='" + targetId + "'}";
    }
}