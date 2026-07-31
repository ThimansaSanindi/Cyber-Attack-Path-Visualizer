package core.graph;

import java.util.List;
import java.util.Map;

public interface NetworkGraph {

    void addNode(Node node);

    void addEdge(Edge edge);

    boolean removeNode(String nodeId);

    List<Node> getNodes();

    List<String> getNeighbors(String nodeId);

    Map<String, List<String>> getAdjacencyList();
}