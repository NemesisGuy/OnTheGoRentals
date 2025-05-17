package za.ac.cput.metrics;

import za.ac.cput.metrics.CodeDuplicationDetector;
import za.ac.cput.metrics.MaintainabilityIndexCalculator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CodeLineCounter {
    private int totalLines = 0;
    private int totalCodeLines = 0;
    private int totalCommentLines = 0;
    private int totalFunctions = 0;
    private int totalClasses = 0; // Total classes counter
    private int totalInterfaces = 0; // Total interfaces counter
    private final StringBuilder markdownContent = new StringBuilder();
    private int fileCount = 0; // Keep track of the number of files
    private String baseDirectoryPath; // Base directory to use for relative paths

    public void countLines(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            System.out.println("The provided path is not a directory.");
            return;
        }

        baseDirectoryPath = directory.getAbsolutePath(); // Store the base directory for relative paths
        processDirectory(directory);
    }

    private void processDirectory(File directory) {
        // Get the relative path by removing the base path from the current directory's absolute path
        String relativePath = directory.getAbsolutePath().replace(baseDirectoryPath, ".");

        markdownContent.append("\n## Directory: ").append(relativePath).append("\n\n");
        markdownContent.append("| # | File Name | Total Lines | Code Lines | Comment Lines | Function Count | Class Count | Interface Count | Duplicate Count | Maintainability Index | Cyclomatic Complexity |\n");
        markdownContent.append("|---|-----------|-------------|------------|---------------|----------------|-------------|------------------|------------------|------------------------|-----------------------|\n");

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    processDirectory(file); // Recursively process subdirectories
                } else if (file.getName().endsWith(".java")) {
                    fileCount++; // Increment file count
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
        int totalCyclomaticComplexity = 0; // Initialize cyclomatic complexity

        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder currentMethodBody = new StringBuilder();
            boolean inMethod = false;

            while ((line = reader.readLine()) != null) {
                fileLines++;
                totalLines++;

                fileContent.append(line).append("\n"); // Store content for duplication detection

                if (line.trim().isEmpty()) {
                    continue;
                }

                if (line.trim().startsWith("//") || line.trim().startsWith("/*") || line.trim().startsWith("*")) {
                    fileCommentLines++;
                    totalCommentLines++;
                } else {
                    fileCodeLines++;
                    totalCodeLines++;
                }

                // Check for function declarations
                if (line.trim().matches(".*(public|private|protected|static|final)\\s+.*\\(.*\\)\\s*\\{")) {
                    fileFunctionCount++;
                    totalFunctions++;
                    inMethod = true; // Start capturing method body
                    currentMethodBody.setLength(0); // Clear previous method body
                }

                if (inMethod) {
                    currentMethodBody.append(line).append("\n"); // Capture method body

                    // Check for method closing brace
                    if (line.contains("}")) {
                        // Calculate cyclomatic complexity for the method
                        CyclomaticComplexityCalculator cyclomaticComplexityCalculator = new CyclomaticComplexityCalculator();
                        totalCyclomaticComplexity += cyclomaticComplexityCalculator.calculate(currentMethodBody.toString());
                        currentMethodBody.setLength(0); // Reset for next method
                        inMethod = false; // End capturing method body
                    }
                }

                // Check for class and interface declarations
                if (line.trim().matches(".*\\b(class|interface)\\s+.*")) {
                    if (line.contains("class")) {
                        fileClassCount++;
                        totalClasses++;
                    } else if (line.contains("interface")) {
                        fileInterfaceCount++;
                        totalInterfaces++;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getAbsolutePath());
            e.printStackTrace();
        }

        // Detect duplicates
        CodeDuplicationDetector duplicationDetector = new CodeDuplicationDetector();
        int duplicateCount = duplicationDetector.detectDuplicates(fileContent.toString());

        // Calculate Maintainability Index
        MaintainabilityIndexCalculator maintainabilityIndexCalculator = new MaintainabilityIndexCalculator();
        double maintainabilityIndex = maintainabilityIndexCalculator.calculateMaintainabilityIndex(fileCodeLines, fileFunctionCount, fileCommentLines);

        // Append to markdown content
        markdownContent.append(String.format("| %d | %s | %d | %d | %d | %d | %d | %d | %d | %.2f | %d |\n",
                fileCount, file.getName(), fileLines, fileCodeLines, fileCommentLines, fileFunctionCount, fileClassCount, fileInterfaceCount, duplicateCount, maintainabilityIndex, totalCyclomaticComplexity));
    }

    public void printGrandTotals() {
        markdownContent.append("\n");
        markdownContent.append("## Grand Totals\n\n");
        markdownContent.append("| Total Files | Grand Total Lines | Grand Total Code Lines | Grand Total Comment Lines | Grand Total Functions | Grand Total Classes | Grand Total Interfaces |\n");
        markdownContent.append("|-------------|-------------------|------------------------|---------------------------|-----------------------|---------------------|------------------------|\n");
        markdownContent.append(String.format("| %d | %d | %d | %d | %d | %d | %d |\n", fileCount, totalLines, totalCodeLines, totalCommentLines, totalFunctions, totalClasses, totalInterfaces));
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

    public static void main(String[] args) {
        CodeLineCounter counter = new CodeLineCounter();
        // Replace the path below with your actual path
        counter.countLines("C:\\Users\\Reign\\IdeaProjects\\OnTheGoRentals\\src\\main\\java\\za\\ac\\cput");

        // Call to print the grand totals only once after processing all files
        counter.printGrandTotals();

        // Write the results to the markdown file
        counter.writeToFile("CodeCounterResults.md");
    }
}
