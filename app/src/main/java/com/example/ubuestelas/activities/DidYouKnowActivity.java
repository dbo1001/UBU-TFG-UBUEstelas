package com.example.ubuestelas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class DidYouKnowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_did_you_know);
        loadText();
    }

    public void loadText() {
        //TODO
        //tiene que haber un sharedpreferences donde me pase el nombre del archivo a cargar
        //de ahí va a salir la variable que ahora está como texto fijo en mark02.json
        try {
            JSONObject fileToRead = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), "mark02.json"));
            String curiosity = fileToRead.getString("curiosity");
            TextView textView = findViewById(R.id.texto_curiosidad);
            textView.setText(curiosity);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void goToMap(View view) {
        finish();
    }
}
