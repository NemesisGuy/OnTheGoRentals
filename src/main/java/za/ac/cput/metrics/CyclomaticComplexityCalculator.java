package za.ac.cput.metrics;

public class CyclomaticComplexityCalculator {
    public int calculate(String methodBody) {
        // Start with a base complexity of 1 for the method itself
        int complexity = 1;

        // Extend the list of decision keywords to include more control structures
        String[] decisionKeywords = {"if", "else if", "else", "case", "while", "for", "do", "catch", "&&", "||", "?", ":", "return", "throw", "try", "finally"};

        for (String keyword : decisionKeywords) {
            complexity += countOccurrences(methodBody, keyword);
        }

        return complexity;
    }

    private int countOccurrences(String text, String keyword) {
        int count = 0;
        int index = 0;

        while ((index = text.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }
}
