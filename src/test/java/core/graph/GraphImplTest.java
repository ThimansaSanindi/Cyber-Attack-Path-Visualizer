package core.graph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

class GraphImplTest {

    private GraphImpl graph;

    @BeforeEach
    void setUp() {
        graph = new GraphImpl();
    }

    @Test
    void addNode_addsSuccessfully() {
        graph.addNode(new Node("A", "Server A", 0, 0));
        assertEquals(1, graph.getNodes().size());
    }

    @Test
    void addNode_duplicateId_throwsException() {
        graph.addNode(new Node("A", "Server A", 0, 0));
        assertThrows(IllegalArgumentException.class, () ->
                graph.addNode(new Node("A", "Duplicate", 5, 5)));
    }

    @Test
    void addEdge_unknownSourceId_throwsException() {
        graph.addNode(new Node("A", "Server A", 0, 0));
        assertThrows(IllegalArgumentException.class, () ->
                graph.addEdge(new Edge("X", "A")));
    }

    @Test
    void getNeighbors_returnsCorrectNeighbors() {
        graph.addNode(new Node("A", "Server A", 0, 0));
        graph.addNode(new Node("B", "Server B", 0, 0));
        graph.addEdge(new Edge("A", "B"));

        List<String> neighbors = graph.getNeighbors("A");
        assertEquals(1, neighbors.size());
        assertEquals("B", neighbors.get(0));
    }

    @Test
    void getNeighbors_isolatedNode_returnsEmptyList() {
        graph.addNode(new Node("C", "Isolated PC", 0, 0));
        assertTrue(graph.getNeighbors("C").isEmpty());
    }

    @Test
    void addEdge_selfLoop_isAllowed() {
        graph.addNode(new Node("A", "Server A", 0, 0));
        graph.addEdge(new Edge("A", "A"));
        assertEquals(List.of("A"), graph.getNeighbors("A"));
    }

    @Test
    void removeNode_removesNodeAndItsIncomingEdges() {
        graph.addNode(new Node("A", "Server A", 0, 0));
        graph.addNode(new Node("B", "Server B", 0, 0));
        graph.addEdge(new Edge("A", "B"));

        boolean removed = graph.removeNode("B");

        assertTrue(removed);
        assertEquals(1, graph.getNodes().size());
        assertTrue(graph.getNeighbors("A").isEmpty());
    }

    @Test
    void removeNode_unknownId_returnsFalse() {
        assertFalse(graph.removeNode("doesNotExist"));
    }

    @Test
    void getAdjacencyList_reflectsAllNodesAndEdges() {
        graph.addNode(new Node("A", "Server A", 0, 0));
        graph.addNode(new Node("B", "Server B", 0, 0));
        graph.addEdge(new Edge("A", "B"));

        Map<String, List<String>> adj = graph.getAdjacencyList();
        assertEquals(2, adj.size());
        assertEquals(List.of("B"), adj.get("A"));
        assertTrue(adj.get("B").isEmpty());
    }

    @Test
    void saveAndLoadFile_preservesGraphData() throws IOException {
        graph.addNode(new Node("A", "Server A", 0, 0));
        graph.addNode(new Node("B", "Server B", 100, 0));
        graph.addEdge(new Edge("A", "B"));

        String path = "test-output-temp.json";
        graph.saveToFile(path);

        GraphImpl loaded = new GraphImpl();
        loaded.loadFromFile(path);

        assertEquals(2, loaded.getNodes().size());
        assertEquals(List.of("B"), loaded.getNeighbors("A"));

        new File(path).delete(); // cleanup
    }
}