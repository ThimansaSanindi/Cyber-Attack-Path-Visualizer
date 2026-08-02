package core.simulation;

import core.graph.NetworkGraph;

public interface AttackSimulator {
    SimulationResult simulate(NetworkGraph graph, String startNodeId);
}