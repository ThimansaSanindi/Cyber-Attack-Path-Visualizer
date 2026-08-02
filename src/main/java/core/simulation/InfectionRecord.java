package core.simulation;


public class InfectionRecord {
    public String nodeId;
    public int step;

    public InfectionRecord() {
    }

    public InfectionRecord(String nodeId, int step) {
        this.nodeId = nodeId;
        this.step = step;
    }

    @Override
    public String toString() {
        return "InfectionRecord{nodeId='" + nodeId + "', step=" + step + "}";
    }
}