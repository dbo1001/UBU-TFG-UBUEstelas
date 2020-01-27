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

public class DidYouKnowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_did_you_know);
        loadText();
    }

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

    public void goToMap(View view) {
        finish();
    }
}
