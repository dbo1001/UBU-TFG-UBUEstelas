package com.rutaestelas.ubuestelas.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.rutaestelas.ubuestelas.R;
import com.rutaestelas.ubuestelas.util.Util;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Actividad donde se encuentra el menú lateral y lo relacionado con la carga del mapa y de las actividades.
 *
 * @author Marcos Pena
 */
public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions currentLocation;
    private final List<Marker> markerList = new ArrayList<>();
    private Marker currentLocationMarker;
    private HashMap<Marker,List<String>> dicMarkerAct;
    private Marker currentMarkerActivity = null;

    private boolean finish = false;

    AlertDialog dialog = null;
    AlertDialog updatedDialog = null;


    /**
     * Inicializa la actividad con su respectivo layout. Se inicializa el menú lateral y el mapa.
     * Se comprueba si tiene los permisos activados y en caso contrario vuelve a solicitarlos.
     * Se llama a otros métodos para inicializar el resto de la actividad.
     * @param savedInstanceState Si la actividad se ha reiniciado se le pasa el contenido de datos más reciente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        updatePreferences();

    }

    /**
     * Cuando se presiona el botón de atras, si el menú lateral izquierdo está abierto, lo cierra.
     * En caso contrario realiza la función con normalidad.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Carga los datos de puntuación y nombre en el menú lateral.
     * @param menu El menú en el que se ponen los items.
     * @return true para que se muestre, false para que no.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences sharedPref= getSharedPreferences("nameActivity",0);
        String name = sharedPref.getString("name", "amigo");
        TextView textViewName = findViewById(R.id.user_name);
        textViewName.setText(name);
        TextView textViewScore = findViewById(R.id.score);
        textViewScore.setText(getScoreOutOfTotal());
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    /**
     * Cuando se selecciona un item en la barra de herramientas realiza la acción que se le indique.
     * @param item El item del menú que se ha seleccionado.
     * @return Heredado del método sobreescrito.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Cuando un item del menú lateral es seleccionado, relaliza la acción que se le indica.
     * @param item El item del menú que se ha seleccionado.
     * @return true muestra el item como seleccionado, false no lo hace
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            finish();
        //Apartado donde se encontraría el ranking
//        } else if (id == R.id.nav_ranking) {

        } else if (id == R.id.nav_end_game) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.end_game)
                    .setMessage(R.string.end_game_confirmation)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), EndGameActivity.class);
                            startActivity(intent);
                            NavigationDrawerActivity.this.finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.shared_text, String.valueOf(getScore())));
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Este método llama a funciones para cargar el tipo de mapa, añadir los marcadores y
     * poner la localización del jugador.
     * @param googleMap Una instancia del GoogleMap asociada con el fragmento
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapType();
        addJSONmarkersAndFillDic();
        getCurrentLocation();
        SharedPreferences newNavigationDrawerActivitySP= getSharedPreferences("newNavigationDrawerActivity", 0);
        boolean first = newNavigationDrawerActivitySP.getBoolean("first", true);
        if(first) {
            Button button = new Button(this);
            button.setText(R.string.ok);
            button.setTextSize(24);
            button.setTextColor(Color.BLACK);
            button.setBackgroundColor(Color.WHITE);

            new ShowcaseView.Builder(this)
                    .withNewStyleShowcase()
                    .setContentTitle(R.string.game_explain)
                    .replaceEndButton(button)
                    .setStyle(R.style.CustomShowcaseThemeDYKA)
                    .hideOnTouchOutside()
                    .build();

        }
        SharedPreferences.Editor newNavigationDrawerActivityEditor = newNavigationDrawerActivitySP.edit();
        newNavigationDrawerActivityEditor.putBoolean("first", false);
        newNavigationDrawerActivityEditor.apply();
    }

    /**
     * Called when pointer capture is enabled or disabled for the current window.
     *
     * @param hasCapture True if the window has pointer capture.
     */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * Este método carga, según lo que ponga el fichero de posiciones, los marcadores en el mapa. También según
     * lo que ponga el fichero de guardado pone los colores correspondientes a cada marcador y guarda en un diccionario
     * la relación entre cada marcador con su tipo de prueba y su fichero.
     */
    private void addJSONmarkersAndFillDic(){
        JSONObject objMarks;
        JSONObject objUserInfo;
        for (Marker m : markerList){
            m.remove();
        }
        markerList.clear();
        dicMarkerAct = new HashMap<>();
        try {
            objUserInfo = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
            JSONArray marksColors = objUserInfo.getJSONArray("marks");
            objMarks = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), "marksJSON.json"));
            JSONArray townCentre = objMarks.getJSONArray("townCentre");
            JSONObject town = townCentre.getJSONObject(0);
            int markImageWrongAnswer = getResources().getIdentifier(objMarks.getString("markImageWrongAnswer"), "drawable", getPackageName());
            int markImageNotAnswered = getResources().getIdentifier(objMarks.getString("markImageNotAnswered"), "drawable", getPackageName());
            int markImageCorrectAnswer = getResources().getIdentifier(objMarks.getString("markImageCorrectAnswer"), "drawable", getPackageName());
            int markImageMidAnswer = getResources().getIdentifier(objMarks.getString("markImageMidAnswer"), "drawable", getPackageName());
            LatLng townLatLng = new LatLng(town.getDouble("latitude"), town.getDouble("longitude"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(townLatLng, 17.5f));
            JSONArray marks = objMarks.getJSONArray("marks");

            for (int i = 0; i < marks.length(); i++) {
                JSONObject mark = marks.getJSONObject(i);
                JSONObject markColor = marksColors.getJSONObject(i);
                LatLng stelaLatLng = new LatLng(mark.getDouble("latitude"), mark.getDouble("longitude"));
                Marker marker = mMap.addMarker(new MarkerOptions().position(stelaLatLng).title(mark.getString("description")));//.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                switch (getColorForMarker(markColor.getString("mark"))){
                    case "green":
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBannerSized(markImageCorrectAnswer)));
                        break;
                    case "red":
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBannerSized(markImageWrongAnswer)));
                        break;
                    case "yellow":
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBannerSized(markImageMidAnswer)));
                        break;
                    case "azure":
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBannerSized(markImageNotAnswered)));
                        break;
                }
                markerList.add(marker);
                List<String> typeAndFile = new ArrayList<>();
                typeAndFile.add(mark.getString("type"));
                typeAndFile.add(mark.getString("fileName"));
                dicMarkerAct.put(marker,typeAndFile);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la localización del jugador y el personaje que ha elegido y lo coloca en el mapa.
     * También llama a otros métodos para comprobar la cercanía a los marcadores necesarios para iniciar
     * cada una de las pruebas y crea el diálogo donde se le permite al usuario escoger el marcador con el que
     * quiere jugar.
     */
    private void getCurrentLocation(){
        final Context context = this;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SharedPreferences sharedPrefChar = getSharedPreferences("characterSelected",0);
        final int characterDrawable = sharedPrefChar.getInt("drawableCharac",R.drawable.character01);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        try {
            JSONObject obj = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
            finish = obj.getBoolean("finish");
        }catch (JSONException e){
            e.printStackTrace();
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0.5f, new LocationListener() {
            List<String> prevNameCloseMarkers = new ArrayList<>();
            final Bitmap characterSized = getCharacterSized(characterDrawable);

            /**
             * Cuando cambia la ubicación del usuario se llama a este método. Colocar su posición en el mapa
             * y lanza el dialogo para que escoja el marcado con el que quiere jugar.
             *
             * @param location La localización del jugador.
             */
            @Override
            public void onLocationChanged(Location location) {

                if (currentLocationMarker != null) {
                    currentLocationMarker.remove();
                }
                currentLocation = new MarkerOptions();

                currentLocation.position(new LatLng(location.getLatitude(), location.getLongitude())).title("Estoy aquí").icon(BitmapDescriptorFactory.fromBitmap(characterSized)).zIndex(1.0f);

                currentLocationMarker = mMap.addMarker(currentLocation);

                final List<Marker> closeMarkers = getCloseMarkers();
                final List<String> nameCloseMarkers = new ArrayList<>();

                for (Marker marker : closeMarkers) {
                    nameCloseMarkers.add(marker.getTitle());
                }

                if (!prevNameCloseMarkers.equals(nameCloseMarkers) && !nameCloseMarkers.isEmpty()) {
                    if(!finish) {
                        createMarksDialogBuilder(nameCloseMarkers, closeMarkers);
                        dialog = builder.create();
                        if(!((Activity) context).isFinishing()) {
                            dialog.show();

                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    if (marker.isInfoWindowShown()) {
                                        marker.hideInfoWindow();
                                    } else {
                                        marker.showInfoWindow();
                                    }
                                    final List<String> updatedNameCloseMarkers = new ArrayList<>();
                                    List<Marker> updatedCloseMarkers = getCloseMarkers();
                                    for (Marker mm : updatedCloseMarkers) {
                                        updatedNameCloseMarkers.add(mm.getTitle());
                                    }
                                    for (Marker m : updatedCloseMarkers) {
                                        if (m.equals(marker)) {
                                            createMarksDialogBuilder(updatedNameCloseMarkers, updatedCloseMarkers);
                                            updatedDialog = builder.create();
                                            updatedDialog.show();
                                            ImageView imgV = updatedDialog.findViewById(R.id.iconInfo);
                                            imgV.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    updatedDialog.dismiss();
                                                    Intent intent = new Intent(getBaseContext(), ImageSliderActivity.class);
                                                    intent.putExtra("closeMarks", (Serializable) updatedNameCloseMarkers);
                                                    startActivity(intent);
                                                }
                                            });
                                            break;
                                        }
                                    }
                                    return true;
                                }
                            });
                            ImageView imgV = dialog.findViewById(R.id.iconInfo);
                            imgV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getBaseContext(), ImageSliderActivity.class);
                                    intent.putExtra("closeMarks", (Serializable) nameCloseMarkers);
                                    startActivity(intent);
                                }
                            });
                        }
                    }


                }
                TextView textViewScore = findViewById(R.id.score);
                textViewScore.setText(getScoreOutOfTotal());
                prevNameCloseMarkers = new ArrayList<>(nameCloseMarkers);
            }

            void createMarksDialogBuilder(List<String> nameCloseMarkers, final List<Marker> closeMarkers) {
                String[] closeMarkersString = new String[nameCloseMarkers.size()];
                closeMarkersString = nameCloseMarkers.toArray(closeMarkersString);
                builder.setItems(closeMarkersString, new DialogInterface.OnClickListener() {
                    /**
                     * Carga el tipo de prueba que corresopnda al marcador seleccionado.
                     * @param dialog Diálogo del que se selecciona un item.
                     * @param which Item seleccionado dentro de la lista.
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentMarkerActivity = closeMarkers.get(which);
                        List<String> markerChosen = dicMarkerAct.get(currentMarkerActivity);
                        if(markerChosen == null){
                            return;
                        }
                        SharedPreferences fileNameSP = getSharedPreferences("navDrawFileName", 0);
                        SharedPreferences.Editor nameEditor = fileNameSP.edit();
                        String fileNameChosen = markerChosen.get(1);
                        nameEditor.putString("fileName", fileNameChosen);
                        nameEditor.apply();
                        Intent intent;
                        boolean first;
                        List<Boolean> completed = checkCompletedMarkers();
                        first = !completed.contains(Boolean.TRUE);
                        switch (markerChosen.get(0)) {
                            case "test":
                                intent = new Intent(getBaseContext(), TypeTestActivity.class);
                                intent.putExtra("first_game", first);
                                startActivity(intent);
                                break;
                            case "puzzle":
                                intent = new Intent(getBaseContext(), TypePuzzleActivity.class);
                                intent.putExtra("first_game", first);
                                startActivity(intent);
                                break;
                            case "complete_words":
                                intent = new Intent(getBaseContext(), TypeCompleteWordsActivity.class);
                                intent.putExtra("first_game", first);
                                startActivity(intent);
                                break;
                        }
                    }
                });
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.custom_title_dialog_alert, null);
                builder.setCustomTitle(view);
            }

            /**
             * Hace un cálculo del tamaño del personaje en función del tamaño de la pantalla.
             *
             * @param character El persoaje del que se quiere redimensionar.
             * @return Bitmap del tamaño proporcionado del personaje.
             */
            Bitmap getCharacterSized(int character) {
                Bitmap b = BitmapFactory.decodeResource(getResources(), character);
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int widthDisplay = size.x;
                int heightDisplay = size.y;
                int width;
                int heigth;
                if (widthDisplay < heightDisplay) {
                    width = (int) (widthDisplay * 0.1);
                    heigth = (b.getHeight() * width) / b.getWidth();
                } else {
                    heigth = (int) (heightDisplay * 0.2);
                    width = (b.getWidth() * heigth) / b.getHeight();
                }
                return Bitmap.createScaledBitmap(b, width, heigth, true);
            }

            /**
             * Cuando el usuario desactiva su ubicación, muestra un mensaje pidiendo que se vuelva a activar.
             * @param provider
             */
            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(), getString(R.string.disabled_location), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }
        });
    }

    /**
     * Hace un cálculo del tamaño de la imagen del marcador en función del tamaño de la pantalla.
     * @param banner Imagen del marcador que se quiere redimensionar.
     * @return Bitmap del marcador con el tamaño proporcionado.
     */
    private Bitmap getBannerSized(int banner){
        BitmapDrawable bitmapdraw = (BitmapDrawable)getDrawable(banner);
        Bitmap b = bitmapdraw.getBitmap();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widthDisplay = size.x;
        int heightDisplay = size.y;
        int width;
        int heigth;
        if(widthDisplay<heightDisplay) {
            width = (int) (widthDisplay * 0.1);
            heigth = (b.getHeight() * width) / b.getWidth();
        }else{
            heigth = (int) (heightDisplay * 0.2);
            width = (b.getWidth() * heigth) / b.getHeight();
        }
        return Bitmap.createScaledBitmap(b, width, heigth, true);
    }

    /**
     * Comprueba los marcadores que están cerca de la posición del jugador.
     * @return Lista con los marcadores a una distancia menor a 10 metros.
     */
    private List<Marker> getCloseMarkers(){
        List<Boolean> completedMarkers = checkCompletedMarkers();
        List<Marker> closeMarkers= new ArrayList<>();
        int counter = 0;
        for (Marker marker : markerList) {
            if (!completedMarkers.get(counter)) {
                if (Util.getDistanceFromLatLong(currentLocationMarker.getPosition(), marker.getPosition()) <= 10) {
                    closeMarkers.add(marker);
                }
            }
            counter++;
        }
        return closeMarkers;
    }

    /**
     * Comprueba de cuales de los marcadores ya han sido completadas sus pruebas.
     * @return Lista con los marcadores ya resueltos.
     */
    private List<Boolean> checkCompletedMarkers(){
        JSONObject obj;
        List<Boolean> completedMarkers= new ArrayList<>();
        try {
            obj = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
            JSONArray marks = obj.getJSONArray("marks");
            for (int i = 0; i < marks.length(); i++){
                JSONObject mark = marks.getJSONObject(i);
                completedMarkers.add(mark.getBoolean("solved"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return completedMarkers;
    }

    /**
     * Método para obtener la impresión de la puntuación en pantalla en función de la cantidad
     * de pruebas en el mapa.
     * @return String con la puntuación actual sobre la puntuación total.
     */
    private String getScoreOutOfTotal(){
        JSONObject obj;
        double score=0.0;
        try {
            obj = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
            score = obj.getDouble("score");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return score +"/"+ markerList.size() * 100;
    }

    private double getScore(){
        JSONObject obj;
        double score=0.0;
        try {
            obj = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
            score = obj.getDouble("score");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return score;
    }

    /**
     * Método donde se comprueba que color debe tener el marcador en función de su resultado o de si ha sido resuelta.
     * @param nMark Marcador del que se quiere comprobar su color.
     * @return String con el nombre del color solicitado.
     */
    private String getColorForMarker(String nMark){
        JSONObject obj;
        String color ="";
        try {
            obj = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
            JSONArray marks = obj.getJSONArray("marks");
            for (int i = 0; i < marks.length(); i++){
                JSONObject mark = marks.getJSONObject(i);
                if(nMark.equals(mark.getString("mark"))){
                    color = mark.getString("color");
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return color;
    }

    /**
     * Método que actualiza algunas de las características de la aplicación en función de lo
     * que haya decidido en usuario en los ajustes.
     * Se actualiza cada vez que el usuario cambia algún parámetro en los ajustes
     */
    private void updatePreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this );
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("map_type")) {
                    setMapType();
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Pone el mapa sobre el que se está jugando con el tipo de mapa que decida el usario
     * en los ajustes.
     */
    private void setMapType(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this );
        String map_type = sharedPreferences.getString("map_type", "MAP_TYPE_SATELLITE");
        switch (map_type){
            case "MAP_TYPE_SATELLITE":
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case "MAP_TYPE_TERRAIN":
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case "MAP_TYPE_NORMAL":
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
    }

    @Override
    public void onRestart(){
        super.onRestart();
        setMapType();
        addJSONmarkersAndFillDic();
        TextView textViewScore = findViewById(R.id.score);
        textViewScore.setText(getScoreOutOfTotal());
        checkEndGame();
        SharedPreferences didYouKnowActivitySP= getSharedPreferences("didYouKnowActivity", 0);
        SharedPreferences.Editor didYouKnowActivityEditor = didYouKnowActivitySP.edit();
        didYouKnowActivityEditor.putBoolean("firstAudio", true);
        didYouKnowActivityEditor.apply();
    }

    private void checkEndGame(){
        List<Boolean> marksCompleted = checkCompletedMarkers();
        if(!marksCompleted.contains(false)){
            Intent intent = new Intent(this, EndGameActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        if(updatedDialog != null) {
            if (updatedDialog.isShowing()) {
                updatedDialog.dismiss();
            }
        }
    }


}
