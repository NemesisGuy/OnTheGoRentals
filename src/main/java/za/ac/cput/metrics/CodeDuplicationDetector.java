package za.ac.cput.metrics;

import java.util.HashSet;
import java.util.Set;

public class CodeDuplicationDetector {

    public int detectDuplicates(String fileContent) {
        String[] lines = fileContent.split("\\r?\\n"); // Split content into lines
        Set<String> uniqueLines = new HashSet<>(); // To track unique lines
        Set<String> duplicateLines = new HashSet<>(); // To track duplicates

        for (String line : lines) {
            line = line.trim(); // Remove leading and trailing whitespace

            if (line.isEmpty()) {
                continue; // Skip empty lines
            }

            // Check if the line already exists in uniqueLines
            if (!uniqueLines.add(line)) {
                duplicateLines.add(line); // Add to duplicateLines if it's already in uniqueLines
            }
        }

        return duplicateLines.size(); // Return the count of unique duplicate lines
    }
}
