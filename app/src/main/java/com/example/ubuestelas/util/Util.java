package com.example.ubuestelas.util;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {

    static final double RADIO_TIERRA = 6371;

    public static String loadJSONFromAsset(Context context, String name) {
        String json;
        try {
            InputStream is = context.getAssets().open(name);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public static String loadJSONFromFilesDir(Context context, String name) {
        String json;
        try {
//            FileInputStream fis = context.openFileInput(name);
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader bufferedReader = new BufferedReader(isr);
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                sb.append(line);
//            }
            //TODO arreglar lectura de fichero
            File file = new File(context.getFilesDir(), name);
            StringBuffer output = new StringBuffer();
//            FileReader fileReader = new FileReader(file);
//            FileInputStream fileInputStream = new FileInputStream(context.getFilesDir()+ "/" + name);
//            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                output.append(line + "\n");
            }
            json = output.toString();
            bufferedReader.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }


    public static double getDistanceFromLatLong(LatLng latLng1, LatLng latLng2){
        double p = Math.PI/180;
        double distance;
        double km;
        distance=0.5-Math.cos((latLng2.latitude - latLng1.latitude) * p)/2 +
                Math.cos(latLng1.latitude * p) * Math.cos(latLng2.latitude * p) *
                        (1 - Math.cos((latLng2.longitude - latLng1.longitude) * p))/2;
        km = 2*RADIO_TIERRA * Math.asin(Math.sqrt(distance));
        return km * 1000; //distancia en metros

    }
}
