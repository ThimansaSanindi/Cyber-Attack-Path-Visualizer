package ui.playback;

import core.graph.Node;
import core.simulation.SimulationResult;

import java.util.*;

public class MockDataFactory {

    public static List<Node> mockNodes() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(new Node("N1", "Router",   60,  60));
        nodes.add(new Node("N2", "Server A", 200, 60));
        nodes.add(new Node("N3", "Server B", 340, 60));
        nodes.add(new Node("N4", "Laptop 1", 200, 180));
        nodes.add(new Node("N5", "Laptop 2", 340, 180));
        return nodes;
    }

    public static SimulationResult mockSimulationResult() {
        List<String> infectionOrder = List.of("N1", "N2", "N4", "N3", "N5");

        Map<String, Integer> infectionStep = new LinkedHashMap<>();
        infectionStep.put("N1", 0);
        infectionStep.put("N2", 1);
        infectionStep.put("N4", 1);
        infectionStep.put("N3", 2);
        infectionStep.put("N5", 2);

        Map<String, Double> riskScoreByNode = new LinkedHashMap<>();
        riskScoreByNode.put("N1", 0.9);
        riskScoreByNode.put("N2", 0.6);
        riskScoreByNode.put("N3", 0.5);
        riskScoreByNode.put("N4", 0.3);
        riskScoreByNode.put("N5", 0.3);

        String mostAffected = "N1";

        return new SimulationResult(infectionOrder, infectionStep, mostAffected, riskScoreByNode);
    }
}