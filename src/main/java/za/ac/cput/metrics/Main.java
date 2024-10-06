package za.ac.cput.metrics;




public class Main {
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
