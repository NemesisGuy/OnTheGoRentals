package za.ac.cput.metrics;

public class MaintainabilityIndexCalculator {

    public double calculateMaintainabilityIndex(int fileCodeLines, int cyclomaticComplexity, int fileCommentLines) {
        if (fileCodeLines <= 0) { // Changed to <=0 to handle zero or negative (though unlikely)
            return 0; // Or return a predetermined value, e.g., 0 or 100 if no code
        }
        if (fileCommentLines < 0) fileCommentLines = 0; // Ensure non-negative comments

        // Ensure cyclomaticComplexity is at least 1 for meaningful calculation if there's code
        // However, if CC is genuinely 0 (e.g. empty file or file with only comments), formula might behave unexpectedly.
        // The formula uses log(fileCodeLines), so fileCodeLines > 0 is critical.
        // log(fileCommentLines + 1) handles 0 comments.

        double logFileCodeLines = Math.log(Math.max(1, fileCodeLines)); // Avoid log(0) or log(<1) issues
        double logFileCommentLines = Math.log(Math.max(1, fileCommentLines + 1.0)); // Avoid log(0)

        // Example formula for Maintainability Index
        // MI = 171 - 5.2 * ln(V) - 0.23 * G - 16.2 * ln(L)
        // Where V = Halstead Volume (approximated by fileCodeLines here), G = Cyclomatic Complexity, L = Lines of Comment
        // For simplicity, we are using fileCodeLines as a proxy for volume.
        double maintainabilityIndex = 171 - (5.2 * logFileCodeLines) - (0.23 * cyclomaticComplexity) - (16.2 * logFileCommentLines);

        // Cap the value between 0 and 100
        return Math.min(100, Math.max(0, maintainabilityIndex));
    }
}