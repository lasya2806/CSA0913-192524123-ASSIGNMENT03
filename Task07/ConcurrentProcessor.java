
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

class ProcessingResult {
    String fileName;
    boolean success;
    int score;
    String message;

    ProcessingResult(String fileName, boolean success,
                     int score, String message) {
        this.fileName = fileName;
        this.success = success;
        this.score = score;
        this.message = message;
    }
}

class ScriptTask implements Callable<ProcessingResult> {
    private String fileName;

    ScriptTask(String fileName) {
        this.fileName = fileName;
    }

    public ProcessingResult call() {
        try {
            String content = new String(
                Files.readAllBytes(Paths.get(fileName))
            ).trim();

            if (!content.matches("\\d+")) {
                throw new IllegalArgumentException(
                    "Invalid score data"
                );
            }

            int score = Integer.parseInt(content);

            if (score < 0 || score > 100) {
                throw new IllegalArgumentException(
                    "Score must be between 0 and 100"
                );
            }

            return new ProcessingResult(
                fileName, true, score, "Processed successfully"
            );

        } catch (Exception e) {
            return new ProcessingResult(
                fileName, false, 0, e.getMessage()
            );
        }
    }
}

public class ConcurrentProcessor {

    public static void main(String[] args) throws Exception {

        Files.createDirectories(Paths.get("scripts"));

        // Create sample answer-script files
        for (int i = 1; i <= 8; i++) {
            Files.write(
                Paths.get("scripts/script" + i + ".txt"),
                Arrays.asList(String.valueOf(60 + i * 4))
            );
        }

        // Create one corrupted file
        Files.write(
            Paths.get("scripts/corrupt.txt"),
            Arrays.asList("INVALID_SCORE")
        );

        // Include one missing file to test fault handling
        List<String> files = new ArrayList<>();

        for (int i = 1; i <= 8; i++) {
            files.add("scripts/script" + i + ".txt");
        }

        files.add("scripts/corrupt.txt");
        files.add("scripts/missing.txt");

        int[] poolSizes = {1, 2, 4, 8};

        for (int poolSize : poolSizes) {

            System.out.println("\nThread Pool Size: " + poolSize);

            ExecutorService executor =
                Executors.newFixedThreadPool(poolSize);

            List<Future<ProcessingResult>> futures =
                new ArrayList<>();

            long start = System.nanoTime();

            // Submit each script as a separate task
            for (String file : files) {
                futures.add(
                    executor.submit(new ScriptTask(file))
                );
            }

            int successful = 0;
            int failed = 0;
            int totalScore = 0;

            // Collect results from Future objects
            for (Future<ProcessingResult> future : futures) {
                try {
                    ProcessingResult result = future.get();

                    if (result.success) {
                        successful++;
                        totalScore += result.score;
                        System.out.println(
                            result.fileName + " : " +
                            result.message + " | Score = " +
                            result.score
                        );
                    } else {
                        failed++;
                        System.out.println(
                            result.fileName + " : FAILED - " +
                            result.message
                        );
                    }

                } catch (ExecutionException e) {
                    failed++;
                    System.out.println(
                        "Unexpected task failure: " + e.getMessage()
                    );
                }
            }

            long end = System.nanoTime();

            executor.shutdown();

            System.out.println("Successful files: " + successful);
            System.out.println("Failed files: " + failed);
            System.out.println("Total score: " + totalScore);
            System.out.println(
                "Processing time: " +
                ((end - start) / 1_000_000.0) + " ms"
            );
        }
    }
}

