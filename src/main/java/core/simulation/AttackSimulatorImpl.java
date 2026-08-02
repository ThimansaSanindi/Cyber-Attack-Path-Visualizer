package core.simulation;

import core.graph.NetworkGraph;
import core.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


public class AttackSimulatorImpl implements AttackSimulator {

    private static final double CONNECTIVITY_WEIGHT = 0.4;
    private static final double IMPACT_WEIGHT = 0.6;

    @Override
    public SimulationResult simulate(NetworkGraph graph, String startNodeId) {
        if (graph == null) {
            throw new IllegalArgumentException("graph must not be null");
        }
        if (startNodeId == null || startNodeId.isEmpty()) {
            throw new IllegalArgumentException("startNodeId must not be null/empty");
        }

        List<String> allNodeIds = new ArrayList<>();
        for (Node n : graph.getNodes()) {
            allNodeIds.add(n.id);
        }
        if (!allNodeIds.contains(startNodeId)) {
            throw new IllegalArgumentException("startNodeId not found in graph: " + startNodeId);
        }

        List<InfectionRecord> infectionRecords = runBfs(graph, startNodeId);

        List<String> infectionOrder = new ArrayList<>();
        Map<String, Integer> infectionStep = new LinkedHashMap<>();
        for (InfectionRecord rec : infectionRecords) {
            infectionOrder.add(rec.nodeId);
            infectionStep.put(rec.nodeId, rec.step);
        }

        Map<String, Double> riskScoreByNode = computeRiskScores(graph, allNodeIds);
        String mostAffectedNodeId = findHighestRisk(riskScoreByNode);

        return new SimulationResult(infectionOrder, infectionStep, mostAffectedNodeId, riskScoreByNode);
    }

    
    private List<InfectionRecord> runBfs(NetworkGraph graph, String startNodeId) {
        List<InfectionRecord> records = new ArrayList<>();
        Set<String> visited = new LinkedHashSet<>();
        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> stepOf = new HashMap<>();

        queue.add(startNodeId);
        visited.add(startNodeId);
        stepOf.put(startNodeId, 0);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentStep = stepOf.get(current);
            records.add(new InfectionRecord(current, currentStep));

            for (String neighbor : graph.getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    stepOf.put(neighbor, currentStep + 1);
                    queue.add(neighbor);
                }
            }
        }
        return records;
    }

   
    private Map<String, Double> computeRiskScores(NetworkGraph graph, List<String> allNodeIds) {
        Map<String, Double> scores = new LinkedHashMap<>();
        if (allNodeIds.isEmpty()) {
            return scores;
        }

        Map<String, Integer> degree = new HashMap<>();
        Map<String, Integer> reach = new HashMap<>();
        int maxDegree = 0;
        int maxReach = 0;

        for (String id : allNodeIds) {
            int d = graph.getNeighbors(id).size();
            degree.put(id, d);
            maxDegree = Math.max(maxDegree, d);

            int r = reachableCount(graph, id);
            reach.put(id, r);
            maxReach = Math.max(maxReach, r);
        }

        for (String id : allNodeIds) {
            double normDegree = maxDegree == 0 ? 0.0 : (double) degree.get(id) / maxDegree;
            double normReach = maxReach == 0 ? 0.0 : (double) reach.get(id) / maxReach;
            double score = (CONNECTIVITY_WEIGHT * normDegree) + (IMPACT_WEIGHT * normReach);
            scores.put(id, round2(score));
        }
        return scores;
    }

  
    private int reachableCount(NetworkGraph graph, String fromId) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(fromId);
        visited.add(fromId);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (String neighbor : graph.getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        visited.remove(fromId);
        return visited.size();
    }

    private String findHighestRisk(Map<String, Double> riskScoreByNode) {
        String best = null;
        double bestScore = -1.0;
        for (Map.Entry<String, Double> entry : riskScoreByNode.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestScore = entry.getValue();
                best = entry.getKey();
            }
        }
        return best;
    }

    private double round2(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}