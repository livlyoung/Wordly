package edu.fandm.teamyellowstone.wordly;

import android.view.View;

import java.util.*;


//Source: Chat GPT
public class Graph {
    private Map<String, Set<String>> adjList;

    public Graph() {
        this.adjList = new HashMap<>();
    }

    public void addWord(String word) {
        // Add the word to the adjacency list with an empty set of neighbors
        adjList.put(word, new HashSet<>());
        // Check all other words in the adjacency list to see if they differ by one letter
        for (String otherWord : adjList.keySet()) {
            if (isOneLetterDifferent(word, otherWord)) {
                // Add an edge between the two words
                adjList.get(word).add(otherWord);
                adjList.get(otherWord).add(word);
            }
        }
    }

    public List<String> shortestPath(String start, String end) {
        Map<String, String> parentMap = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(start);
        parentMap.put(start, null);

        while (!queue.isEmpty()) {
            String current = queue.remove();
            if (current.equals(end)) {
                // We've found the end word, so build and return the path
                List<String> path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = parentMap.get(current);
                }
                Collections.reverse(path);
                return path;
            }
            for (String neighbor : adjList.get(current)) {
                if (!parentMap.containsKey(neighbor)) {
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        // If we get here, there is no path from start to end
        MainActivity.loadingTextView.setVisibility(View.GONE);
        return null;
    }

    private boolean isOneLetterDifferent(String word1, String word2) {
        if (word1.length() != word2.length()) {
            return false;
        }
        int numDifferences = 0;
        for (int i = 0; i < word1.length(); i++) {
            if (word1.charAt(i) != word2.charAt(i)) {
                numDifferences++;
            }
            if (numDifferences > 1) {
                return false;
            }
        }
        return numDifferences == 1;
    }

    public boolean isEmpty(){
        return adjList.isEmpty();

    }


}

