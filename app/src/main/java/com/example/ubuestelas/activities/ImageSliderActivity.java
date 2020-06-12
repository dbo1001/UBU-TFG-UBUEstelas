package com.example.ubuestelas.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;
import com.github.amlcurran.showcaseview.ShowcaseView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ImageSliderActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        init();
        SharedPreferences imageSliderActivitySP= getSharedPreferences("imageSliderActivity", 0);
        boolean first = imageSliderActivitySP.getBoolean("first", true);
        if(first) {
            Button button = new Button(this);
            button.setText(R.string.ok);
            button.setTextSize(24);
            button.setTextColor(Color.BLACK);
            button.setBackgroundColor(Color.WHITE);
//            button.setBackground(getResources().getDrawable(R.drawable.border));
//        Target target = new ViewTarget(R.id.hint_test, this);
            new ShowcaseView.Builder(this)
//                .setTarget(target)
                    .setContentTitle(R.string.information)
                    .setContentText(R.string.environment_text)
                    .replaceEndButton(button)
                    .setStyle(R.style.CustomShowcaseTheme)
                    .hideOnTouchOutside()
                    .build();
        }
        SharedPreferences.Editor imageSAEditor = imageSliderActivitySP.edit();
        imageSAEditor.putBoolean("first", false);
        imageSAEditor.apply();
    }

    private void init(){
        constraintLayout = findViewById(R.id.image_slider_constraint);
        iv = new ImageView(this);
        List<String> closeMarks = (List<String>) getIntent().getSerializableExtra("closeMarks");
        LinearLayout linearLayoutV = findViewById(R.id.layout_image_slider);
        JSONObject objMarks;
        try {
            objMarks = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), "marksJSON.json"));
            JSONArray allMarks = objMarks.getJSONArray("marks");
            for (String mark : closeMarks) {
                TextView markTV = new TextView(this);
                markTV.setText(mark+":");
                markTV.setTextSize(24);
                markTV.setTextColor(Color.BLACK);
                markTV.setBackground(getDrawable(R.drawable.border));
                markTV.setPadding(10,0,10,0);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10,30,10,10);
                markTV.setLayoutParams(layoutParams);
                markTV.setGravity(Gravity.CENTER);
                markTV.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
                final LinearLayout linearLayoutH = new LinearLayout(this);
                linearLayoutH.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayoutH.setOrientation(LinearLayout.HORIZONTAL);
                for(int i=0; i<allMarks.length(); i++){
                    JSONObject markJO = allMarks.getJSONObject(i);
                    String markName= markJO.getString("description");
                    if(markName.equals(mark)){
                        JSONArray pictures = markJO.getJSONArray("picturesAround");
                        for(int j=0; j<pictures.length(); j++) {
                            JSONObject picture = pictures.getJSONObject(j);
                            final String pictureName = picture.getString("pictureName");
                            final ImageView imageView = new ImageView(this);
                            imageView.setImageBitmap(getImageSmallResized(getResources().getIdentifier(pictureName, "drawable", this.getPackageName())));
//                            imageView.setImageResource(getResources().getIdentifier(pictureName, "drawable", this.getPackageName()));
                            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            ll.setMargins(20,0,0,0);
                            imageView.setLayoutParams(ll);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    iv.setImageBitmap(getImageScreenResized(getResources().getIdentifier(pictureName, "drawable", getApplicationContext().getPackageName())));
                                    iv.setBackgroundColor(getColor(R.color.colorSemiTransparent));
                                    LinearLayout.LayoutParams vgLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                    vgLp.gravity=Gravity.CENTER;
                                    iv.setLayoutParams(vgLp);
                                    constraintLayout.addView(iv);
                                    iv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(hasImage(iv)){
                                                constraintLayout.removeView(iv);
                                                iv.setImageDrawable(null);
                                            }
//                                            constraintLayout.removeView(v);
//                                            iv = new ImageView(getApplicationContext());
//                                            iv.setImageDrawable(null);
                                        }
                                    });
                                }
                            });
                            linearLayoutH.addView(imageView);
                        }
                    }
                }
                horizontalScrollView.addView(linearLayoutH);
                linearLayoutV.addView(markTV);
                linearLayoutV.addView(horizontalScrollView);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private Bitmap getImageSmallResized(int image){
        Bitmap b = BitmapFactory.decodeResource(getResources(),image);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widthDisplay = size.x;
        int heightDisplay = size.y;
        int width;
        int height;
        if(widthDisplay<heightDisplay) {
            width = widthDisplay / 3;
            height = (b.getHeight() * width) / b.getWidth();
        }else{
            height = heightDisplay / 3;
            width = (b.getWidth() * height) / b.getHeight();
        }
        return Bitmap.createScaledBitmap(b, width, height, true);
    }

    private Bitmap getImageScreenResized(int image){
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) image;
//        Bitmap b = bitmapDrawable.getBitmap();
        Bitmap b = BitmapFactory.decodeResource(getResources(),image);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widthDisplay = size.x;
        int heightDisplay = size.y;
        int width;
        int heigth;
        if(widthDisplay<heightDisplay) {
            width = widthDisplay;
            heigth = (b.getHeight() * width) / b.getWidth();
        }else{
            heigth = heightDisplay;
            width = (b.getWidth() * heigth) / b.getHeight();
        }
        return Bitmap.createScaledBitmap(b, width, heigth, true);
    }

    @Override
    public void onBackPressed(){
        if(hasImage(iv)){
            constraintLayout.removeView(iv);
            iv.setImageDrawable(null);
        }else {
            super.onBackPressed();
        }
    }

    private boolean hasImage(ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }
}
