package com.example.ubuestelas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;
import com.google.android.gms.common.util.ArrayUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Actividad donde el usuario introduce su nombre y escoge personaje.
 *
 * @author Marcos Pena
 */
public class NameActivity extends AppCompatActivity{

    JSONArray chars;
    Integer[] characs = {};
    Integer[] characsSelec = {};

    int prevIdCharacter = R.id.character;
    int prevSelecCharacter = R.drawable.character01;

    boolean selected;

    /**
     * Inicializa la actividad con su respectivo layout. Se llama a otros métodos para inicializar el resto de la actividad.
     * @param savedInstanceState Si la actividad se ha reiniciado se le pasa el contenido de datos más reciente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nombre);
        ImageButton cont = (ImageButton) findViewById(R.id.continueToScene);
        cont.getDrawable().setColorFilter(null);
        fillCharacters();
    }

    /**
     * Método que se llama cuando el botón continuar es pulsado. Guarda en un SharedPreferences
     * el nombre que el usuario ha dado.
     * @param view La vista que se ha clickado.
     */
    public void continueToScene(View view){

        SharedPreferences nameSP= getSharedPreferences("nameActivity", 0);
        SharedPreferences.Editor nameEditor = nameSP.edit();
        EditText editText = findViewById(R.id.input_name);
        String name = editText.getText().toString();
        nameEditor.putString("name", name);
        nameEditor.commit();
        if(!selected){
            Toast.makeText(this, getString(R.string.select_character) + ". ", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(this, SceneActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Carga en pantalla las imagenes de los personajes disponibles.
     * Cuando uno de ellos es seleccionado lo guarda en un SharedPreferences.
     */
    public void fillCharacters(){
        JSONObject obj;
        try {
            obj = new JSONObject(Util.loadJSONFromAsset(this, "characters.json"));
            chars = obj.getJSONArray("characters");
            for (int i = 0; i<chars.length(); i++){
                JSONObject charac = chars.getJSONObject(i);
                int resourceIdCharac = getResources().getIdentifier(charac.getString("characterImage"), "drawable", getPackageName());
                int resourceIdSelecCharac = getResources().getIdentifier(charac.getString("whenSelected"), "drawable", getPackageName());
                characs = ArrayUtils.appendToArray(characs, resourceIdCharac);
                characsSelec = ArrayUtils.appendToArray(characsSelec, resourceIdSelecCharac);
            }}catch (JSONException e){
            e.printStackTrace();
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.images_scroll);
        for (int i =0; i<characs.length; i++) {
            ImageView iv = new ImageView (this);
            iv.setImageResource(characs[i]);
            iv.setId(R.id.character+i);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            iv.setMinimumWidth(50);
//            iv.requestLayout();
//            iv.getLayoutParams().width=300;
//            iv.setLayoutParams(new LinearLayout.LayoutParams(600, LinearLayout.LayoutParams.WRAP_CONTENT));
            iv.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          for (int i = 0; i<chars.length(); i++) {
                                              if (v.getId() == R.id.character + i) {
                                                  selected=true;
                                                  ImageView ivSelected = (ImageView) findViewById(R.id.character + i);
                                                  ImageView ivPreviousSelected = (ImageView) findViewById(prevIdCharacter);
                                                  ivPreviousSelected.setImageResource(prevSelecCharacter);
                                                  ivPreviousSelected.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                                  ivSelected.setImageResource(characsSelec[i]);
                                                  ivSelected.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                                  prevSelecCharacter=characs[i];
                                                  prevIdCharacter =R.id.character+i;
                                                  SharedPreferences characSP= getSharedPreferences("characterSelected", 0);
                                                  SharedPreferences.Editor characEditor = characSP.edit();
                                                  characEditor.putInt("drawableCharac", characs[i]);
                                                  characEditor.commit();
                                              }
                                          }
                                      }});
            linearLayout.addView(iv);
        }

    }
}
