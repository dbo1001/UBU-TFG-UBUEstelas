package com.example.ubuestelas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.LettersAdapter;
import com.example.ubuestelas.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TypeCompleteWordsActivity extends AppCompatActivity {

    JSONObject fileToRead;
    static String markName;
    String fileNameMark;

    EditText textToComplete;

    int relativePosition = 0;
    int absolutePosition = 0;

    int attempts = 0;

    List<Double> errorsAttempt = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_complete_words);

        init();

        fillGrid();
    }

    public void init(){
        try{
            SharedPreferences sharedPref = getSharedPreferences("navDrawFileName", 0);
            fileNameMark = sharedPref.getString("fileName", "error");
            String[] splitName = fileNameMark.split("\\.");
            markName = splitName[0];
            fileToRead = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), fileNameMark));
            String text = fileToRead.getString("text");
            JSONArray gaps = fileToRead.getJSONArray("gaps");
            JSONObject gap = gaps.getJSONObject(relativePosition);
            absolutePosition = gap.getInt("position");
            text = text.replace("%l%", "_");
            textToComplete = (EditText) findViewById(R.id.text_to_complete);
            textToComplete.setText(text);
            textToComplete.setTextSize(24);
            textToComplete.setKeyListener(null);
            selectPosition();
            textToComplete.requestFocus();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void fillGrid(){
        GridView gridView = (GridView) findViewById(R.id.letters_selection);

        gridView.setAdapter(new LettersAdapter(this, fileNameMark, relativePosition));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            /**
             * Callback method to be invoked when an item in this AdapterView has
             * been clicked.
             * <p>
             * Implementers can call getItemAtPosition(relativePosition) if they need
             * to access the data associated with the selected item.
             *
             * @param parent   The AdapterView where the click happened.
             * @param view     The view within the AdapterView that was clicked (this
             *                 will be a view provided by the adapter)
             * @param position The relativePosition of the view in the adapter.
             * @param id       The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView letter = (TextView) view;
                CharSequence letts = letter.getText();
                textToComplete.getText().replace(absolutePosition-1,absolutePosition,letts);
            }
        });
    }

    public void leftArrowPressed(View view){
        try {
            if(relativePosition != 0){
                relativePosition--;
                fillGrid();
                JSONArray gaps = fileToRead.getJSONArray("gaps");
                JSONObject gap = gaps.getJSONObject(relativePosition);
                absolutePosition = gap.getInt("position");
                selectPosition();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void rightArrowPressed(View view){
        try {
            JSONArray gaps = fileToRead.getJSONArray("gaps");
            if(relativePosition != gaps.length()-1){
                relativePosition++;
                fillGrid();
                JSONObject gap = gaps.getJSONObject(relativePosition);
                absolutePosition = gap.getInt("position");
                selectPosition();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void selectPosition(){
        textToComplete.setSelection(absolutePosition-1,absolutePosition);
    }

    public boolean isAllPlaced(){
        return !textToComplete.getText().toString().contains("_");
    }

    public boolean checkCorrect(){
        boolean solved = true;
        List<Integer> wrongPositions = new ArrayList<Integer>();
        double errorsNumber = 0;
        try {
            JSONArray gaps = fileToRead.getJSONArray("gaps");
            for(int i = 0; i< gaps.length(); i++){
                JSONObject gap = gaps.getJSONObject(i);
                int position = gap.getInt("position");
                String correct = gap.getString("correct");
                if(textToComplete.getText().toString().charAt(position - 1) != correct.charAt(0)){
                    solved=false;
                    wrongPositions.add(position);
                    errorsNumber++;
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        if(!solved){
            indicateWrong(wrongPositions);
            errorsAttempt.add(errorsNumber);
        }
        return solved;
    }

    public void sendPhrase(View view){
        if(isAllPlaced()){
            attempts++;
            try {
                if(checkCorrect()){
                    double score;
                    if (attempts==1){
                        score=100.00;
                    }else {
                        JSONArray gaps = fileToRead.getJSONArray("gaps");
                        JSONObject gap = gaps.getJSONObject(0);
                        String [] letts = gap.getString("options").split(",");
                        score = Util.completeWordsScoreIfFail(this, attempts, gaps.length(), errorsAttempt, letts.length);
                    }
                    Toast.makeText(this,getString(R.string.correct) + ". " + getString(R.string.points_obtained, score), Toast.LENGTH_SHORT).show();
                    JSONObject obj;
                    obj = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
                    JSONArray marks = obj.getJSONArray("marks");
                    for (int i = 0; i < marks.length(); i++) {
                        JSONObject mark = marks.getJSONObject(i);
                        if(markName.equals(mark.getString("mark"))){
                            mark.put("solved", true);
                            if (markName.equals(mark.getString("mark"))) {
                                mark.put("solved", true);
                                if (score == 100) {
                                    mark.put("color", "green");
                                } else if (score == 0) {
                                    mark.put("color", "red");
                                } else if (score > 0 && score < 100) {
                                    mark.put("color", "yellow");
                                } else {
                                    mark.put("color", "azure");
                                }
                            }
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
        }else{
            Toast.makeText(this,getString(R.string.incorrect) + ". " + getString(R.string.complete_gaps), Toast.LENGTH_SHORT).show();
        }
    }

    public void indicateWrong(List<Integer> wrongPositions){
        Spannable modifiedText = new SpannableString(textToComplete.getText());
        modifiedText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorBlack)), 0, modifiedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textToComplete.setText(modifiedText);
        for(Integer position : wrongPositions) {
            modifiedText = textToComplete.getText();//new SpannableString(textToComplete.getText());
            modifiedText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorWrong)), position-1, position, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textToComplete.setText(modifiedText);
        }
    }
}
