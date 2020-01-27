package com.example.ubuestelas.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TypeTestActivity extends AppCompatActivity {

    JSONObject fileToRead;
    int attempts = 0;
    String markName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_test);
        fillData();
    }

    public void fillData(){
        try {
            SharedPreferences sharedPref = getSharedPreferences("navDrawFileName", 0);
            String fileNameMark = sharedPref.getString("fileName", "error");
            String[] splitName = fileNameMark.split("\\.");
            markName = splitName[0];
            fileToRead = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), fileNameMark));
            JSONArray options = fileToRead.getJSONArray("options");
            int optionsNumber = fileToRead.getInt("optionsNumber");
            String question = fileToRead.getString("question");
            TextView textView = findViewById(R.id.question_text);
            textView.setText(question);
            setOptions(options,optionsNumber);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void setOptions(JSONArray options, int optionsNumber){
        RadioGroup rg = findViewById(R.id.options_group);

        for(int i =0; i<optionsNumber; i++){
            try {
                RadioButton radioButton = new RadioButton(this);
                JSONObject option = options.getJSONObject(i);
                int resourceId = this.getResources().getIdentifier(option.getString("image"), "drawable", this.getPackageName());
                radioButton.setCompoundDrawablesWithIntrinsicBounds(resourceId,0,0,0);
                radioButton.setText(option.getString("image"));
                radioButton.setTextColor(Color.WHITE);
//                radioButton.setText(option.getString("image"));
//                radioButton.setButtonDrawable(getResources().getDrawable(resourceId));
//                radioButton.setButtonDrawable(resourceId);
                radioButton.setId(i);//set radiobutton id and store it somewhere
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                rg.addView(radioButton, params);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
//        RadioButton radioButton = new RadioButton(this);
//        int resourceId = this.getResources().getIdentifier("mark02option4", "drawable", this.getPackageName());
//        radioButton.setCompoundDrawablesWithIntrinsicBounds(resourceId,0,0,0);
////        radioButton.setId(3);//set radiobutton id and store it somewhere
//        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
//        rg.addView(radioButton, params);
//        RadioButton radioButton2 = new RadioButton(this);
//        int resourceId2 = this.getResources().getIdentifier("mark02option5", "drawable", this.getPackageName());
//        radioButton2.setCompoundDrawablesWithIntrinsicBounds(resourceId2,0,0,0);
////        radioButton.setId(3);//set radiobutton id and store it somewhere
//        RadioGroup.LayoutParams params2 = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
//        rg.addView(radioButton2, params2);
    }

    public void getOption(View view){
        RadioGroup rg = findViewById(R.id.options_group);
        int selectedId = rg.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        attempts++;
        try {
            String correctAnswer = fileToRead.getString("correct");
            double score;
            if(correctAnswer.equals(radioButton.getText())){
                if (attempts==1){
                    score=100.00;
                }else {
                    score = Util.testScoreIfFail(fileToRead.getInt("optionsNumber"), attempts);
                }
                Toast.makeText(this,getString(R.string.correct) + ". " + getString(R.string.points_obtained, score), Toast.LENGTH_SHORT).show();
                JSONObject obj;
                try {
                    obj = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
                    JSONArray marks = obj.getJSONArray("marks");
                    for (int i = 0; i < marks.length(); i++) {
                        JSONObject mark = marks.getJSONObject(i);
                        if(markName.equals(mark.getString("mark"))){
                            mark.put("solved", true);
                        }
                    }
                    obj.put("marks", marks);
                    double scoreFile = obj.getDouble("score");
                    scoreFile += score;
                    double scoreToFile = Math.round(scoreFile*100)/100.0;
//                    String scoreFormat = String.format("%.2f",scoreFile);
//                    Double scoreToFile = Double.valueOf(scoreFormat);
//                    double scoreToFile = Double.parseDouble(scoreFormat);
                    obj.put("score", scoreToFile);
                    Util.writeJSONToFilesDir(this,"userInfo", obj.toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
                SharedPreferences scoreSP = getSharedPreferences("scoreEvent", 0);
                SharedPreferences.Editor scoreEditor = scoreSP.edit();
                scoreEditor.putString("score", String.valueOf(score));
                scoreEditor.commit();
                SharedPreferences nameFileSP = getSharedPreferences("nameFileSP", 0);
                SharedPreferences.Editor nameFileEditor = nameFileSP.edit();
                nameFileEditor.putString("fileName", markName);
                nameFileEditor.commit();
                Intent intent = new Intent(this, DidYouKnowActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(this,getString(R.string.incorrect) + ". " + getString(R.string.try_again), Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}
