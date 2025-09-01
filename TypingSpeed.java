import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TypingSpeed {
    public static void main(String[] args) throws Exception {
        List<String> sentences = Files.readAllLines(Paths.get("sentences.txt"));
        sentences.removeIf(String::isEmpty);
        Random rand = new Random();
        String sentence = sentences.get(rand.nextInt(sentences.size()));

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = sc.nextLine().trim();

        System.out.println("\nType this sentence:\n" + sentence);

        for (int i = 3; i > 0; i--) {
            System.out.println("Starting in " + i + "...");
            TimeUnit.SECONDS.sleep(1);
        }

        long start = System.currentTimeMillis();
        System.out.print("\nStart typing here:\n");
        String typed = sc.nextLine();
        long end = System.currentTimeMillis();

        double timeTaken = (end - start) / 1000.0;
        int words = sentence.split("\\s+").length;
        double wpm = (words / timeTaken) * 60;

        int correctChars = 0;
        for (int i = 0; i < sentence.length(); i++) {
            if (i < typed.length() && sentence.charAt(i) == typed.charAt(i)) {
                correctChars++;
            }
        }
        double accuracy = (correctChars * 100.0) / sentence.length();

        System.out.println("\n--- Results ---");
        System.out.printf("Time taken: %.2f seconds%n", timeTaken);
        System.out.printf("Speed: %.2f WPM%n", wpm);
        System.out.printf("Accuracy: %.2f%%%n", accuracy);

        File scoreFile = new File("scores.txt");
        List<String> leaderboard = new ArrayList<>();
        double bestScore = 0;
        String bestPlayer = "";

        if (scoreFile.exists()) {
            try (Scanner fileScanner = new Scanner(scoreFile)) {
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    leaderboard.add(line);
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        double score = Double.parseDouble(parts[1]);
                        if (score > bestScore) {
                            bestScore = score;
                            bestPlayer = parts[0];
                        }
                    }
                }
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(scoreFile, true))) {
            pw.println(name + ":" + String.format("%.2f", wpm));
        }

        System.out.println("\nLeaderboard:");
        leaderboard.add(name + ":" + String.format("%.2f", wpm));
        leaderboard.sort((a, b) -> {
            double scoreA = Double.parseDouble(a.split(":")[1]);
            double scoreB = Double.parseDouble(b.split(":")[1]);
            return Double.compare(scoreB, scoreA);
        });
        for (int i = 0; i < Math.min(5, leaderboard.size()); i++) {
            String[] parts = leaderboard.get(i).split(":");
            System.out.printf("%d. %s - %.2f WPM%n", i + 1, parts[0], Double.parseDouble(parts[1]));
        }

        System.out.printf("\nHighest score so far: %s - %.2f WPM%n", bestPlayer, bestScore);
    }
}
