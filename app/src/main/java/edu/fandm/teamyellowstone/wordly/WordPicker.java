package edu.fandm.teamyellowstone.wordly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Source:ChatGPT
public class WordPicker {
    private static final Random random = new Random();

    public static Graph loadFile(InputStream inputStream) throws IOException{
        Graph graph = new Graph();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.trim().length() < 5){
                    graph.addWord(line.trim());
                }

            }
        }
        if (graph.isEmpty()) {
            throw new IllegalArgumentException("File contains no words.");
        }

        return graph;

    }

    public static String pickRandomWord(InputStream inputStream) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.trim().length() < 5) {
                    words.add(line.trim());
                }

            }
        }
        if (words.isEmpty()) {
            throw new IllegalArgumentException("File contains no words.");
        }
        return words.get(random.nextInt(words.size()));
    }
}
