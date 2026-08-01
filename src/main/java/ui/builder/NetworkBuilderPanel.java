package ui.builder;

import core.graph.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

public class NetworkBuilderPanel extends JPanel {
    private final NetworkGraph graph;
    private final DeviceForm deviceForm = new DeviceForm();
    private String edgeSourceId = null;

    public NetworkBuilderPanel(NetworkGraph graph) {
        this.graph = graph;
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addDeviceBtn = new JButton("Add Device");
        top.add(deviceForm);
        top.add(addDeviceBtn);
        add(top, BorderLayout.NORTH);

        CanvasPanel canvas = new CanvasPanel();
        canvas.setPreferredSize(new Dimension(700, 450));
        canvas.setBackground(Color.WHITE);
        add(canvas, BorderLayout.CENTER);

        addDeviceBtn.addActionListener(e -> {
            if (!deviceForm.isValid()) {
                JOptionPane.showMessageDialog(this, "Device ID is required.");
                return;
            }
            Node node = new Node(deviceForm.getDeviceId(), deviceForm.getDeviceLabel(), 60, 60);
            graph.addNode(node);
            deviceForm.clear();
            canvas.repaint();
        });

        canvas.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                String clicked = hitTestNode(e.getX(), e.getY());
                if (clicked == null) return;
                if (edgeSourceId == null) {
                    edgeSourceId = clicked;
                } else {
                    if (!edgeSourceId.equals(clicked)) {
                        graph.addEdge(new Edge(edgeSourceId, clicked));
                    }
                    edgeSourceId = null;
                }
                canvas.repaint();
            }
        });
    }

    private String hitTestNode(int x, int y) {
        for (Node n : graph.getNodes()) {
            if (Math.hypot(n.x - x, n.y - y) <= 20) return n.id;
        }
        return null;
    }

    public NetworkGraph getGraph() { return graph; }

    private class CanvasPanel extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (Map.Entry<String, List<String>> entry : graph.getAdjacencyList().entrySet()) {
                Node a = findNode(entry.getKey());
                if (a == null) continue;
                for (String neighborId : entry.getValue()) {
                    Node b = findNode(neighborId);
                    if (b == null) continue;
                    g2.setColor(Color.GRAY);
                    g2.drawLine(a.x, a.y, b.x, b.y);
                }
            }

            for (Node n : graph.getNodes()) {
                g2.setColor(n.id.equals(edgeSourceId) ? Color.ORANGE : new Color(70, 130, 180));
                g2.fillOval(n.x - 18, n.y - 18, 36, 36);
                g2.setColor(Color.BLACK);
                g2.drawOval(n.x - 18, n.y - 18, 36, 36);
                g2.drawString(n.label == null || n.label.isEmpty() ? n.id : n.label,
                        n.x - 15, n.y + 30);
            }
        }

        private Node findNode(String id) {
            for (Node n : graph.getNodes()) if (n.id.equals(id)) return n;
            return null;
        }
    }
}