package za.ac.cput.metrics;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeLineCounter {
    private final StringBuilder markdownContent = new StringBuilder();
    private int totalLines = 0;
    private int totalCodeLines = 0;
    private int totalCommentLines = 0;
    private int totalFunctions = 0;
    private int totalClasses = 0;
    private int totalInterfaces = 0;
    private int fileCount = 0;
    private String baseDirectoryPath;

    // Removed redundant main method, use Main.java as the entry point

    public void countLines(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            System.out.println("The provided path is not a directory: " + directoryPath);
            return;
        }

        baseDirectoryPath = directory.getAbsolutePath();
        markdownContent.append("# Code Metrics Report\n"); // Overall report title
        processDirectory(directory);
    }

    private void processDirectory(File directory) {
        String relativePath = directory.getAbsolutePath().replace(baseDirectoryPath, ".");
        // Use forward slashes for paths in Markdown for better consistency
        relativePath = relativePath.replace(File.separator, "/");
        if (relativePath.isEmpty() || relativePath.equals("/")) relativePath = ".";


        markdownContent.append("\n## Directory: ").append(relativePath).append("\n\n");
        markdownContent.append("| # | File Name | Total Lines | Code Lines | Comment Lines | Function Count | Class Count | Interface Count | Duplicate Lines | Maintainability Index | Avg. Cyclomatic Complexity |\n");
        markdownContent.append("|---|-----------|-------------|------------|---------------|----------------|-------------|-----------------|-----------------|------------------------|--------------------------|\n");

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    processDirectory(file);
                } else if (file.getName().endsWith(".java")) {
                    fileCount++;
                    countFileLines(file);
                }
            }
        }
    }

    private void countFileLines(File file) {
        int fileLines = 0;
        int fileCodeLines = 0;
        int fileCommentLines = 0;
        int fileFunctionCount = 0;
        int fileClassCount = 0;
        int fileInterfaceCount = 0;
        int fileTotalCyclomaticComplexity = 0;

        StringBuilder fileContent = new StringBuilder();
        StringBuilder currentMethodBody = new StringBuilder();
        boolean inMethod = false;
        int methodBraceLevel = 0;
        boolean inAMultiLineComment = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileLines++;
                totalLines++;
                fileContent.append(line).append("\n");
                String trimmedLine = line.trim();

                // --- Comment and Code Line Counting ---
                if (trimmedLine.isEmpty()) {
                    // Empty lines are not counted as code or comment by this logic explicitly
                    // but contribute to totalLines
                    continue;
                }

                String lineForCommentCheck = trimmedLine;
                boolean countedAsCommentThisLine = false;

                if (inAMultiLineComment) {
                    fileCommentLines++;
                    countedAsCommentThisLine = true;
                    if (lineForCommentCheck.contains("*/")) {
                        inAMultiLineComment = false;
                        // Consider content after "*/" for current line processing if any
                        int endCommentIdx = lineForCommentCheck.indexOf("*/");
                        lineForCommentCheck = lineForCommentCheck.substring(endCommentIdx + 2).trim();
                        if (lineForCommentCheck.isEmpty()) { // Line ended with */ or only whitespace after
                            // Do nothing more for this line
                        } else {
                            // There's content after */, re-evaluate it
                            // This part can be complex. For simplicity here, we assume if a line was IN a multiline comment, it's counted as comment.
                            // A more precise approach would parse the line segment by segment.
                            // Let's assume the primary classification is done.
                        }
                    } else {
                        lineForCommentCheck = ""; // Whole line consumed by block comment
                    }
                }

                // Check for new comments if not already handled by active multi-line comment
                // or if there was content after a */
                if (!countedAsCommentThisLine || !lineForCommentCheck.isEmpty()) {
                    if (lineForCommentCheck.startsWith("/*")) {
                        if (!countedAsCommentThisLine) { // Avoid double counting if line had */ and then /*
                            fileCommentLines++;
                            countedAsCommentThisLine = true;
                        }
                        if (!lineForCommentCheck.contains("*/") || lineForCommentCheck.indexOf("*/") < lineForCommentCheck.indexOf("/*") + 2) {
                            inAMultiLineComment = true;
                        }
                    } else if (lineForCommentCheck.startsWith("//")) {
                        if (!countedAsCommentThisLine) {
                            fileCommentLines++;
                            countedAsCommentThisLine = true;
                        }
                    }
                }

                if (!countedAsCommentThisLine) {
                    fileCodeLines++;
                }

                // --- Method, Class, Interface, and Cyclomatic Complexity Counting ---
                String lineWithoutStringsAndChars = line.replaceAll("\".*?\"", "\"\"").replaceAll("'.*?'", "''");


                if (!inMethod) {
                    // Heuristic for method/constructor: contains "([params]) {" and not a control structure/type def.
                    // Modifiers (public, private, etc.) are common but not mandatory (e.g. package-private, constructors)
                    // Regex attempts to find a method signature-like pattern ending in an opening brace.
                    // Excludes common control flow statements and class/interface/enum definitions.
                    Pattern methodPattern = Pattern.compile(
                            "^(?!\\s*(if|for|while|switch|try|catch|synchronized\\s*\\(|class|interface|enum|record)\\b)" + // Negative lookahead for keywords
                                    ".*\\(" + // Contains ( - start of parameters
                                    "[^)]*" +   // Anything not ) - parameters
                                    "\\)\\s*" + // Closing ) and optional whitespace
                                    "(\\{|throws\\s+\\w+(\\s*,\\s*\\w+)*\\s*\\{)" // Opening brace, or throws clause then opening brace
                    );
                    Matcher methodMatcher = methodPattern.matcher(lineWithoutStringsAndChars.trim());

                    if (methodMatcher.find()) {
                        fileFunctionCount++;
                        inMethod = true;
                        currentMethodBody.setLength(0);
                        currentMethodBody.append(line).append("\n");
                        methodBraceLevel = countChar(lineWithoutStringsAndChars, '{') - countChar(lineWithoutStringsAndChars, '}');

                        if (methodBraceLevel == 0 && inMethod) { // Handle one-liner methods
                            CyclomaticComplexityCalculator ccCalc = new CyclomaticComplexityCalculator();
                            fileTotalCyclomaticComplexity += ccCalc.calculate(currentMethodBody.toString());
                            inMethod = false;
                        } else if (methodBraceLevel < 0) { // Should not happen with correct parsing
                            inMethod = false; methodBraceLevel = 0;
                        }
                    }
                } else { // Inside a method body
                    currentMethodBody.append(line).append("\n");
                    methodBraceLevel += countChar(lineWithoutStringsAndChars, '{');
                    methodBraceLevel -= countChar(lineWithoutStringsAndChars, '}');

                    if (methodBraceLevel <= 0) { // Method ended (<=0 for safety, should be ==0)
                        CyclomaticComplexityCalculator ccCalc = new CyclomaticComplexityCalculator();
                        fileTotalCyclomaticComplexity += ccCalc.calculate(currentMethodBody.toString());
                        inMethod = false;
                        methodBraceLevel = 0; // Reset
                    }
                }

                // Check for class and interface declarations (simple check)
                if (lineWithoutStringsAndChars.trim().matches(".*\\b(class|interface|enum|record)\\s+\\w+.*")) {
                    if (lineWithoutStringsAndChars.contains("class ") || lineWithoutStringsAndChars.contains("record ")) {
                        fileClassCount++;
                    } else if (lineWithoutStringsAndChars.contains("interface ") || lineWithoutStringsAndChars.contains("enum ")) {
                        // Grouping enums with interfaces for this count, or create a separate counter
                        fileInterfaceCount++;
                    }
                }
            }
            // Update grand totals after processing all lines of this file
            totalCodeLines += fileCodeLines;
            totalCommentLines += fileCommentLines;
            totalFunctions += fileFunctionCount;
            totalClasses += fileClassCount;
            totalInterfaces += fileInterfaceCount;


            // If a method was open at EOF (e.g., malformed file), process what was collected.
            if (inMethod && currentMethodBody.length() > 0) {
                CyclomaticComplexityCalculator ccCalc = new CyclomaticComplexityCalculator();
                fileTotalCyclomaticComplexity += ccCalc.calculate(currentMethodBody.toString());
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getAbsolutePath());
            e.printStackTrace();
            return; // Skip this file on error
        }

        CodeDuplicationDetector duplicationDetector = new CodeDuplicationDetector();
        int duplicateCount = duplicationDetector.detectDuplicates(fileContent.toString());

        MaintainabilityIndexCalculator miCalculator = new MaintainabilityIndexCalculator();
        // Use fileTotalCyclomaticComplexity (sum for the file) for MI
        double maintainabilityIndex = miCalculator.calculateMaintainabilityIndex(fileCodeLines, fileTotalCyclomaticComplexity, fileCommentLines);

        double avgCyclomaticComplexity = (fileFunctionCount > 0) ? (double) fileTotalCyclomaticComplexity / fileFunctionCount : fileTotalCyclomaticComplexity;


        markdownContent.append(String.format("| %d | %s | %d | %d | %d | %d | %d | %d | %d | %.2f | %.2f |\n",
                fileCount, file.getName(), fileLines, fileCodeLines, fileCommentLines,
                fileFunctionCount, fileClassCount, fileInterfaceCount, duplicateCount,
                maintainabilityIndex, avgCyclomaticComplexity));
    }

    private int countChar(String s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }


    public void printGrandTotals() {
        markdownContent.append("\n");
        markdownContent.append("## Grand Totals\n\n");
        markdownContent.append("| Metric                  | Value |\n");
        markdownContent.append("|-------------------------|-------|\n");
        markdownContent.append(String.format("| Total Files Processed   | %d    |\n", fileCount));
        markdownContent.append(String.format("| Grand Total Lines       | %d    |\n", totalLines));
        markdownContent.append(String.format("| Grand Total Code Lines  | %d    |\n", totalCodeLines));
        markdownContent.append(String.format("| Grand Total Comment Lines| %d    |\n", totalCommentLines));
        markdownContent.append(String.format("| Grand Total Functions   | %d    |\n", totalFunctions));
        markdownContent.append(String.format("| Grand Total Classes     | %d    |\n", totalClasses));
        markdownContent.append(String.format("| Grand Total Interfaces  | %d    |\n", totalInterfaces));
        // Could add more grand totals like average MI, total CC etc. if desired.
    }

    public void writeToFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(markdownContent.toString());
            System.out.println("Results successfully written to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath);
            e.printStackTrace();
        }
    }
}