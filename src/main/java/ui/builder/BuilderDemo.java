package ui.builder;

import javax.swing.*;
import core.graph.GraphImpl;

public class BuilderDemo {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Network Builder - Standalone Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new NetworkBuilderPanel(new GraphImpl()));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}