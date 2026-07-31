package core.simulation;

import java.util.List;
import java.util.Map;

public class SimulationResult {
    public List<String> infectionOrder;
    public Map<String, Integer> infectionStep;
    public String mostAffectedNodeId;
    public Map<String, Double> riskScoreByNode;

    public SimulationResult() {
    }

    public SimulationResult(List<String> infectionOrder,
                             Map<String, Integer> infectionStep,
                             String mostAffectedNodeId,
                             Map<String, Double> riskScoreByNode) {
        this.infectionOrder = infectionOrder;
        this.infectionStep = infectionStep;
        this.mostAffectedNodeId = mostAffectedNodeId;
        this.riskScoreByNode = riskScoreByNode;
    }
}