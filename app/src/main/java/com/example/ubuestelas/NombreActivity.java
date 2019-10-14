package com.example.ubuestelas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NombreActivity extends AppCompatActivity {

    public static final String EXTRA_NOMBRE = "com.example.ubuestelas.NOMBRE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nombre);
    }

    public void continuarAEscena(View view){

        SharedPreferences nombreSP= getSharedPreferences("nombreActivity", 0);
        SharedPreferences.Editor nombreEditor = nombreSP.edit();
        EditText editText = (EditText) findViewById(R.id.input_nombre);
        String nombre = editText.getText().toString();
        nombreEditor.putString("nombre", nombre);
        nombreEditor.commit();

        Intent intent = new Intent(this, EscenaActivity.class);
        startActivity(intent);
    }
}
