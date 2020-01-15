package com.example.ubuestelas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.CharacterSelectionAdapter;

public class NameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nombre);
        fillCharacters();
    }

    public void continueToScene(View view){

        SharedPreferences nameSP= getSharedPreferences("nameActivity", 0);
        SharedPreferences.Editor nameEditor = nameSP.edit();
        EditText editText = findViewById(R.id.input_name);
        String name = editText.getText().toString();
        nameEditor.putString("name", name);
        nameEditor.commit();

        Intent intent = new Intent(this, SceneActivity.class);
        startActivity(intent);
    }

    public void fillCharacters(){
        final GridView gridView = (GridView) findViewById(R.id.selec_charac);

        gridView.setAdapter(new CharacterSelectionAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            /**
             * Callback method to be invoked when an item in this AdapterView has
             * been clicked.
             * <p>
             * Implementers can call getItemAtPosition(position) if they need
             * to access the data associated with the selected item.
             *
             * @param parent   The AdapterView where the click happened.
             * @param view     The view within the AdapterView that was clicked (this
             *                 will be a view provided by the adapter)
             * @param position The position of the view in the adapter.
             * @param id       The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences characSP= getSharedPreferences("characterSelected", 0);
                SharedPreferences.Editor characEditor = characSP.edit();
                characEditor.putInt("position", position);
                characEditor.commit();

                gridView.setAdapter(new CharacterSelectionAdapter(getApplicationContext(), position));
            }
        });
    }
}
