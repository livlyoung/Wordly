package edu.fandm.teamyellowstone.wordly;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class Game extends AppCompatActivity {
    Boolean playing = true;
    String currentWord = "dog";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        changeImgWord(currentWord);

    }


    private void changeImgWord(String word){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
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






}