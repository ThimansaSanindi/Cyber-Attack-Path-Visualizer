package ui.playback;

import core.graph.Node;
import core.simulation.SimulationResult;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AttackPlaybackPanel extends JPanel {

    private final List<Node> nodes;
    private final SimulationResult result;

    private int currentStep = 0;
    private int maxStep = 0;

    private final Timer timer;
    private boolean playing = false;

    private final JButton playPauseButton = new JButton("Play");
    private final JButton stepButton = new JButton("Step");
    private final JLabel stepLabel = new JLabel("Step: 0");
    private JPanel canvasRef;

    public AttackPlaybackPanel(List<Node> nodes, SimulationResult result) {
        this.nodes = nodes;
        this.result = result;

        for (int step : result.infectionStep.values()) {
            if (step > maxStep) maxStep = step;
        }

        setLayout(new BorderLayout());

        JPanel canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawNodes(g);
            }
        };
        canvas.setPreferredSize(new Dimension(450, 260));
        canvas.setBackground(Color.WHITE);
        add(canvas, BorderLayout.CENTER);
        this.canvasRef = canvas;

        JPanel controls = new JPanel();
        controls.add(playPauseButton);
        controls.add(stepButton);
        controls.add(stepLabel);
        add(controls, BorderLayout.SOUTH);

        timer = new Timer(800, e -> advanceStep());

        playPauseButton.addActionListener(e -> togglePlay());
        stepButton.addActionListener(e -> {
            if (playing) togglePlay();
            advanceStep();
        });
    }

    private void togglePlay() {
        playing = !playing;
        if (playing) {
            if (currentStep >= maxStep) currentStep = 0;
            timer.start();
            playPauseButton.setText("Pause");
        } else {
            timer.stop();
            playPauseButton.setText("Play");
        }
    }

    private void advanceStep() {
        if (currentStep >= maxStep) {
            timer.stop();
            playing = false;
            playPauseButton.setText("Play");
            return;
        }
        currentStep++;
        stepLabel.setText("Step: " + currentStep);
        canvasRef.repaint();
    }

    private void drawNodes(Graphics g) {
        for (Node node : nodes) {
            boolean infected = isInfectedByCurrentStep(node.id);
            g.setColor(infected ? new Color(200, 40, 40) : new Color(60, 160, 60));
            g.fillOval(node.x, node.y, 40, 40);
            g.setColor(Color.BLACK);
            g.drawOval(node.x, node.y, 40, 40);
            g.drawString(node.label, node.x, node.y - 5);
        }
    }

    private boolean isInfectedByCurrentStep(String nodeId) {
        Integer step = result.infectionStep.get(nodeId);
        return step != null && step <= currentStep;
    }
}