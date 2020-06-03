package com.example.ubuestelas.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Actividad donde se carga la prueba de tipo test.
 *
 * @author Marcos Pena
 */
public class TypeTestActivity extends AppCompatActivity {

    JSONObject fileToRead;
    int attempts = 0;
    String markName;

    boolean hintUsed;

    /**
     * Inicializa la actividad con su respectivo layout. Se llama a otros métodos para inicializar el resto de la actividad.
     * @param savedInstanceState Si la actividad se ha reiniciado se le pasa el contenido de datos más reciente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_test);
        fillData();
        Bundle bundle = getIntent().getExtras();
        boolean first = bundle.getBoolean("first_game");
        if(first){
            Button button = new Button(this);
            button.setText(R.string.ok);
            button.setTextSize(24);
            button.setTextColor(Color.BLACK);
            button.setBackgroundColor(Color.WHITE);
//            button.setBackground(getResources().getDrawable(R.drawable.border));
            Target target = new ViewTarget(R.id.hint_test, this);
            new ShowcaseView.Builder(this)
                    .setTarget(target)
                    .setContentTitle(R.string.hint)
                    .setContentText(R.string.hint_explain)
                    .replaceEndButton(button)
                    .setStyle(R.style.CustomShowcaseTheme)
                    .hideOnTouchOutside()
                    .build();
        }
    }

    /**
     * Carga la pregunta del fichero correspondiente.
     */
    public void fillData(){
        try {
            SharedPreferences sharedPref = getSharedPreferences("navDrawFileName", 0);
            String fileNameMark = sharedPref.getString("fileName", "error");
            String[] splitName = fileNameMark.split("\\.");
            markName = splitName[0];
            fileToRead = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), fileNameMark));
            JSONArray options = fileToRead.getJSONArray("options");
            int optionsNumber = fileToRead.getInt("optionsNumber");
            String type = fileToRead.getString("type");
            String question = fileToRead.getString("question");
            TextView textView = findViewById(R.id.question_text);
            textView.setText(question);
            setOptions(options,optionsNumber, type);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Carga las posibles respuestas a la pregunta.
     * @param options Array cobtenido del fichero con las opciones para el test.
     * @param optionsNumber Número de opciones que tiene el test.
     * @param type Tipo del que son las opciones, imágenes o texto.
     */
    public void setOptions(JSONArray options, int optionsNumber, String type){
        RadioGroup rg = findViewById(R.id.options_group);

        for(int i =0; i<optionsNumber; i++){
            try {
                RadioButton radioButton = new RadioButton(this);
                JSONObject option = options.getJSONObject(i);
                if(type.equals("images")) {
                    int resourceId = this.getResources().getIdentifier(option.getString("option"), "drawable", this.getPackageName());
                    Drawable drawable = getResources().getDrawable(resourceId);
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int widthDisplay = size.x;
                    int heightDisplay = size.y;
                    int width;
                    int heigth;
                    if(widthDisplay<heightDisplay) {
                        width = (int) (widthDisplay * 0.75);
                        heigth = (int) (drawable.getIntrinsicHeight() * width) / drawable.getIntrinsicWidth();
                    }else{
                        heigth = (int) (heightDisplay * 0.6);
                        width = (int) (drawable.getIntrinsicWidth() * heigth) / drawable.getIntrinsicHeight();
                    }
                    drawable.setBounds(0,0,width,heigth);
                    radioButton.setCompoundDrawables(drawable,null,null,null);
//                    radioButton.setCompoundDrawablesWithIntrinsicBounds(resourceId, 0, 0, 0);
                    radioButton.setText(option.getString("option"));
                    radioButton.setTextColor(Color.TRANSPARENT);
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                    rg.addView(radioButton, params);
                }else{
                    radioButton.setText(option.getString("option"));
                    radioButton.setTextColor(Color.BLACK);
                    radioButton.setTextSize(24);
//                    radioButton.setTypeface(getResources().getFont(R.font.romanica));
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                    rg.addView(radioButton, params);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Se llama cuando se le da al botón de enviar. Tras la comprobación de si es correcta la frase,
     * en caso de serlo, se hace el cálculo de la puntuación, se guarda la información en el fichero de guardado
     * y se finaliza la actividad. Si no es correcta, se muestra un mensaje y se continúa jugando.
     * @param view La vista que se ha clickado.
     */
    public void getOption(View view){
        RadioGroup rg = findViewById(R.id.options_group);
        if (rg.getCheckedRadioButtonId() != -1) {
            int selectedId = rg.getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton) findViewById(selectedId);
            attempts++;
            try {
                String correctAnswer = fileToRead.getString("correct");
                double score;
                if (correctAnswer.equals(radioButton.getText())) {
                    if (attempts == 1) {
                        score = 100.00;
                        if(hintUsed){
                            SharedPreferences sharedPreferences =
                                    PreferenceManager.getDefaultSharedPreferences(this);
                            String difficulty = sharedPreferences.getString("difficulty", "easy");
                            switch (difficulty){
                                case "easy":
                                    score = score-(score * 0.05);
                                    break;
                                case "normal":
                                    score = score-(score * 0.1);
                                    break;
                                case "hard":
                                    score = score-(score * 0.2);
                                    break;
                            }
                        }
                    } else {
                        score = Util.testScoreIfFail(fileToRead.getInt("optionsNumber"), attempts, hintUsed, this);
                    }
                    Toast.makeText(this, getString(R.string.correct) + ". " + getString(R.string.points_obtained, score), Toast.LENGTH_SHORT).show();
                    JSONObject obj;
                    try {
                        obj = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
                        JSONArray marks = obj.getJSONArray("marks");
                        for (int i = 0; i < marks.length(); i++) {
                            JSONObject mark = marks.getJSONObject(i);
                            if (markName.equals(mark.getString("mark"))) {
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
                        double scoreToFile = Math.round(scoreFile * 100) / 100.0;
//                    String scoreFormat = String.format("%.2f",scoreFile);
//                    Double scoreToFile = Double.valueOf(scoreFormat);
//                    double scoreToFile = Double.parseDouble(scoreFormat);
                        obj.put("score", scoreToFile);
                        Util.writeJSONToFilesDir(this, "userInfo", obj.toString());
                    } catch (JSONException e) {
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
                    intent.putExtra("score", score);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.incorrect) + ". " + getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, getString(R.string.select_one) + ".", Toast.LENGTH_SHORT).show();
        }

    }

    public void hintClick(View view){
        try {
            hintUsed=true;
            String hint = fileToRead.getString("hint");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.hint).setMessage(hint).setPositiveButton(R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();
            ImageButton imageButton = (ImageButton) findViewById(R.id.hint_test);
            imageButton.setImageResource(R.drawable.game_hint_used);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
