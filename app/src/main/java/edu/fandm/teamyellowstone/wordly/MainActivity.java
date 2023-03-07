package edu.fandm.teamyellowstone.wordly;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String startWord;
    private String endWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText startWord_et = (EditText) findViewById(R.id.startWord);
        EditText endWord_et = (EditText) findViewById(R.id.endWord);


        try {
            startWord = WordPicker.pickRandomWord("Users/liv/Downloads/words_gwicks.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            endWord = WordPicker.pickRandomWord("Users/liv/Downloads/words_gwicks.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(startWord.length() != endWord.length()){
            while(startWord.length() != endWord.length()){
                try {
                    startWord = WordPicker.pickRandomWord("Users/liv/Downloads/words_gwicks.txt");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    endWord = WordPicker.pickRandomWord("Users/liv/Downloads/words_gwicks.txt");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        startWord_et.setText(startWord);
        endWord_et.setText(endWord);


        Button newPuzzleButton = (Button) findViewById(R.id.newPuzzle);
        newPuzzleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startWord = WordPicker.pickRandomWord("Users/liv/Downloads/words_gwicks.txt");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    endWord = WordPicker.pickRandomWord("Users/liv/Downloads/words_gwicks.txt");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                startWord_et.setText(startWord);
                endWord_et.setText(endWord);
            }
        });




        Button startButton = findViewById(R.id.play);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Game.class);
                startActivity(i);
            }
        });

    }
}