package edu.fandm.teamyellowstone.wordly;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private String startWord;
    private String endWord;
    private  EditText startWord_et;

    private  Graph graph = new Graph();

    private EditText endWord_et;

    public List<String> shortestPath = new ArrayList<>();


    public void setStartandEndWords(){
        startWord_et = (EditText) findViewById(R.id.startWord);
        endWord_et = (EditText) findViewById(R.id.endWord);

        try {
            startWord = WordPicker.pickRandomWord(getAssets().open("words_gwicks.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            endWord = WordPicker.pickRandomWord(getAssets().open("words_gwicks.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(startWord.length() != endWord.length() || startWord.length() > 4){
            while(startWord.length() != endWord.length()){
                try {
                    startWord = WordPicker.pickRandomWord(getAssets().open("words_gwicks.txt"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    endWord = WordPicker.pickRandomWord(getAssets().open("words_gwicks.txt"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        startWord_et.setText(startWord);
        endWord_et.setText(endWord);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStartandEndWords();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    graph = WordPicker.loadFile(getAssets().open("words_gwicks.txt"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });



        Button newPuzzleButton = (Button) findViewById(R.id.newPuzzle);
        newPuzzleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartandEndWords();
            }
        });




        Button startButton = findViewById(R.id.play);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText startingWord_et = (EditText) findViewById(R.id.startWord);
                EditText endingWord_et = (EditText) findViewById(R.id.endWord);
                String startingWord = startingWord_et.getText().toString();
                String endingWord = endingWord_et.getText().toString();
                if(startingWord.length() != endingWord.length() || startingWord.length() > 4){
                    Toast.makeText(getApplicationContext(), "The words need to be the same length", Toast.LENGTH_LONG).show();

                }else{
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            shortestPath = graph.shortestPath(startingWord,endingWord);
                        }
                    });

                    if(shortestPath == null){
                        Toast.makeText(getApplicationContext(), "A path between these two words does not exist", Toast.LENGTH_LONG).show();
                    }else{
                        Intent i = new Intent(getApplicationContext(), Game.class);
                        startActivity(i);

                    }

                }

            }
        });

    }
}