package ui.playback;

import core.graph.Node;
import core.simulation.SimulationResult;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Node> nodes = MockDataFactory.mockNodes();
        SimulationResult result = MockDataFactory.mockSimulationResult();

        JFrame frame = new JFrame("Playback Module Test - Ashroff");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(new AttackPlaybackPanel(nodes, result), BorderLayout.CENTER);
        frame.add(new StatsPanel(result), BorderLayout.EAST);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}