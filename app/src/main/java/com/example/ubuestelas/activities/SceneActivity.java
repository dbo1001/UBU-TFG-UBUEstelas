package com.example.ubuestelas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Actividad donde se muestra la bienvenida a la aplicación.
 * Se muestra el texto y la imagen pasadas por fichero.
 *
 * @author Marcos Pena
 */
public class SceneActivity extends AppCompatActivity {


    private static final String FILE_NAME="userInfo";

    private int counter =0;

    private MediaPlayer voice;

    private GifDrawable gifDrawable;

    /**
     * Inicializa la actividad con su respectivo layout.
     * Se carga el primer texto con el nombre que el usuario ha introducido.
     * @param savedInstanceState Si la actividad se ha reiniciado se le pasa el contenido de datos más reciente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escena);
        createJSONprogress();
        clearSharedPreferences();
        SharedPreferences sharedPref= getSharedPreferences("nameActivity",0);
        String name = sharedPref.getString("name", "amigo");
        JSONObject obj;
        try{
            obj = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(),"welcomeScenes.json"));
            JSONArray scenes = obj.getJSONArray("scenes");
            JSONObject scene = scenes.getJSONObject(0);

            String text = scene.getString("text");
            text = text.replace("%user%", name);
            TextView textView = findViewById(R.id.scene_text);
            textView.setText(text);

            int resourceGifId = this.getResources().getIdentifier(scene.getString("image"), "drawable", this.getPackageName());
            GifImageView gifImageView = findViewById(R.id.scene_gif);
            gifImageView.setImageResource(resourceGifId);
            gifDrawable =(GifDrawable) gifImageView.getDrawable();
//            gifDrawable.setLoopCount(2);

            int resourceAudioID = this.getResources().getIdentifier(scene.getString("audio"), "raw", this.getPackageName());
            voice = MediaPlayer.create(this, resourceAudioID);
            voice.start();
            voice.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    gifDrawable.stop();
                    Toast.makeText(getApplicationContext(), getString(R.string.push_to_go), Toast.LENGTH_LONG).show();
                }
            });
//            VideoView videoView = (VideoView) findViewById(R.id.presentation_video);
//            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.waving_roman;
//            Uri uri = Uri.parse(videoPath);
//            videoView.setVideoURI(uri);
//            videoView.start();

//            WebView webView =  (WebView) findViewById(R.id.web_view_scene);
//            webView.setBackgroundColor(Color.TRANSPARENT); //for gif without background
//            String videoPath = "android.resource://" + getPackageName() + "/" + R.drawable.larito_waving;
//            webView.loadUrl(videoPath);




        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Cada vez que se pulsa en la pantalla se llama a este método mientras haya más textos de bienvenida que mostrar.
     * @param view La vista que se ha clickado.
     */
    public void changeScene(View view){
        GifImageView gifImageView = findViewById(R.id.scene_gif);
//        ImageView imageView = findViewById(R.id.imagen_escena);
        TextView textView = findViewById(R.id.scene_text);
        counter++;
        JSONObject obj;
        if(voice.isPlaying()){
            voice.stop();
        }

        try{
            obj = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(),"welcomeScenes.json"));
            JSONArray scenes = obj.getJSONArray("scenes");
            int scenesNumber = obj.getInt("scenesNumber");
            if (counter == scenesNumber){
//                Intent intent = new Intent(this, MapsActivity.class);
                Intent intent = new Intent(this, NavigationDrawerActivity.class);
                startActivity(intent);
                finish();
            }else {
                JSONObject scene = scenes.getJSONObject(counter);
                int resourceGifId = this.getResources().getIdentifier(scene.getString("image"), "drawable", this.getPackageName());
                gifImageView.setImageResource(resourceGifId);
                gifDrawable =(GifDrawable) gifImageView.getDrawable();
//                gifDrawable.setLoopCount(2);
//                imageView.setImageResource(resourceId);
                textView.setText(scene.getString("text"));
                int resourceAudioID = this.getResources().getIdentifier(scene.getString("audio"), "raw", this.getPackageName());
                voice = MediaPlayer.create(this, resourceAudioID);
                voice.start();
                voice.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        gifDrawable.stop();
                    }
                });
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Se crea el fichero donde se guarda toda la información de progreso del juego.
     */
    private void createJSONprogress(){
        File file = new File(this.getFilesDir(), FILE_NAME);
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        try {
            if (!file.exists()) {
                file.createNewFile();
                fileWriter = new FileWriter(file.getAbsoluteFile());
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write("{}");
                bufferedWriter.close();
            }
            double score=0.0;
            SharedPreferences sharedPref = getSharedPreferences("nameActivity", 0);
            String name = sharedPref.getString("name", "amigo");
            JSONObject obj = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), "marksJSON.json"));
            JSONArray markstot = obj.getJSONArray("marks");
            JSONObject complete = new JSONObject();
            JSONArray stelas = new JSONArray();
            for (int i = 1; i <= markstot.length(); i++) {
                JSONObject mark = new JSONObject();
                mark.put("mark", "mark" + String.format("%02d", i));
                mark.put("solved", false);
                mark.put("color", "azure");
                stelas.put(mark);
            }
            SharedPreferences scoreSharedPref = getSharedPreferences("characterSelected", 0);
            int drawableCharac = scoreSharedPref.getInt("drawableCharac", R.drawable.character01);
            complete.put("user", name);
            complete.put("marks", stelas);
            complete.put("score", score);
            complete.put("drawableCharac", drawableCharac);
            complete.put("finish",false);
            fileWriter = new FileWriter(file.getAbsoluteFile());
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(complete.toString());
            bufferedWriter.close();
        }catch (IOException | JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed(){
        if(voice.isPlaying()){
            voice.stop();
        }
        super.onBackPressed();
    }

    private void clearSharedPreferences(){
        this.getSharedPreferences("imageSliderActivity", 0).edit().clear().apply();
        this.getSharedPreferences("didYouKnowActivity", 0).edit().clear().apply();
        this.getSharedPreferences("newNavigationDrawerActivity", 0).edit().clear().apply();
    }
}
