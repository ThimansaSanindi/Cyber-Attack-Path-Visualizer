package ui.builder;

import core.graph.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Deque;
import java.util.ArrayDeque;

public class NetworkBuilderPanel extends JPanel {

    private final NetworkGraph graph;
    private final DeviceForm deviceForm = new DeviceForm();
    private final JComboBox<String> startNodeSelector = new JComboBox<>();

    private String edgeSourceId = null;
    private String selectedNodeId = null;

    
    private boolean waitingForPlacement = false;
    private String pendingId;
    private String pendingLabel;

    
    private final Set<String> nodeIds = new HashSet<>();

     
    private final Deque<String> recentlyAdded = new ArrayDeque<>();

    public NetworkBuilderPanel(NetworkGraph graph) {
        this.graph = graph;
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addDeviceBtn = new JButton("Add Device");
        JButton saveBtn = new JButton("Save Network");
        JButton undoBtn = new JButton("Undo Last Device");

        top.add(deviceForm);
        top.add(addDeviceBtn);
        top.add(new JLabel("Start node:"));
        top.add(startNodeSelector);
        top.add(saveBtn);
        top.add(undoBtn);

        add(top, BorderLayout.NORTH);

        CanvasPanel canvas = new CanvasPanel();
        canvas.setPreferredSize(new Dimension(700, 450));
        canvas.setBackground(Color.WHITE);
        add(canvas, BorderLayout.CENTER);

        
        addDeviceBtn.addActionListener(e -> {

            if (!deviceForm.isValid()) {
                JOptionPane.showMessageDialog(this,
                        "Device ID is required.");
                return;
            }

            String candidateId = deviceForm.getDeviceId();

            
            if (nodeIds.contains(candidateId)) {
                JOptionPane.showMessageDialog(this,
                        "A device with ID \"" + candidateId + "\" already exists. Choose a different ID.");
                return;
            }

            pendingId = candidateId;
            pendingLabel = deviceForm.getDeviceLabel();

            waitingForPlacement = true;

            JOptionPane.showMessageDialog(
                    this,
                    "Click anywhere on the canvas to place the device."
            );

            deviceForm.clear();
        });

        saveBtn.addActionListener(e -> onSave());

        undoBtn.addActionListener(e -> undoLastDevice());

        // Mouse Clicks
        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                
                if (waitingForPlacement) {

                    Node node = new Node(
                            pendingId,
                            pendingLabel,
                            e.getX(),
                            e.getY()
                    );

                    graph.addNode(node);

                    
                    nodeIds.add(pendingId);
                    recentlyAdded.push(pendingId);

                    refreshStartNodeSelector();

                    waitingForPlacement = false;
                    pendingId = null;
                    pendingLabel = null;

                    canvas.repaint();
                    return;
                }

                
                String clicked = hitTestNode(e.getX(), e.getY());

                if (clicked == null)
                    return;

                if (edgeSourceId == null) {

                    edgeSourceId = clicked;
                    selectedNodeId = clicked;

                } else {

                    if (!edgeSourceId.equals(clicked)) {
                        graph.addEdge(new Edge(edgeSourceId, clicked));
                    }

                    edgeSourceId = null;
                    selectedNodeId = null;
                }

                canvas.repaint();
            }
        });

        
        canvas.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                if (selectedNodeId != null && edgeSourceId == null) {

                    moveNode(selectedNodeId, e.getX(), e.getY());

                    canvas.repaint();
                }
            }
        });
    }

    private String hitTestNode(int x, int y) {

        for (Node n : graph.getNodes()) {

            if (Math.hypot(n.x - x, n.y - y) <= 20)
                return n.id;
        }

        return null;
    }

    private void moveNode(String id, int x, int y) {

        for (Node n : graph.getNodes()) {

            if (n.id.equals(id)) {
                n.x = x;
                n.y = y;
            }
        }
    }

    private void refreshStartNodeSelector() {

        startNodeSelector.removeAllItems();

        for (Node n : graph.getNodes()) {
            startNodeSelector.addItem(n.id);
        }
    }

    public String getSelectedStartNode() {
        return (String) startNodeSelector.getSelectedItem();
    }

    public NetworkGraph getGraph() {
        return graph;
    }

    private void onSave() {

        JOptionPane.showMessageDialog(
                this,
                "Network saved: " + graph.getNodes().size() + " nodes."
        );
    }

    
    private void undoLastDevice() {

        if (recentlyAdded.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nothing to undo.");
            return;
        }

        String lastId = recentlyAdded.pop();
        graph.removeNode(lastId);
        nodeIds.remove(lastId);

        refreshStartNodeSelector();
        repaint();
    }

    private class CanvasPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            
            for (Map.Entry<String, List<String>> entry :
                    graph.getAdjacencyList().entrySet()) {

                Node a = findNode(entry.getKey());

                if (a == null)
                    continue;

                for (String neighborId : entry.getValue()) {

                    Node b = findNode(neighborId);

                    if (b == null)
                        continue;

                    g2.setColor(Color.GRAY);
                    g2.drawLine(a.x, a.y, b.x, b.y);
                }
            }

            
            for (Node n : graph.getNodes()) {

                g2.setColor(
                        n.id.equals(edgeSourceId)
                                ? Color.ORANGE
                                : new Color(70,130,180)
                );

                g2.fillOval(n.x - 18, n.y - 18, 36, 36);

                g2.setColor(Color.BLACK);
                g2.drawOval(n.x - 18, n.y - 18, 36, 36);

                g2.drawString(
                        n.label == null || n.label.isEmpty()
                                ? n.id
                                : n.label,
                        n.x - 15,
                        n.y + 30
                );
            }
        }

        private Node findNode(String id) {

            for (Node n : graph.getNodes()) {

                if (n.id.equals(id))
                    return n;
            }

            return null;
        }
    }
}