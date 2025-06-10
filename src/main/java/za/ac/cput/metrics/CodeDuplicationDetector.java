package za.ac.cput.metrics;

import java.util.HashSet;
import java.util.Set;

public class CodeDuplicationDetector {

    public int detectDuplicates(String fileContent) {
        String[] lines = fileContent.split("\\r?\\n"); // Split content into lines
        Set<String> uniqueLines = new HashSet<>(); // To track unique lines
        Set<String> duplicatedLineValues = new HashSet<>(); // To track the distinct values of duplicated lines

        for (String line : lines) {
            String trimmedLine = line.trim(); // Remove leading and trailing whitespace

            if (trimmedLine.isEmpty()) {
                continue; // Skip empty lines
            }

            // Normalize multiple spaces/tabs to a single space for more robust duplication detection
            // String normalizedLine = trimmedLine.replaceAll("\\s+", " "); // Optional: can make it too aggressive

            if (!uniqueLines.add(trimmedLine)) {
                // If add returns false, it means the line was already in uniqueLines, so it's a duplicate
                duplicatedLineValues.add(trimmedLine);
            }
        }
        // This returns the count of *unique lines that are duplicated*.
        // e.g., if "foo();" appears 3 times, it adds 1 to this count.
        return duplicatedLineValues.size();
    }
}