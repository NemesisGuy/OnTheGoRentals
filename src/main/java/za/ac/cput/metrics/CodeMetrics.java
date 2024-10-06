package za.ac.cput.metrics;

public class CodeMetrics {
    private int classCount;
    private int interfaceCount;

    public void incrementClassCount() {
        classCount++;
    }

    public void incrementInterfaceCount() {
        interfaceCount++;
    }

    public int getClassCount() {
        return classCount;
    }

    public int getInterfaceCount() {
        return interfaceCount;
    }
}
