package com.example.ubuestelas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class SceneActivity extends AppCompatActivity {


    private int counter =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escena);
        SharedPreferences sharedPref= getSharedPreferences("nameActivity",0);
        String name = sharedPref.getString("name", "amigo");
        JSONObject obj;
        try{
            obj = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(),"welcomeScenes.json"));
            JSONArray scenes = obj.getJSONArray("scenes");
            JSONObject scene = scenes.getJSONObject(0);
            String text= scene.getString("text");
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
}
