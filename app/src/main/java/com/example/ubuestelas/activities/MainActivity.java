package com.example.ubuestelas.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad principal de la aplicación. Donde se inicia la aplicación cada vez que arranca.
 *
 * @author Marcos Pena
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Inicializa la actividad con su respectivo layout. Solicita permisos, si no los tiene,
     * para acceder a la ubicación del dispositivo.
     * @param savedInstanceState Si la actividad se ha reiniciado se le pasa el contenido de datos más reciente.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        if(Util.loadJSONFromFilesDir(this, "userInfo") == null){
            ImageButton cont = (ImageButton) findViewById(R.id.continueButton);
            cont.getDrawable().setColorFilter(getResources().getColor(R.color.colorButtonDisabled), PorterDuff.Mode.LIGHTEN);
        }
    }

    /**
     * Cuando el botón de nuevo juego es pulsado se llama a este método. Lleva a la actividad donde
     * se escoge nombre y personaje.
     * TODO Si ya tiene una partida iniciada preguntar si quiere iniciar una nueva partida.
     * @param view La vista que se ha clickado.
     */
    public void newGame(View view) {
        startActivity(new Intent(this, NameActivity.class));

    }

    /**
     * Cuando el botón de continuar es pulsado se llama a este método. Si existe una partida iniciada,
     * lleva directamente al mapa con los datos ya guardados anteriormente en un fichero.
     * @param view La vista que se ha clickado.
     */
    public void continueGame(View view){
        String exists;
        exists = Util.loadJSONFromFilesDir(this, "userInfo");
        if (exists != null){
            startActivity(new Intent(this, NavigationDrawerActivity.class));
        }else{
//            ImageButton cont = (ImageButton) findViewById(R.id.continueButton);
//            cont.setEnabled(false);
//            cont.setClickable(false);
//            cont.getDrawable().setColorFilter(getResources().getColor(R.color.colorButtonDisabled), PorterDuff.Mode.LIGHTEN);
//            cont.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
            Toast.makeText(this,getString(R.string.game_not_exists), Toast.LENGTH_SHORT).show();
        }
//        startActivity(new Intent(this, NavigationDrawerActivity.class));
    }

    /**
     * Cuando se clicka sobre la imagen de ajustes llama a este método. Abre la actividad de ajustes.
     * @param view La vista que se ha clickado.
     */
    public void settingsButton(View view){
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
