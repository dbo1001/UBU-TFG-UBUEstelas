package com.example.ubuestelas.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Util {

    static final double RADIO_TIERRA = 6371;

    /**
     * Carga un archivo de tipo JSON de la carpeta de assets.
     * @param context Contexto de la apliación.
     * @param name Nombre del archivo a cargar.
     * @return Devuelve una cadena con el contenido del JSON.
     */
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

    /**
     * Carga un archivo de tipo JSON de la memoria del dispositivo.
     * @param context Contexto de la apliación.
     * @param name Nombre del archivo a cargar.
     * @return Devuelve una cadena con el contenido del JSON.
     */
    public static String loadJSONFromFilesDir(Context context, String name) {
        String json;
        try {
            File file = new File(context.getFilesDir(), name);
            StringBuffer output = new StringBuffer();
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

    /**
     * Calcula la distancia en metros entre dos puntos conociendo su latitud y longitud en el mapa.
     * @param latLng1 Latitud y longitud del punto 1.
     * @param latLng2 Latitud y longitud del punto 2.
     * @return Distancia en metros entre los dos puntos.
     */
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

    /**
     * Calcula la puntuación de la prueba tipo test cuando se ha fallado.
     * @param optionsNumber Número de opciones del test.
     * @param attemptNumber Número de intentos que ha tardado en conseguirlo.
     * @return Puntuación obtenida.
     */
    public static double testScoreIfFail(double optionsNumber, double attemptNumber, boolean hint, Context context){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String difficulty = sharedPreferences.getString("difficulty", "easy");
        double score;
        if (attemptNumber >= optionsNumber){
            return 0;
        }
        score=1-(attemptNumber/optionsNumber);
        score *= 100;
        double scoreF = Math.round(score*100)/100.0;
        if(hint){
            switch (difficulty){
                case "easy":
                    scoreF = scoreF-(scoreF * 0.05);
                    break;
                case "normal":
                    scoreF = scoreF-(scoreF * 0.1);
                    break;
                case "hard":
                    scoreF = scoreF-(scoreF * 0.2);
                    break;
            }
        }
        return scoreF;
    }

    /**
     * Escribe en un fichero de tipo JSON la información pasada.
     * @param context Contexto de la aplicación.
     * @param name Nombre del fichero.
     * @param data Datos que se quieren guardar.
     */
    public static void writeJSONToFilesDir(Context context, String name, String data){
        try {
            File file = new File(context.getFilesDir(), name);
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(data);
            bufferedWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Calcula la puntuación obtenida en la prueba de tipo puzzle en función del tiempo tardado
     * y de la dificultad seleccionada para jugar.
     * @param context Contexto de la aplicación.
     * @param text Cadena de texto con el tiempo tardado con formato MM:SS
     * @return Puntuación obtenida.
     */
    public static double puzzleSolvedScore(Context context, CharSequence text, boolean hint) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String difficulty = sharedPreferences.getString("difficulty", "easy");
        double score = 0;
        String minutes = "" + text.charAt(0) + text.charAt(1);
        String seconds = "" + text.charAt(3) + text.charAt(4);
        double secondsTot = (Integer.parseInt(minutes)*60)+ Integer.parseInt(seconds);
        switch (difficulty){
            case "easy":
                if(secondsTot<=90){
                    score=100;
                }else if (secondsTot > 90 && secondsTot <=180){
                    double diff = secondsTot - 90;
                    score=100-diff;
                }else{
                    score=0;
                }
                if(hint){
                    score = score-(score * 0.05);
                }
                break;
            case "normal":
                if(secondsTot<=60){
                    score=100;
                }else if (secondsTot > 60 && secondsTot <=120){
                    double diff = secondsTot - 60;
                    score=100-diff;
                }else{
                    score=0;
                }
                if(hint){
                    score = score-(score * 0.1);
                }
                break;
            case "hard":
                if(secondsTot<=45){
                    score=100;
                }else if (secondsTot > 45 && secondsTot <=90){
                    double diff = secondsTot - 45;
                    score=100-diff;
                }else{
                    score=0;
                }
                if(hint){
                    score = score-(score * 0.2);
                }
                break;
        }

        return score;
    }

    /**
     * Calcula la puntuación obtenida en la prueba de tipo completar palabras.
     * @param attemptNumber Número de intentos para conseguirlo.
     * @param gapNumber Número de huecos en la frase.
     * @param errorsAttempt Lista con el número de errores en cada intento
     * @param lettersNumber Número de opciones para cada hueco.
     * @return Puntuación obtenida.
     */
    public static double completeWordsScoreIfFail(double attemptNumber, double gapNumber, List<Double> errorsAttempt, double lettersNumber, boolean hint, Context context){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String difficulty = sharedPreferences.getString("difficulty", "easy");
        double score = 0;
        double counter=0;
        if (attemptNumber >= gapNumber){
            return 0;
        }
        for (Double wrongs : errorsAttempt){
            counter++;
            if(counter==1) {
                score = gapNumber - (wrongs/2) - wrongs*(1/(lettersNumber));
            }else{
                score = score - wrongs * (1/(lettersNumber-counter));
            }
        }
        if(score<0){
            score=0;
        }
        score = score * 100 / gapNumber;
        score = Math.round(score*100)/100.0;
        if(hint){
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
        return score;
    }
}
