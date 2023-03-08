package edu.fandm.teamyellowstone.wordly;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class Game extends AppCompatActivity {
    Boolean playing = true;
    private GraphViewAdapter graphViewAdapter;
    private GridView graphGridView;
    private static ArrayList<String>  words = new ArrayList<>();
    String currentWord;
    int missingWords;
    ArrayList<Boolean> guessedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Hide the button bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);




        // after words are added to arraylist
        currentWord= words.get(1);
        changeImgWord(currentWord);
        missingWords= words.size()-2;


        graphViewAdapter = new GraphViewAdapter(this, words);
        graphGridView = findViewById(R.id.graphListView);
        graphGridView.setAdapter(graphViewAdapter);
        graphViewAdapter.notifyDataSetChanged();
        setupguessedList();

        graphGridView.setOnItemClickListener((parent, view, position, id) -> {
            if(currentWord != words.get(position)){
                changeImgWord(words.get(position));
            }
            String selectedWord = words.get(position);
            TextView text = view.findViewById(R.id.itemET);
            if(text.getText() == selectedWord){
                return; // Already guessed
            }
            // Create a new dialog to ask for the user's guess
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Guess the word");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String userGuess = input.getText().toString();
                if (userGuess.equalsIgnoreCase(selectedWord)) {
                    // Correct guess
                    Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
                    guessedList.set(position, true);
                    missingWords--;
                    text.setText(selectedWord);
                    if(missingWords==0){
                        endGame();
                    }else {
                        for(int i=0; i<guessedList.size(); i++){
                            if(!guessedList.get(i)){
                                changeImgWord(words.get(i));
                                break;
                            }
                        }

                    }

                }
                else if (userGuess.length() != selectedWord.length()) {
                    // Incorrect guess length
                    Toast.makeText(this, "Wrong length! Try again.", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Incorrect guess
                    Toast.makeText(this, "Incorrect! Try again.", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();

        });

        Button button = findViewById(R.id.hintButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hint = getHintLetter(words.get(words.indexOf(currentWord)-1),currentWord );
                Toast.makeText(getApplicationContext(), hint, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void changeImgWord(String word){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    currentWord = word;
                    String current = currentWord;
                    String apiKey = "34188491-f127f0cfa95dbc5ad739452a2";
                    String query = word;
                    URL url = new URL("https://pixabay.com/api/?key=" + apiKey + "&q=" + query + "&image_type=photo");
                    HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.connect();

                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuffer data = new StringBuffer();
                    String curLine;
                    while ((curLine = in.readLine()) != null) {
                        data.append(curLine);
                    }

                    JSONObject json = new JSONObject(data.toString());
                    JSONArray hits = json.getJSONArray("hits");
                    int i =0;
                    while(currentWord == current){
                        if(!playing){
                            break;
                        }
                        getImg(new URL(hits.getJSONObject(i).getString("webformatURL")));
                        Thread.sleep(3000);
                        i++;
                        if(i>5){
                            i=0;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getImg(URL url){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.connect();

                    InputStream in = new BufferedInputStream(con.getInputStream());
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    in.close();
                    con.disconnect();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageView imageView = findViewById(R.id.hintImage);
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void endGame(){
        words.clear();
        playing = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Game.this, "You win!", Toast.LENGTH_SHORT).show();
                graphGridView.setVisibility(View.INVISIBLE);
                Button button = findViewById(R.id.hintButton);
                button.setVisibility(View.INVISIBLE);
                ImageView imageView = findViewById(R.id.hintImage);
                imageView.setImageResource(R.drawable.star);

                // Add touch listener to the root view
                View root = getWindow().getDecorView().getRootView();
                root.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Finish the current activity when the user taps the screen
                        finish();
                        return true;
                    }
                });
            }
        });
    }


    private void setupguessedList(){
        for(int i = 0; i<words.size(); i++) {
            if (i == 0 || i == words.size() - 1) {
                guessedList.add(true);
            } else {
                guessedList.add(false);
            }
        }
    }

    public static void setWords(List<String> list) {
        System.out.println("setWords");

        for (int i = 0; i < list.size(); i++) {
            words.add(list.get(i));
        }
    }

    public static String getHintLetter(String s1, String s2) {
        Log.d("hint", s1 + " " + s2);
        if (s1.length() != s2.length()) {
            return "error";
        }

        int index = -1;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                if (index == -1) {
                    index = i;
                } else {
                    return "error";
                }
            }
        }

        if (index == -1) {
            return "error";
        } else {
            return Character.toString(s2.charAt(index));
        }
    }

    @Override
    public void onBackPressed(){
    Log.d("back", "back");
    words.clear();
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finishAffinity();

    }



}