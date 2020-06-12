
package com.example.ubuestelas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;
import com.github.amlcurran.showcaseview.ShowcaseView;

import org.json.JSONException;
import org.json.JSONObject;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Actividad que se muestra despues de cada prueba en el juego para mostrar alguna curiosidad.
 *
 * @author Marcos Pena
 */
public class DidYouKnowActivity extends AppCompatActivity {

    MediaPlayer voice;

    /**
     * Inicializa la actividad con su respectivo layout. Se llama a otros métodos para inicializar el resto de la actividad.
     * @param savedInstanceState Si la actividad se ha reiniciado se le pasa el contenido de datos más reciente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_did_you_know);
        loadTextAndAudio();
        loadGif();
        SharedPreferences didYouKnowActicitySP= getSharedPreferences("didYouKnowActicity", 0);
        boolean first = didYouKnowActicitySP.getBoolean("first", true);
        if(first) {
            Button button = new Button(this);
            button.setText(R.string.ok);
            button.setTextSize(24);
            button.setTextColor(Color.BLACK);
            button.setBackgroundColor(Color.WHITE);

            new ShowcaseView.Builder(this)
                    .withNewStyleShowcase()
                    .setContentTitle(R.string.did_you_know_info_text)
                    .replaceEndButton(button)
                    .setStyle(R.style.CustomShowcaseThemeDYKA)
                    .hideOnTouchOutside()
                    .build();

        }
        SharedPreferences.Editor didYouKnowActicityEditor = didYouKnowActicitySP.edit();
        didYouKnowActicityEditor.putBoolean("first", false);
        didYouKnowActicityEditor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.did_you_know_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.stela_video_button) {
            String video = getVideoURL();
            if(video.equals("NOT_VIDEO")){
                Toast.makeText(this, getString(R.string.not_video) + ".", Toast.LENGTH_SHORT).show();
            }else {
                if (voice != null) {
                    if (voice.isPlaying()) {
                        voice.stop();
                    }
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video));
                intent.putExtra("force_fullscreen",true);
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Carga el texto que extrae del archivo correspondiente a la prueba que se acaba de hacer.
     */
    public void loadTextAndAudio() {
        SharedPreferences nameFileSP = getSharedPreferences("nameFileSP", 0);
        String fileName = nameFileSP.getString("fileName", "error");
        SharedPreferences didYouKnowActicitySP= getSharedPreferences("didYouKnowActicity", 0);
        boolean firstAudio = didYouKnowActicitySP.getBoolean("firstAudio", true);
        try {
            JSONObject fileToRead = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), fileName + ".json"));
            String curiosity = fileToRead.getString("curiosity");
            TextView textView = findViewById(R.id.texto_curiosidad);
            textView.setText(curiosity);
            if(firstAudio) {
                int resourceAudioID = this.getResources().getIdentifier(fileToRead.getString("audio"), "raw", this.getPackageName());
                voice = MediaPlayer.create(this, resourceAudioID);
                voice.start();
            }
            SharedPreferences.Editor didYouKnowActicityEditor = didYouKnowActicitySP.edit();
            didYouKnowActicityEditor.putBoolean("firstAudio", false);
            didYouKnowActicityEditor.apply();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Finaliza la actividad cuando la pantalla es pulsada. Lleva nuevamente al mapa.
     * @param view La vista que se ha clickado.
     */
    public void goToMap(View view) {
        if (voice != null) {
            if (voice.isPlaying()) {
                voice.stop();
            }
        }
        finish();
    }

    public void loadGif(){
        GifImageView gifImageView = (GifImageView) findViewById(R.id.did_you_know_gif);
        Intent intent = getIntent();
        double score = intent.getDoubleExtra("score",0);
        if(score<50){
            gifImageView.setImageResource(R.drawable.larito_wrong);
        }else{
            gifImageView.setImageResource(R.drawable.larito_correct);
        }
        GifDrawable gifDrawable =(GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(1);
    }

    @Override
    public void onBackPressed(){
        if (voice != null) {
            if (voice.isPlaying()) {
                voice.stop();
            }
        }
        super.onBackPressed();
    }

    public String getVideoURL(){
        SharedPreferences nameFileSP = getSharedPreferences("nameFileSP", 0);
        String fileName = nameFileSP.getString("fileName", "error");
        String video = "";
        try {
            JSONObject fileToRead = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), fileName + ".json"));
            video = fileToRead.getString("video");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return video;
    }
}
