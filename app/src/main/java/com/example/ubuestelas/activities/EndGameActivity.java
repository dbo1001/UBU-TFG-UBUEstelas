package com.example.ubuestelas.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class EndGameActivity extends AppCompatActivity {

    MediaPlayer voice;
    GifDrawable gifDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        fillText();
        setGifAndAudio();
        setFinish();
    }

    private void setFinish() {
        JSONObject obj;
        try {
            obj = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
            obj.put("finish", true);
            Util.writeJSONToFilesDir(this, "userInfo", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setGifAndAudio() {
        JSONObject obj;
        try {
            obj = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), "endGame.json"));
            GifImageView gifImageView = (GifImageView) findViewById(R.id.end_game_gif);
            int resourceGifId = this.getResources().getIdentifier(obj.getString("image"), "drawable", this.getPackageName());
            gifImageView.setImageResource(resourceGifId);
            gifDrawable = (GifDrawable) gifImageView.getDrawable();
//            gifDrawable.setLoopCount(2);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Rellena los textos en la pantalla final.
     */
    public void fillText(){
        TextView marksCompTV = (TextView) findViewById(R.id.marks_completed);
        TextView pointsObtainedTV = (TextView) findViewById(R.id.points_obtained);
        TextView finalTextTV = (TextView) findViewById(R.id.final_text);

        JSONObject endGameJO;
        try{
            double marksCS[]= checkCompletedMarkersAndScore();
            endGameJO = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(),"endGame.json"));
            String marksCompS = endGameJO.getString("marks_completed_format");
            marksCompS=marksCompS.replace("%comp%",String.valueOf((int)marksCS[0]));
            marksCompS=marksCompS.replace("%mtot%",String.valueOf((int)marksCS[1]));
            String pointsObtainedS = endGameJO.getString("points_obtained_format");
            pointsObtainedS=pointsObtainedS.replace("%obt%",Double.toString(marksCS[2]));
            double maxScore = marksCS[1]*100;
            pointsObtainedS=pointsObtainedS.replace("%ptot%",String.valueOf((int)maxScore));
            String finalTextS;
            int resourceAudioID;
            if(marksCS[2]>=(maxScore*0.9)){
                finalTextS = endGameJO.getString("outstanding");
                resourceAudioID = this.getResources().getIdentifier(endGameJO.getString("outstanding_audio"), "raw", this.getPackageName());
            }else if(marksCS[2]<(maxScore*0.9) && marksCS[2]>=(maxScore*0.7)){
                finalTextS = endGameJO.getString("mention");
                resourceAudioID = this.getResources().getIdentifier(endGameJO.getString("mention_audio"), "raw", this.getPackageName());
            }else if(marksCS[2]<(maxScore*0.7) && marksCS[2]>=(maxScore*0.5)){
                finalTextS = endGameJO.getString("pass");
                resourceAudioID = this.getResources().getIdentifier(endGameJO.getString("pass_audio"), "raw", this.getPackageName());
            }else{
                finalTextS = endGameJO.getString("fail");
                resourceAudioID = this.getResources().getIdentifier(endGameJO.getString("fail_audio"), "raw", this.getPackageName());
            }
            voice = MediaPlayer.create(this, resourceAudioID);
            voice.start();
            voice.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    gifDrawable.stop();
                }
            });

            marksCompTV.setText(marksCompS);
            pointsObtainedTV.setText(pointsObtainedS);
            finalTextTV.setText(finalTextS);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Comprueba de cuales de los marcadores ya han sido completadas sus pruebas.
     * @return Lista con los marcadores ya resueltos.
     */
    public double[] checkCompletedMarkersAndScore(){
        JSONObject obj;
        int completed=0;
        int total=0;
        double score=0;
        try {
            obj = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
            score = obj.getDouble("score");
            JSONArray marks = obj.getJSONArray("marks");
            total=marks.length();
            for (int i = 0; i < marks.length(); i++){
                JSONObject mark = marks.getJSONObject(i);
                if (mark.getBoolean("solved")){
                    completed++;
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return new double[] {completed,total,score};
    }

    public void goMainMenu(View view){
        if(voice.isPlaying()){
            voice.stop();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.back_main_menu)
               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
                    }
                })
               .setNegativeButton(R.string.no, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed(){
        if(voice.isPlaying()){
            voice.stop();
        }
        finish();
        super.onBackPressed();
    }
}
