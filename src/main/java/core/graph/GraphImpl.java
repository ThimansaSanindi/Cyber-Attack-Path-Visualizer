package core.graph;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GraphImpl implements NetworkGraph {

    // node id -> Node
    private final Map<String, Node> nodesById = new LinkedHashMap<>();

    // node id -> list of edges starting at that node
    private final Map<String, List<Edge>> adjacency = new LinkedHashMap<>();

    @Override
    public void addNode(Node node) {
        if (node == null || node.id == null || node.id.isEmpty()) {
            throw new IllegalArgumentException("Node and node.id must not be null/empty");
        }
        if (nodesById.containsKey(node.id)) {
            throw new IllegalArgumentException("Duplicate node id: " + node.id);
        }
        nodesById.put(node.id, node);
        adjacency.put(node.id, new ArrayList<>());
    }

    @Override
    public void addEdge(Edge edge) {
        if (edge == null || edge.sourceId == null || edge.targetId == null) {
            throw new IllegalArgumentException("Edge and its sourceId/targetId must not be null");
        }
        if (!nodesById.containsKey(edge.sourceId)) {
            throw new IllegalArgumentException("Unknown sourceId: " + edge.sourceId);
        }
        if (!nodesById.containsKey(edge.targetId)) {
            throw new IllegalArgumentException("Unknown targetId: " + edge.targetId);
        }
        adjacency.get(edge.sourceId).add(edge);
    }

    @Override
    public boolean removeNode(String nodeId) {
        if (nodeId == null || !nodesById.containsKey(nodeId)) {
            return false;
        }
        nodesById.remove(nodeId);
        adjacency.remove(nodeId);

        for (List<Edge> edges : adjacency.values()) {
            edges.removeIf(e -> nodeId.equals(e.targetId));
        }
        return true;
    }

    @Override
    public List<Node> getNodes() {
        return new ArrayList<>(nodesById.values());
    }

    @Override
    public List<String> getNeighbors(String nodeId) {
        List<String> neighbors = new ArrayList<>();
        List<Edge> edges = adjacency.get(nodeId);
        if (edges == null) {
            return neighbors;
        }
        for (Edge e : edges) {
            neighbors.add(e.targetId);
        }
        return neighbors;
    }

    @Override
    public Map<String, List<String>> getAdjacencyList() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String nodeId : adjacency.keySet()) {
            result.put(nodeId, getNeighbors(nodeId));
        }
        return result;
    }
}