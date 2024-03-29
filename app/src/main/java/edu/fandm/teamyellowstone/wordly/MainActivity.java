package edu.fandm.teamyellowstone.wordly;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private String startWord;
    private String endWord;
    private  EditText startWord_et;
    public static TextView loadingTextView;
    private  Graph graph = new Graph();
    private EditText endWord_et;
    public List<String> shortestPath;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

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

        if(startWord.length() != endWord.length() /*|| startWord.length() > 6*/){

            while(startWord.length() != endWord.length() /*|| startWord.length() >6*/){
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
        loadingTextView.setVisibility(View.GONE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (prefs.getBoolean(IS_FIRST_TIME_LAUNCH, true)) {
            // The app is being launched for the first time
            prefs.edit().putBoolean(IS_FIRST_TIME_LAUNCH, false).apply();
            Intent i = new Intent(getApplicationContext(), InstructionsPage.class);
            startActivity(i);

            // Do your first time operations here
        }


        setContentView(R.layout.activity_main);
        loadingTextView = findViewById(R.id.loading_text_view);
        loadingTextView.setVisibility(View.VISIBLE);
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

                loadingTextView.setVisibility(View.VISIBLE);
                setStartandEndWords();


            }
        });


        Button startButton = findViewById(R.id.play);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText startingWord_et = (EditText) findViewById(R.id.startWord);
                EditText endingWord_et = (EditText) findViewById(R.id.endWord);
                String startingWord = startingWord_et.getText().toString().toLowerCase();
                String endingWord = endingWord_et.getText().toString().toLowerCase();
                if(startingWord.length() != endingWord.length()){
                    Toast.makeText(getApplicationContext(), "The words need to be the same length.", Toast.LENGTH_LONG).show();

                }else{
                    shortestPath = graph.shortestPath(startingWord,endingWord);
                    if(shortestPath == null){
                        Toast.makeText(getApplicationContext(), "A path between these two words does not exist", Toast.LENGTH_LONG).show();
                    }else if(shortestPath.size() <= 2){
                        Toast.makeText(getApplicationContext(), "the path between these words is only one letter difference, no game can be played.", Toast.LENGTH_LONG).show();

                    }else{
                        for(int i = 0; i < shortestPath.size(); i++){
                            Log.d("Shortest Path at i:", shortestPath.get(i));
                        };
                        Game.setWords(shortestPath);
                        Intent i = new Intent(getApplicationContext(), Game.class);
                        startActivity(i);

                    }
                }
            }
        });

    }
}