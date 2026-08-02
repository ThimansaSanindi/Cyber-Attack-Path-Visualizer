package core.simulation;

import core.graph.Edge;
import core.graph.GraphImpl;
import core.graph.Node;


public class SimulationTestRunner {

    public static void main(String[] args) {
        System.out.println("=== Test 1: connected network, BFS from PC1 ===");
        testConnectedNetwork();

        System.out.println();
        System.out.println("=== Test 2: isolated/disconnected node ===");
        testDisconnectedNode();

        System.out.println();
        System.out.println("=== Test 3: single node (no edges) ===");
        testSingleNode();

        System.out.println();
        System.out.println("=== Test 4: cyclic graph (should not infinite-loop) ===");
        testCyclicGraph();
    }

    private static void testConnectedNetwork() {
        GraphImpl graph = new GraphImpl();
        graph.addNode(new Node("PC1", "Reception PC", 0, 0));
        graph.addNode(new Node("PC2", "Finance PC", 100, 0));
        graph.addNode(new Node("SRV1", "File Server", 200, 0));
        graph.addNode(new Node("SRV2", "DB Server", 300, 0));
        graph.addNode(new Node("PC3", "HR PC", 400, 0));

        graph.addEdge(new Edge("PC1", "PC2"));
        graph.addEdge(new Edge("PC2", "SRV1"));
        graph.addEdge(new Edge("SRV1", "SRV2"));
        graph.addEdge(new Edge("SRV1", "PC3"));

        printResult(new AttackSimulatorImpl().simulate(graph, "PC1"));
    }

    private static void testDisconnectedNode() {
        GraphImpl graph = new GraphImpl();
        graph.addNode(new Node("A", "Node A", 0, 0));
        graph.addNode(new Node("B", "Node B", 100, 0));
        graph.addNode(new Node("ISOLATED", "Isolated Node", 200, 0));

        graph.addEdge(new Edge("A", "B"));
        

        printResult(new AttackSimulatorImpl().simulate(graph, "A"));
    }

    private static void testSingleNode() {
        GraphImpl graph = new GraphImpl();
        graph.addNode(new Node("ONLY", "Only Node", 0, 0));

        printResult(new AttackSimulatorImpl().simulate(graph, "ONLY"));
    }

    private static void testCyclicGraph() {
        GraphImpl graph = new GraphImpl();
        graph.addNode(new Node("X", "Node X", 0, 0));
        graph.addNode(new Node("Y", "Node Y", 100, 0));
        graph.addNode(new Node("Z", "Node Z", 200, 0));

        graph.addEdge(new Edge("X", "Y"));
        graph.addEdge(new Edge("Y", "Z"));
        graph.addEdge(new Edge("Z", "X")); 

        printResult(new AttackSimulatorImpl().simulate(graph, "X"));
    }

    private static void printResult(SimulationResult result) {
        System.out.println("Infection order: " + result.infectionOrder);
        System.out.println("Infection step:  " + result.infectionStep);
        System.out.println("Most affected:   " + result.mostAffectedNodeId);
        System.out.println("Risk scores:     " + result.riskScoreByNode);
    }
}