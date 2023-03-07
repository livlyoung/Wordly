package edu.fandm.teamyellowstone.wordly;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Source:ChatGPT
public class WordPicker {
    private static final Random random = new Random();

    public static String pickRandomWord(String filePath) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }
        }
        if (words.isEmpty()) {
            throw new IllegalArgumentException("File contains no words.");
        }
        return words.get(random.nextInt(words.size()));
    }
}
