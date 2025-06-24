package za.ac.cput.metrics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CyclomaticComplexityCalculator {
    public int calculate(String methodBody) {
        // Start with a base complexity of 1 for the method itself
        int complexity = 1;

        // Refined list of decision keywords.
        // "else if" is removed as "if" and "else" cover this.
        // ":" is part of "case" or "?:", which are included.
        // "try" and "finally" usually don't add to McCabe CC. "catch" does.
        // "default" (for switch) can be considered a path.
        String[] decisionKeywords = {
                "if", "else", "case", "default", "while", "for", "do", "catch",
                "&&", "||", "?", "return", "throw"
        };

        // Remove comments from method body before counting keywords to avoid false positives
        String bodyWithoutComments = removeComments(methodBody);

        for (String keyword : decisionKeywords) {
            complexity += countOccurrences(bodyWithoutComments, keyword);
        }

        return complexity;
    }

    private String removeComments(String code) {
        // Remove block comments
        String noBlockComments = code.replaceAll("/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/", "");
        // Remove line comments
        String noLineComments = noBlockComments.replaceAll("//.*", "");
        return noLineComments;
    }

    private int countOccurrences(String text, String keyword) {
        int count = 0;
        Pattern pattern;

        // For operators like "&&", "||", "?", word boundaries \b are not suitable.
        // We need to ensure they are not part of a larger operator or identifier.
        // For alphanumeric keywords, \b is good.
        if (keyword.matches("[a-zA-Z0-9]+")) { // Alphanumeric keywords
            pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b");
        } else { // Operators or other special character sequences
            pattern = Pattern.compile(Pattern.quote(keyword));
        }

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}