package com.example.ubuestelas.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.ubuestelas.R;

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
     * Cuando el botón de continuar es pulsado se llama a este método. Lleva directamente al mapa
     * con los datos ya guardados anteriormente en un fichero.
     * TODO Si no tiene ninguna partida iniciada no permiti pulsar este botón.
     * @param view La vista que se ha clickado.
     */
    public void continueGame(View view){
        startActivity(new Intent(this, NavigationDrawerActivity.class));
    }

    /**
     * Cuando se clicka sobre la imagen de ajustes llama a este método. Abre la actividad de ajustes.
     * @param view La vista que se ha clickado.
     */
    public void settingsButton(View view){
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
