package ui.playback;

import core.simulation.SimulationResult;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Comparator;

public class StatsPanel extends JPanel {

    public StatsPanel(SimulationResult result) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Attack Statistics"));

        int totalSteps = maxStep(result);
        int totalNodes = result.infectionStep.size();

        add(new JLabel("Infection Order: " + String.join(" → ", result.infectionOrder)));
        add(new JLabel("Total Steps: " + totalSteps));
        add(new JLabel("Most Affected Node: " + result.mostAffectedNodeId));
        add(new JLabel(" "));

        add(new JLabel("Infection Spread by Step:"));
        for (int step = 0; step <= totalSteps; step++) {
            int infectedCount = countInfectedByStep(result, step);
            double percent = (infectedCount * 100.0) / totalNodes;
            add(new JLabel(String.format("  Step %d: %d/%d infected (%.0f%%)",
                    step, infectedCount, totalNodes, percent)));
        }
        add(new JLabel(" "));

        add(new JLabel("Nodes Ranked by Risk (highest first):"));
        List<Map.Entry<String, Double>> ranked = new ArrayList<>(result.riskScoreByNode.entrySet());
        ranked.sort(Comparator.comparingDouble((Map.Entry<String, Double> e) -> e.getValue()).reversed());
        int rank = 1;
        for (Map.Entry<String, Double> entry : ranked) {
            add(new JLabel("  " + rank + ". " + entry.getKey() + " — risk " + entry.getValue()));
            rank++;
        }
    }

    private int maxStep(SimulationResult result) {
        int max = 0;
        for (int step : result.infectionStep.values()) {
            if (step > max) max = step;
        }
        return max;
    }

    private int countInfectedByStep(SimulationResult result, int step) {
        int count = 0;
        for (int s : result.infectionStep.values()) {
            if (s <= step) count++;
        }
        return count;
    }
}