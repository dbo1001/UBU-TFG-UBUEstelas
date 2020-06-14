
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
 * Actividad que se muestra después de cada prueba en el juego para mostrar alguna curiosidad.
 *
 * @author Marcos Pena
 */
public class DidYouKnowActivity extends AppCompatActivity {

    private MediaPlayer voice;

    /**
     * Inicializa la actividad con su respectivo layout. Se muestra el ShowcaseView si es la primera vez que se entra en esta pantalla.
     * Se llama a otros métodos para inicializar el resto de la actividad.
     * @param savedInstanceState Si la actividad se ha reiniciado se le pasa el contenido de datos más reciente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_did_you_know);
        loadTextAndAudio();
        loadGif();
        SharedPreferences didYouKnowActivitySP= getSharedPreferences("didYouKnowActivity", 0);
        boolean first = didYouKnowActivitySP.getBoolean("first", true);
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
        SharedPreferences.Editor didYouKnowActivityEditor = didYouKnowActivitySP.edit();
        didYouKnowActivityEditor.putBoolean("first", false);
        didYouKnowActivityEditor.apply();
    }

    /**
     * Inicializa el contenido de la barra de herramientas.
     * @param menu El menú en el que se colocan los items.
     * @return true para que se muestre el menú
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.did_you_know_menu, menu);
        return true;
    }

    /**
     * Se llama a este método cuando se selecciona una opción del menú de opciones.
     * Indica que se debe hacer en cada opción.
     * @param item El item que se ha seleccionado
     * @return  false para procesar el menú de forma normal, true para usarlo aquí.
     */
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
     * Inicializa el audio correspondiente con la prueba.
     */
    private void loadTextAndAudio() {
        SharedPreferences nameFileSP = getSharedPreferences("nameFileSP", 0);
        String fileName = nameFileSP.getString("fileName", "error");
        SharedPreferences didYouKnowActivitySP= getSharedPreferences("didYouKnowActivity", 0);
        boolean firstAudio = didYouKnowActivitySP.getBoolean("firstAudio", true);
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
            SharedPreferences.Editor didYouKnowActivityEditor = didYouKnowActivitySP.edit();
            didYouKnowActivityEditor.putBoolean("firstAudio", false);
            didYouKnowActivityEditor.apply();
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

    /**
     * Carga el gif que corresponde en función de la puntuación obtenida.
     */
    private void loadGif(){
        GifImageView gifImageView = findViewById(R.id.did_you_know_gif);
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

    /**
     * Sobrescribe el método cuando se pulsa el botón de atrás para que pare el audio.
     */
    @Override
    public void onBackPressed(){
        if (voice != null) {
            if (voice.isPlaying()) {
                voice.stop();
            }
        }
        super.onBackPressed();
    }

    /**
     * Obtiene la URL del vídeo correspondiente con la prueba que se acaba de realizar.
     * @return Cadena de texto con la url del vídeo.
     */
    private String getVideoURL(){
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
