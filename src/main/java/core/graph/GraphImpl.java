package core.graph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class GraphImpl implements NetworkGraph {

    private final Map<String, Node> nodesById = new LinkedHashMap<>();
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

    /**
     * Saves the current network (all nodes + all edges) to a JSON file.
     */
    public void saveToFile(String filePath) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        NetworkData data = new NetworkData(getNodes(), collectAllEdges());
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        }
    }

    /**
     * Loads a network from a JSON file, replacing whatever is currently in this graph.
     */
    public void loadFromFile(String filePath) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<NetworkData>() {
            }.getType();
            NetworkData data = gson.fromJson(reader, type);

            nodesById.clear();
            adjacency.clear();

            for (Node node : data.nodes) {
                addNode(node);
            }
            for (Edge edge : data.edges) {
                addEdge(edge);
            }
        }
    }

    private List<Edge> collectAllEdges() {
        List<Edge> all = new ArrayList<>();
        for (List<Edge> edges : adjacency.values()) {
            all.addAll(edges);
        }
        return all;
    }

    /**
     *Helper class to hold the network data for JSON serialization/deserialization.
     */
    private static class NetworkData {
        List<Node> nodes;
        List<Edge> edges;

        NetworkData(List<Node> nodes, List<Edge> edges) {
            this.nodes = nodes;
            this.edges = edges;
        }
    }
}