package com.example.ubuestelas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nombre);
    }

    public void continueToScene(View view){

        SharedPreferences nameSP= getSharedPreferences("nameActivity", 0);
        SharedPreferences.Editor nameEditor = nameSP.edit();
        EditText editText = findViewById(R.id.input_name);
        String name = editText.getText().toString();
        nameEditor.putString("name", name);
        nameEditor.commit();

        Intent intent = new Intent(this, SceneActivity.class);
        startActivity(intent);
    }
}
