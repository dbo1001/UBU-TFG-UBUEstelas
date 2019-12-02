package com.example.ubuestelas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SceneActivity extends AppCompatActivity {


    private static final String FILE_NAME="userInfo";

    private int counter =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escena);
        createJSONprogress();
        SharedPreferences sharedPref= getSharedPreferences("nameActivity",0);
        String name = sharedPref.getString("name", "amigo");
        JSONObject obj;
        try{
            obj = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(),"welcomeScenes.json"));
            JSONArray scenes = obj.getJSONArray("scenes");
            JSONObject scene = scenes.getJSONObject(0);

            String text = scene.getString("text");
            text = text.replace("%user%", name);
            TextView textView = findViewById(R.id.scene_text);
            textView.setText(text);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void changeScene(View view){
        ImageView imageView = findViewById(R.id.imagen_escena);
        TextView textView = findViewById(R.id.scene_text);
        counter++;
        JSONObject obj;

        try{
            obj = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(),"welcomeScenes.json"));
            JSONArray scenes = obj.getJSONArray("scenes");
            int scenesNumber = obj.getInt("scenesNumber");
            if (counter >= scenesNumber){
//                Intent intent = new Intent(this, MapsActivity.class);
                Intent intent = new Intent(this, NavigationDrawerActivity.class);
                startActivity(intent);
            }
            JSONObject scene = scenes.getJSONObject(counter);
            final int resourceId = this.getResources().getIdentifier(scene.getString("image"), "drawable", this.getPackageName());
            imageView.setImageResource(resourceId);
            textView.setText(scene.getString("text"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createJSONprogress(){
        File file = new File(this.getFilesDir(), FILE_NAME);
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        try {
            if (!file.exists()) {
                file.createNewFile();
                fileWriter = new FileWriter(file.getAbsoluteFile());
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write("{}");
                bufferedWriter.close();
            }
            SharedPreferences sharedPref = getSharedPreferences("nameActivity", 0);
            String name = sharedPref.getString("name", "amigo");
            JSONObject obj = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), "marksJSON.json"));
            JSONArray markstot = obj.getJSONArray("marks");
            JSONObject complete = new JSONObject();
            JSONArray stelas = new JSONArray();
            for (int i = 1; i <= markstot.length(); i++) {
                JSONObject mark = new JSONObject();
                mark.put("mark" + String.format("%02d", i), false);
                stelas.put(mark);
            }
            complete.put("user", name);
            complete.put("marks", stelas);
            fileWriter = new FileWriter(file.getAbsoluteFile());
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(complete.toString());
            bufferedWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}
