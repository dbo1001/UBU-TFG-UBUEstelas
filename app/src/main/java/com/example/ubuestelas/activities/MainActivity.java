package com.example.ubuestelas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ubuestelas.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
    *Se llama cuando el usuario pulsa el botón nuevo juego
     */
    public void newGame(View view) {
        startActivity(new Intent(this, NameActivity.class));

    }

    public void continueGame(View view){
        startActivity(new Intent(this, NavigationDrawerActivity.class));
    }

    public void settingsButton(View view){
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
