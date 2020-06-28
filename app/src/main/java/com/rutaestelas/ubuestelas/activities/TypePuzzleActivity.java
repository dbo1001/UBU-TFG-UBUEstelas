package com.rutaestelas.ubuestelas.activities;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rutaestelas.ubuestelas.R;
import com.rutaestelas.ubuestelas.util.GestureDetectGridView;
import com.rutaestelas.ubuestelas.util.PuzzleAdapter;
import com.rutaestelas.ubuestelas.util.Util;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Actividad donde se carga la prueba del puzzle.
 *
 * @author Dave Park
 * @author Marcos Pena
 */
public class TypePuzzleActivity extends AppCompatActivity {

    private static GestureDetectGridView mGridView;

    private static final int COLUMNS = 3;
    private static final int DIMENSIONS = COLUMNS * COLUMNS;

    private static int mColumnWidth, mColumnHeight;

    public static final String up = "up";
    public static final String down = "down";
    public static final String left = "left";
    public static final String right = "right";

    private static String[] tileList;
    private static List<Drawable> imagenes;

    private static Context ctx;
    private static Activity activity;
    private static boolean hintUsed = false;

    private JSONObject fileToRead;
    private static String markName;

    private static Chronometer chronometer;
    private long pauseOffset;

    /**
     * Inicializa la actividad con su respectivo layout. Se llama a otros métodos para inicializar el resto de la actividad.
     * Se inicializa el temporizador.
     * @param savedInstanceState Si la actividad se ha reiniciado se le pasa el contenido de datos más reciente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_puzzle);


        chronometer = findViewById(R.id.timer);
        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);

        Bundle bundle = getIntent().getExtras();
        boolean first = bundle.getBoolean("first_game");
        if(first){
            Button button = new Button(this);
            button.setText(R.string.ok);
            button.setTextSize(24);
            button.setTextColor(Color.BLACK);
            button.setBackgroundColor(Color.WHITE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chronometer.start();
                }
            });
            Target target = new ViewTarget(R.id.hint_puzzle, this);
            new ShowcaseView.Builder(this)
                    .setTarget(target)
                    .setContentTitle(R.string.hint)
                    .setContentText(R.string.hint_explain)
                    .replaceEndButton(button)
                    .setStyle(R.style.CustomShowcaseTheme)
                    .hideOnTouchOutside()
                    .build();
        }

        init();

        divide();

        scramble();

        setDimensions();


    }

    /**
     * Lee la imagen del archivo y la divide en trozos.
     */
    private void divide() {
        try {
            String markImage = fileToRead.getString("image");
            int resourceId = this.getResources().getIdentifier(markImage, "drawable", this.getPackageName());
            Bitmap originalBm = BitmapFactory.decodeResource(getResources(), resourceId);
            imagenes = new ArrayList<>();
            int n = COLUMNS;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    imagenes.add(new BitmapDrawable(getResources(), Bitmap.createBitmap(originalBm, (originalBm.getWidth() / n) * j, (originalBm.getHeight() / n) * i, originalBm.getWidth() / n, originalBm.getHeight() / n)));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Comienza el temporizador y se inicializan algunas variables.
     */
    private void init() {
        try {
            chronometer.start();
            ctx = this;
            activity = this;
            SharedPreferences sharedPref = getSharedPreferences("navDrawFileName", 0);
            String fileNameMark = sharedPref.getString("fileName", "error");
            String[] splitName = fileNameMark.split("\\.");
            markName = splitName[0];
            fileToRead = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), fileNameMark));

            mGridView = findViewById(R.id.grid);
            mGridView.setNumColumns(COLUMNS);

            tileList = new String[DIMENSIONS];
            for (int i = 0; i < DIMENSIONS; i++) {
                tileList[i] = String.valueOf(i);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Mezcla las piezas del puzzle de forma aleatoria.
     */
    private void scramble() {
        int index;
        String temp;
        Random random = new Random();

        for (int i = tileList.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = tileList[index];
            tileList[index] = tileList[i];
            tileList[i] = temp;
        }
    }

    /**
     * Redimensiona los tamaños para que cuadre con la pantalla.
     */
    private void setDimensions() {
        ViewTreeObserver vto = mGridView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth = mGridView.getMeasuredWidth();
                int displayHeight = mGridView.getHeight();//getMeasuredHeight();

                int statusbarHeight;
                int requiredHeight;
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    statusbarHeight = getStatusBarHeight(getApplicationContext());
                    requiredHeight = displayHeight - statusbarHeight;
                } else {
                    statusbarHeight = getStatusBarHeight(getApplicationContext())*3;
                    requiredHeight = displayHeight - statusbarHeight;
                }

                mColumnWidth = displayWidth / COLUMNS;
                mColumnHeight = requiredHeight / COLUMNS;

                display(getApplicationContext());
            }
        });
    }

    /**
     * Obtiene el tamaño de la barra de herramientas.
     * @param context Contexto de la aplicación.
     * @return Tamaño de la barra de herramientas
     */
    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    /**
     * Muestra las imágenes en sus respectivos lugares.
     * @param context Contexto de la aplicación.
     */
    private static void display(Context context) {
        ArrayList<Button> buttons = new ArrayList<>();
        Button button;

        for (String s : tileList) {
            button = new Button(context);
            for (int j = 0; j < tileList.length; j++) {
                if (s.equals(String.valueOf(j))) {
                    button.setBackground(imagenes.get(j));
                    buttons.add(button);
                }
            }
        }

        mGridView.setAdapter(new PuzzleAdapter(buttons, mColumnWidth, mColumnHeight));
    }

    /**
     * Intercambia las piezas de sitio. También comprueba si se ha resuelto y finaliza la actividad.
     * @param context Contexto de la aplicación.
     * @param currentPosition Posición actual de la pieza.
     * @param swap Posición con la que se va a intercambiar.
     */
    private static void swap(Context context, int currentPosition, int swap) {
        String newPosition = tileList[currentPosition + swap];
        tileList[currentPosition + swap] = tileList[currentPosition];
        tileList[currentPosition] = newPosition;
        display(context);

        if (isSolved()) {
            double score = Util.puzzleSolvedScore(context, chronometer.getText(), hintUsed);
            Toast.makeText(context, ctx.getString(R.string.correct) + ". " + ctx.getString(R.string.points_obtained, score), Toast.LENGTH_SHORT).show();

            JSONObject obj;
            try {

                obj = new JSONObject(Util.loadJSONFromFilesDir(ctx, "userInfo"));
                JSONArray marks = obj.getJSONArray("marks");
                for (int i = 0; i < marks.length(); i++) {
                    JSONObject mark = marks.getJSONObject(i);
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
                obj.put("marks", marks);
                double scoreFile = obj.getDouble("score");
                scoreFile += score;
                double scoreToFile = Math.round(scoreFile * 100) / 100.0;
                obj.put("score", scoreToFile);
                Util.writeJSONToFilesDir(ctx, "userInfo", obj.toString());
                SharedPreferences scoreSP = ctx.getSharedPreferences("scoreEvent", 0);
                SharedPreferences.Editor scoreEditor = scoreSP.edit();
                scoreEditor.putString("score", String.valueOf(score));
                scoreEditor.apply();
                SharedPreferences nameFileSP = ctx.getSharedPreferences("nameFileSP", 0);
                SharedPreferences.Editor nameFileEditor = nameFileSP.edit();
                nameFileEditor.putString("fileName", markName);
                nameFileEditor.apply();
                Intent intent = new Intent(ctx, DidYouKnowActivity.class);
                intent.putExtra("score", score);
                ctx.startActivity(intent);
                activity.finish();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Calcula los movimientos de la piezas en función de lo que ha indicado el jugador.
     * @param context Contexto de la aplicación.
     * @param direction Dirección en la que ha movido.
     * @param position Posición de la pieza que ha movido.
     */
    public static void moveTiles(Context context, String direction, int position) {

        // Upper-left-corner tile
        if (position == 0) {

            if (direction.equals(right)) swap(context, position, 1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, R.string.invalid_move, Toast.LENGTH_SHORT).show();

            // Upper-center tiles
        } else if (position > 0 && position < COLUMNS - 1) {
            switch (direction) {
                case left:
                    swap(context, position, -1);
                    break;
                case down:
                    swap(context, position, COLUMNS);
                    break;
                case right:
                    swap(context, position, 1);
                    break;
                default:
                    Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
                    break;
            }

            // Upper-right-corner tile
        } else if (position == COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Left-side tiles
        } else if (position > COLUMNS - 1 && position < DIMENSIONS - COLUMNS &&
                position % COLUMNS == 0) {
            switch (direction) {
                case up:
                    swap(context, position, -COLUMNS);
                    break;
                case right:
                    swap(context, position, 1);
                    break;
                case down:
                    swap(context, position, COLUMNS);
                    break;
                default:
                    Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
                    break;
            }

            // Right-side AND bottom-right-corner tiles
        } else if (position == COLUMNS * 2 - 1 || position == COLUMNS * 3 - 1) {
            switch (direction) {
                case up:
                    swap(context, position, -COLUMNS);
                    break;
                case left:
                    swap(context, position, -1);
                    break;
                case down:

                    // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                    // right-corner tile.
                    if (position <= DIMENSIONS - COLUMNS - 1) swap(context, position,
                            COLUMNS);
                    else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
                    break;
            }

            // Bottom-left corner tile
        } else if (position == DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Bottom-center tiles
        } else if (position < DIMENSIONS - 1 && position > DIMENSIONS - COLUMNS) {
            switch (direction) {
                case up:
                    swap(context, position, -COLUMNS);
                    break;
                case left:
                    swap(context, position, -1);
                    break;
                case right:
                    swap(context, position, 1);
                    break;
                default:
                    Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
                    break;
            }

            // Center tiles
        } else {
            switch (direction) {
                case up:
                    swap(context, position, -COLUMNS);
                    break;
                case left:
                    swap(context, position, -1);
                    break;
                case right:
                    swap(context, position, 1);
                    break;
                default:
                    swap(context, position, COLUMNS);
                    break;
            }
        }
    }

    /**
     * Comprueba si el puzzle ya está resuelto.
     * @return true si el puzzle está resuelto.
     */
    private static boolean isSolved() {
        boolean solved = false;

        for (int i = 0; i < tileList.length; i++) {
            if (tileList[i].equals(String.valueOf(i))) {
                solved = true;
            } else {
                solved = false;
                break;
            }
        }

        return solved;
    }

    /**
     * Pausa el cronómetro cuando se pausa la aplicación.
     */
    @Override
    protected void onPause(){
        super.onPause();
        chronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
    }

    /**
     * reanuda el cronometro cuando se reanuda la aplicación.
     */
    @Override
    protected void onResume(){
        super.onResume();
        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        chronometer.start();
    }

    /**
     * Al pulsar en el icono de la pista se llama a este método.
     * Muestra un diálogo con la pista correspondiente a esa prueba.
     * @param view Vista que se ha clickado.
     */
    public void hintClick(View view){
        try {
            hintUsed=true;
            String hint = fileToRead.getString("hint");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.hint).setMessage(hint).setPositiveButton(R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();
            ImageButton imageButton = findViewById(R.id.hint_puzzle);
            imageButton.setImageResource(R.drawable.game_hint_used);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
