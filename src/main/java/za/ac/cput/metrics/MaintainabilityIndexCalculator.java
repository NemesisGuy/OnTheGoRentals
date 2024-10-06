package za.ac.cput.metrics;

public class MaintainabilityIndexCalculator {

    public double calculateMaintainabilityIndex(int fileCodeLines, int cyclomaticComplexity, int fileCommentLines) {
        if (fileCodeLines == 0) {
            return 0; // Or return a predetermined value, e.g., 0
        }

        // Example formula for Maintainability Index
        double maintainabilityIndex = 171 - (5.2 * Math.log(fileCodeLines)) - (0.23 * cyclomaticComplexity) - (16.2 * Math.log(fileCommentLines + 1));

        // Cap the value between 0 and 100
        return Math.min(100, Math.max(0, maintainabilityIndex));
    }


}
