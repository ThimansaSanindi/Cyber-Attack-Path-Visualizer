import core.graph.GraphImpl;
import core.graph.NetworkGraph;
import core.graph.Node;
import core.simulation.AttackSimulator;
import core.simulation.AttackSimulatorImpl;
import core.simulation.SimulationResult;
import ui.builder.NetworkBuilderPanel;
import ui.playback.AttackPlaybackPanel;
import ui.playback.StatsPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class Main {

    private static JTabbedPane tabs;
    private static JPanel playbackTab;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGui);
    }

    private static void createAndShowGui() {
        JFrame frame = new JFrame("Cyber Attack Path Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        NetworkGraph graph = new GraphImpl();
        NetworkBuilderPanel builderPanel = new NetworkBuilderPanel(graph);

        tabs = new JTabbedPane();
        tabs.addTab("Build Network", wrapWithRunButton(builderPanel));

        playbackTab = new JPanel(new BorderLayout());
        playbackTab.add(
            new JLabel("Run a simulation from the Build Network tab to see playback & stats.",
                SwingConstants.CENTER),
            BorderLayout.CENTER
        );
        tabs.addTab("Playback & Stats", playbackTab);

        frame.add(tabs, BorderLayout.CENTER);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JComponent wrapWithRunButton(NetworkBuilderPanel builder) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(builder, BorderLayout.CENTER);

        JPanel runBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton runBtn = new JButton("Run Simulation");
        runBtn.addActionListener(e -> runSimulation(builder));
        runBar.add(runBtn);
        wrapper.add(runBar, BorderLayout.SOUTH);

        return wrapper;
    }

    private static void runSimulation(NetworkBuilderPanel builder) {
        NetworkGraph graph = builder.getGraph();
        String startNodeId = builder.getSelectedStartNode();

        if (startNodeId == null || startNodeId.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Pick a start node in the builder tab first.",
                "No start node selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Node> nodes = graph.getNodes();
        if (nodes.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Add at least one device before running the simulation.",
                "Empty network", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AttackSimulator simulator = new AttackSimulatorImpl();
        SimulationResult result;
        try {
            result = simulator.simulate(graph, startNodeId);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                "Simulation error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        playbackTab.removeAll();
        playbackTab.add(new AttackPlaybackPanel(nodes, result), BorderLayout.CENTER);
        playbackTab.add(new StatsPanel(result), BorderLayout.EAST);
        playbackTab.revalidate();
        playbackTab.repaint();

        tabs.setSelectedIndex(1);
    }
}