package za.ac.cput.metrics;

// This class appears unused in the current setup.
// If it's intended for future use, it can remain. Otherwise, it could be removed.
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