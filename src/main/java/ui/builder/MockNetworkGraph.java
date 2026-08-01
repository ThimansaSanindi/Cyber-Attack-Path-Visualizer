package ui.builder;

import core.graph.*;
import java.util.*;

public class MockNetworkGraph implements NetworkGraph {
    private final Map<String, Node> nodes = new LinkedHashMap<>();
    private final List<Edge> edges = new ArrayList<>();

    @Override public void addNode(Node node) { nodes.put(node.id, node); }

    @Override public void addEdge(Edge edge) { edges.add(edge); }

    @Override public boolean removeNode(String nodeId) {
        edges.removeIf(e -> e.sourceId.equals(nodeId) || e.targetId.equals(nodeId));
        return nodes.remove(nodeId) != null;
    }

    @Override public List<Node> getNodes() { return new ArrayList<>(nodes.values()); }

    @Override public List<String> getNeighbors(String nodeId) {
        List<String> result = new ArrayList<>();
        for (Edge e : edges) {
            if (e.sourceId.equals(nodeId)) result.add(e.targetId);
            if (e.targetId.equals(nodeId)) result.add(e.sourceId);
        }
        return result;
    }

    @Override public Map<String, List<String>> getAdjacencyList() {
        Map<String, List<String>> adj = new LinkedHashMap<>();
        for (String id : nodes.keySet()) adj.put(id, getNeighbors(id));
        return adj;
    }
}