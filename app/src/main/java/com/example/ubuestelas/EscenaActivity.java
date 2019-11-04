package com.example.ubuestelas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class EscenaActivity extends AppCompatActivity {

    public static final int NUMERO_ESCENAS=4;

    private int contador=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escena);
        SharedPreferences sharedPref= getSharedPreferences("nombreActivity",0);
        String name = sharedPref.getString("nombre", "amigo");
        String texto1= getResources().getString(R.string.textoEscena1, name);
        TextView textView = findViewById(R.id.texto_escena);
        textView.setText(texto1);
    }

    public void cambiarEscena (View view){
        ImageView imagen = findViewById(R.id.imagen_escena);
        TextView texto = findViewById(R.id.texto_escena);
        contador++;
        switch (contador){
            case 2:
                imagen.setImageResource(R.drawable.cararomano);
                texto.setText(R.string.textoEscena2);
                break;
            case 3:
                imagen.setImageResource(R.drawable.romano);
                texto.setText(R.string.textoEscena3);
                break;
            case NUMERO_ESCENAS:
                imagen.setImageResource(R.drawable.cararomano);
                texto.setText(R.string.textoEscena4);
                break;
            default:
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;

        }
    }
}
