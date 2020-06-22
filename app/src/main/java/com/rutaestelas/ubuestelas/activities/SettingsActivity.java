package com.rutaestelas.ubuestelas.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.rutaestelas.ubuestelas.R;

/**
 * Actividad donde se cargan los ajustes de la aplicación.
 *
 * @author Marcos Pena
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Inicializa la actividad con su respectivo layout.
     * @param savedInstanceState Si la actividad se ha reiniciado se le pasa el contenido de datos más reciente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Carga el fragmento con el xml donde se encuentra la lista de ajustes.
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    /**
     * Cuando la flecha de volver atrás en la barra de herramientas es presionada vuelve
     * a la actividad de la que se le llamó.
     * @param item El item del menú que se ha seleccionado.
     * @return Heredado del método sobrescrito.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}