package com.example.ubuestelas.activities;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.widget.ToggleButton;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.GestureDetectGridView;
import com.example.ubuestelas.util.PuzzleAdapter;
import com.example.ubuestelas.util.Util;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
    static List<Drawable> imagenes;

    private static Context ctx;
    private static Activity activity;

    JSONObject fileToRead;
    static String markName;

//    static Thread thread;
//
//    private Handler handler = new Handler();
//
//    int number = 0;
//
//    private int time = 0;
//    private Timer timer;
//    private TextView textViewTimer;

    private static Chronometer chronometer;
    private long pauseOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_puzzle);

        init();

//        initTimer();

//        startCounting();

        divide();

        scramble();

        setDimensions();
    }

//    private void startCounting() {
//        handler.post(run);
//    }
//
//    private Runnable run = new Runnable() {
//        TextView timer = (TextView) findViewById(R.id.timer) ;
//        @Override
//        public void run() {
//            number++;
//            timer.setText(number);
//            handler.postDelayed(this, 1000);
//        }
//    };

//    private void initTimer() {
//        timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        textViewTimer.setText(String.format(Locale.getDefault(), "%d", time));
//                        if (time >= 0)
//                            time += 1;
//                        else {
//                            textViewTimer.setText(R.string.did_you_know);
//                        }
//                    }
//                });
//            }
//        };
//        timer.scheduleAtFixedRate(timerTask, 0, 1000);

//    }

    public void divide() {
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

    public void init() {
        try {
            chronometer = (Chronometer) findViewById(R.id.timer);
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            ctx = this;
            activity = this;
            SharedPreferences sharedPref = getSharedPreferences("navDrawFileName", 0);
            String fileNameMark = sharedPref.getString("fileName", "error");
            String[] splitName = fileNameMark.split("\\.");
            markName = splitName[0];
            fileToRead = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), fileNameMark));

            mGridView = (GestureDetectGridView) findViewById(R.id.grid);
            mGridView.setNumColumns(COLUMNS);

            tileList = new String[DIMENSIONS];
            for (int i = 0; i < DIMENSIONS; i++) {
                tileList[i] = String.valueOf(i);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void scramble() {
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

    public void setDimensions() {
        ViewTreeObserver vto = mGridView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth = mGridView.getMeasuredWidth();
                int displayHeight = mGridView.getHeight();//getMeasuredHeight();

                int statusbarHeight = getStatusBarHeight(getApplicationContext())*3;
                int requiredHeight = displayHeight - statusbarHeight;

                mColumnWidth = displayWidth / COLUMNS;
                //mColumnHeight = (displayHeight / COLUMNS);
                mColumnHeight = requiredHeight / COLUMNS;

                display(getApplicationContext());
            }
        });
    }

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public static void display(Context context) {
        ArrayList<Button> buttons = new ArrayList<>();
        Button button;

        for (int i = 0; i < tileList.length; i++) {
            button = new Button(context);
            for(int j = 0; j < tileList.length; j++) {
                if (tileList[i].equals(String.valueOf(j))) {
                    button.setBackground(imagenes.get(j));
                    buttons.add(button);
                }
            }
        }

        mGridView.setAdapter(new PuzzleAdapter(buttons, mColumnWidth, mColumnHeight));
    }

    public static void swap(Context context, int currentPosition, int swap) {
        String newPosition = tileList[currentPosition + swap];
        tileList[currentPosition + swap] = tileList[currentPosition];
        tileList[currentPosition] = newPosition;
        display(context);

        if (isSolved()) {
            double score = Util.puzzleSolvedScore(chronometer.getText());
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
//                    String scoreFormat = String.format("%.2f",scoreFile);
//                    Double scoreToFile = Double.valueOf(scoreFormat);
//                    double scoreToFile = Double.parseDouble(scoreFormat);
                obj.put("score", scoreToFile);
                Util.writeJSONToFilesDir(ctx, "userInfo", obj.toString());
                SharedPreferences scoreSP = ctx.getSharedPreferences("scoreEvent", 0);
                SharedPreferences.Editor scoreEditor = scoreSP.edit();
                scoreEditor.putString("score", String.valueOf(score));
                scoreEditor.commit();
                SharedPreferences nameFileSP = ctx.getSharedPreferences("nameFileSP", 0);
                SharedPreferences.Editor nameFileEditor = nameFileSP.edit();
                nameFileEditor.putString("fileName", markName);
                nameFileEditor.commit();
                Intent intent = new Intent(ctx, DidYouKnowActivity.class);
                ctx.startActivity(intent);
                activity.finish();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    public static void moveTiles(Context context, String direction, int position) {

        // Upper-left-corner tile
        if (position == 0) {

            if (direction.equals(right)) swap(context, position, 1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Upper-center tiles
        } else if (position > 0 && position < COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Upper-right-corner tile
        } else if (position == COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Left-side tiles
        } else if (position > COLUMNS - 1 && position < DIMENSIONS - COLUMNS &&
                position % COLUMNS == 0) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Right-side AND bottom-right-corner tiles
        } else if (position == COLUMNS * 2 - 1 || position == COLUMNS * 3 - 1) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) {

                // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                // right-corner tile.
                if (position <= DIMENSIONS - COLUMNS - 1) swap(context, position,
                        COLUMNS);
                else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Bottom-left corner tile
        } else if (position == DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Bottom-center tiles
        } else if (position < DIMENSIONS - 1 && position > DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Center tiles
        } else {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(right)) swap(context, position, 1);
            else swap(context, position, COLUMNS);
        }
    }

    public static boolean isSolved() {
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

    @Override
    protected void onPause(){
        super.onPause();
        chronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
    }

    @Override
    protected void onResume(){
        super.onResume();
        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        chronometer.start();
    }
}
