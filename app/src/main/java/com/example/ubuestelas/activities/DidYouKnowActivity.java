
package com.example.ubuestelas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Actividad que se muestra despues de cada prueba en el juego para mostrar alguna curiosidad.
 *
 * @author Marcos Pena
 */
public class DidYouKnowActivity extends AppCompatActivity {

    /**
     * Inicializa la actividad con su respectivo layout. Se llama a otros métodos para inicializar el resto de la actividad.
     * @param savedInstanceState Si la actividad se ha reiniciado se le pasa el contenido de datos más reciente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_did_you_know);
        loadText();
    }

    /**
     * Carga el texto que extrae del archivo correspondiente a la prueba que se acaba de hacer.
     */
    public void loadText() {
        SharedPreferences nameFileSP = getSharedPreferences("nameFileSP", 0);
        String fileName = nameFileSP.getString("fileName", "error");
        try {
            JSONObject fileToRead = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), fileName + ".json"));
            String curiosity = fileToRead.getString("curiosity");
            TextView textView = findViewById(R.id.texto_curiosidad);
            textView.setText(curiosity);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Finaliza la actividad cuando la pantalla es pulsada. Lleva nuevamente al mapa.
     * @param view La vista que se ha clickado.
     */
    public void goToMap(View view) {
        finish();
    }
}
