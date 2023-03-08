package edu.fandm.teamyellowstone.wordly;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class Game extends AppCompatActivity {
    Boolean playing = true;
    private GraphViewAdapter graphViewAdapter;
    private GridView graphGridView;
    ArrayList<String> words = new ArrayList<>();
    String currentWord;
    int missingWords;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        words.add("dog");
        words.add("cat");
        words.add("bird");
        words.add("fish");



        // after words are added to arraylist
        currentWord= words.get(1);
        changeImgWord(currentWord);
        missingWords= words.size()-2;


        graphViewAdapter = new GraphViewAdapter(this, words);
        graphGridView = findViewById(R.id.graphListView);
        graphGridView.setAdapter(graphViewAdapter);
        graphViewAdapter.notifyDataSetChanged();


        graphGridView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedWord = words.get(position);
            TextView text = view.findViewById(R.id.itemET);
            if(text.getText() == "1"){
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
                    missingWords--;
                    text.setText(selectedWord);
                    words.set(position, "1");
                    if(missingWords==0){
                        endGame();
                    }else {
                        //change word being displayed

                    }

                }
                else if (userGuess.length() != selectedWord.length()) {
                    // Incorrect guess length
                    Toast.makeText(this, "Wrong length! Try again.", Toast.LENGTH_SHORT).show();
                    changeImgWord(words.get(position));
                }
                else {
                    // Incorrect guess
                    Toast.makeText(this, "Incorrect! Try again.", Toast.LENGTH_SHORT).show();
                    changeImgWord(words.get(position));
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();

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
                    System.out.println("Changing image");
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
                    System.out.println("Error");
                    e.printStackTrace();
                }
            }
        });
    }

    private void getImg(URL url){
        System.out.println("Getting image");
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
                            System.out.println("Image set");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void endGame(){
        playing = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Game.this, "You win!", Toast.LENGTH_SHORT).show();
                graphGridView.setVisibility(View.INVISIBLE);
            }
        });
    }





}