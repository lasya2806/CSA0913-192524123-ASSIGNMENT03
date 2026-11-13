
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SalesReport {

    // Create sample store files
    static void createFiles() throws IOException {

        Files.write(Paths.get("store1.txt"),
            Arrays.asList("Store 1 Sales: 10000"));

        Files.write(Paths.get("store2.txt"),
            Arrays.asList("Store 2 Sales: 15000"));
    }

    // Method 1: Manual closing using finally
    static void manualReport(String[] files) {

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(
                new FileWriter("manual_report.txt"));

            for (String file : files) {

                BufferedReader reader = null;

                try {
                    reader = new BufferedReader(
                        new FileReader(file));

                    String line;

                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }

                } catch (IOException e) {
                    System.out.println(
                        "Manual version: Error reading " + file);

                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            System.out.println(
                                "Error closing reader");
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Manual report error: " + e.getMessage());

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Error closing writer");
                }
            }
        }
    }

    // Method 2: Try-with-resources
    static void automaticReport(String[] files) {

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("automatic_report.txt"))) {

            for (String file : files) {

                try (BufferedReader reader = new BufferedReader(
                        new FileReader(file))) {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }

                } catch (IOException e) {
                    System.out.println(
                        "Automatic version: Error reading " + file);
                }
            }

        } catch (IOException e) {
            System.out.println(
                "Automatic report error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        try {
            createFiles();

            String[] files = {
                "store1.txt",
                "missing_store.txt",
                "store2.txt"
            };

            System.out.println("Generating Manual Report...");
            manualReport(files);

            System.out.println("\nGenerating Automatic Report...");
            automaticReport(files);

            System.out.println("\nManual Report:");
            for (String line :
                    Files.readAllLines(Paths.get("manual_report.txt"))) {
                System.out.println(line);
            }

            System.out.println("\nAutomatic Report:");
            for (String line :
                    Files.readAllLines(Paths.get("automatic_report.txt"))) {
                System.out.println(line);
            }

            System.out.println("\nBoth reports generated successfully.");

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }
}

