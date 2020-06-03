package com.example.ubuestelas.activities;

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
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.location.FusedLocationProviderClient;
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
 * Actividad donde se encuentra el menú lateral y todo lo relacionado con la carga del mapa y de las actividades.
 *
 * @author Marcos Pena
 */
public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    MarkerOptions currentLocation;
    List<Marker> markerList = new ArrayList<Marker>();
    Marker currentLocationMarker;
    HashMap<Marker,List<String>> dicMarkerAct;
    Marker currentMarkerActivity = null;
    public String difficulty;

    int markImageWrongAnswer;
    int markImageNotAnswered;
    int markImageCorrectAnswer;
    int markImageMidAnswer;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    boolean finish = false;

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

        setDifficulty();
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
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
        }
    }

    /**
     * Carga los datos de puntuación y nombre en el menú lateral.
     * @param menu El menú en el que se ponen los items.
     * @return true para que se muestre, false para que no.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        SharedPreferences sharedPref= getSharedPreferences("nameActivity",0);
        String name = sharedPref.getString("name", "amigo");
        TextView textViewName = findViewById(R.id.user_name);
        textViewName.setText(name);
        //Se setea también el valor inicial del score en el menú lateral
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_ranking) {

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
//                            ((Activity) getApplicationContext()).finish();
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

        } //else if (id == R.id.nav_send) {

//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Este método llama a funciones para cargar el tipo de mapa, añadir los marcadores y
     * poner la localización del jugador.
     * TODO En lugar de requiresApi se puede ver de hacer alguna forma para soportar versiones anteriores metiéndolo en un if
     * @param googleMap Una instancia del GoogleMap asociada con el fragmento
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapType();
        addJSONmarkersAndFillDic();
        getCurrentLocation();
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
     * Este método carga, según lo que ponga el fichero de posiciones, los marcadores en el mapa. TAmbién según
     * Lo que ponga el fichero de guardado pone los colores correspondientes a cada marcador y guarda en un diccionario
     * la relación entre cada marcador con su tipo de prueba y su fichero.
     */
    public void addJSONmarkersAndFillDic(){
        JSONObject objMarks;
        JSONObject objUserInfo;
        for (Marker m : markerList){
            m.remove();
        }
        markerList.clear();
//        markerList = new ArrayList<Marker>();
        dicMarkerAct = new HashMap<Marker,List<String>>();
        try {
            objUserInfo = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
            JSONArray marksColors = objUserInfo.getJSONArray("marks");
            objMarks = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), "marksJSON.json"));
            JSONArray townCentre = objMarks.getJSONArray("townCentre");
            JSONObject town = townCentre.getJSONObject(0);
            markImageWrongAnswer = getResources().getIdentifier(objMarks.getString("markImageWrongAnswer"), "drawable", getPackageName());
            markImageNotAnswered = getResources().getIdentifier(objMarks.getString("markImageNotAnswered"), "drawable", getPackageName());
            markImageCorrectAnswer = getResources().getIdentifier(objMarks.getString("markImageCorrectAnswer"), "drawable", getPackageName());
            markImageMidAnswer = getResources().getIdentifier(objMarks.getString("markImageMidAnswer"), "drawable", getPackageName());
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
//                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.estandarte_verde));
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBannerSized(markImageCorrectAnswer)));
                        break;
                    case "red":
//                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBannerSized(markImageWrongAnswer)));
                        break;
                    case "yellow":
//                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBannerSized(markImageMidAnswer)));
                        break;
                    case "azure":
//                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.estandarte_azul));
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBannerSized(markImageNotAnswered)));
                        break;
                }
                markerList.add(marker);
                List<String> typeAndFile = new ArrayList<String>();
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
     * TODO En lugar de requiresApi se puede ver de hacer alguna forma para soportar versiones anteriores metiéndolo en un if
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getCurrentLocation(){
        final Context context = this;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
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
            Bitmap characterSized = getCharacterSized(characterDrawable);

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
//                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    nameCloseMarkers.add(marker.getTitle());
                }

                if (!prevNameCloseMarkers.equals(nameCloseMarkers) && !nameCloseMarkers.isEmpty()) {
                    //builder.setTitle(getString(R.string.choose_stela));
//                    String[] closeMarkersString = new String[nameCloseMarkers.size()];
//                    closeMarkersString = nameCloseMarkers.toArray(closeMarkersString);
//                    builder.setItems(closeMarkersString, new DialogInterface.OnClickListener() {
//                        /**
//                         * Carga el tipo de prueba que corresopnda al marcador seleccionado.
//                         * @param dialog Diálogo del que se selecciona un item.
//                         * @param which Item seleccionado dentro de la lista.
//                         */
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            currentMarkerActivity=closeMarkers.get(which);
//                            List<String> markerChosen = dicMarkerAct.get(currentMarkerActivity);
//                            SharedPreferences fileNameSP = getSharedPreferences("navDrawFileName", 0);
//                            SharedPreferences.Editor nameEditor = fileNameSP.edit();
//                            String fileNameChosen = markerChosen.get(1);
//                            nameEditor.putString("fileName", fileNameChosen);
//                            nameEditor.commit();
//                            Intent intent;
//                            boolean first;
//                            List<Boolean> completed = checkCompletedMarkers();
//                            if(completed.contains(Boolean.TRUE)){
//                                first=false;
//                            }else{
//                                first=true;
//                            }
//                            switch (markerChosen.get(0)){
//                                case "test":
//                                    intent = new Intent(getBaseContext(), TypeTestActivity.class);
//                                    intent.putExtra("first_game", first);
//                                    startActivity(intent);
//                                    break;
//                                case "puzzle":
//                                    intent = new Intent(getBaseContext(), TypePuzzleActivity.class);
//                                    intent.putExtra("first_game", first);
//                                    startActivity(intent);
//                                    break;
//                                case "complete_words":
//                                    intent = new Intent(getBaseContext(), TypeCompleteWordsActivity.class);
//                                    intent.putExtra("first_game", first);
//                                    startActivity(intent);
//                                    break;
//                            }
//                        }
//                    });


                    //builder.setIcon(R.drawable.ic_info_images);
//                    LayoutInflater inflater = getLayoutInflater();
//                    View view = inflater.inflate(R.layout.custom_title_dialog_alert, null);
//                    builder.setCustomTitle(view);

                    if(!finish) {
                        createMarksDialogBuilder(nameCloseMarkers, closeMarkers);
                        final AlertDialog dialog = builder.create();
                        if(!((Activity) context).isFinishing()) {
                            dialog.show();


//                    Target target = new ViewTarget(R.id.iconInfo, getParent());
//                    new ShowcaseView.Builder(getParent())
//                            .setTarget(target)
//                            .setContentTitle(R.string.hint)
//                            .setContentText(R.string.hint_explain)
//                            //.replaceEndButton(button)
//                            .setStyle(R.style.CustomShowcaseTheme)
//                            .hideOnTouchOutside()
//                            .build();

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
                                            AlertDialog updatedDialog = builder.create();
                                            updatedDialog.show();
                                            ImageView imgV = (ImageView) updatedDialog.findViewById(R.id.iconInfo);
                                            imgV.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
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

//                    dialogBuilder.setTitle("Estas son las imagenes de las estelas");
//                    dialogBuilder.setMessage("Estela que salga:");
//                    dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    ImageView imageView = new ImageView(getApplicationContext());
//                    imageView.setImageResource(R.drawable.estela1old);
//                    dialogBuilder.setView(imageView);
//                    final AlertDialog alertDialog = dialogBuilder.create();


                            ImageView imgV = (ImageView) dialog.findViewById(R.id.iconInfo);
                            imgV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getBaseContext(), ImageSliderActivity.class);
                                    intent.putExtra("closeMarks", (Serializable) nameCloseMarkers);
                                    startActivity(intent);

//                            dialog.dismiss();
//                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CategoryPage.this);
//                            dialogBuilder.setTitle("Estas son las imagenes de las estelas");
//                            dialogBuilder.setMessage("Estela que salga:");
//                            ImageView imageView = new ImageView(getApplicationContext());
//                            imageView.setImageResource(R.drawable.estela1old);
//                            dialogBuilder.setView(imageView);
//                            AlertDialog alertDialog = dialogBuilder.create();
//                            alertDialog.show();

//                            dialog.dismiss();
//                            RelativeLayout imgLayout = new RelativeLayout(getApplicationContext());
//
//                            ImageView iv = new ImageView(getApplicationContext());
//                            iv.setImageResource(R.drawable.estela1old);
//
//                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//
//                            lp.setMargins(50, 50, 0, 0);
//                            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//
//                            imgLayout.addView(iv, lp);
//
//                            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//                            drawerLayout.addView(imgLayout);

                                }
                            });
                        }
                    }

//                    Button button = new Button(getApplicationContext());
//                    button.setText(R.string.ok);
//                    button.setTextSize(24);
//                    button.setTextColor(Color.BLACK);
//                    button.setBackgroundColor(Color.WHITE);
//
////                    Target target = new ViewTarget(R.id.iconInfo, getParent());
////                    if(dialog.findViewById(R.id.iconInfo) != null) {
////                        Target target = new ViewTarget(R.id.iconInfo, getParent());
////                        Target target = dialog.findViewById(R.id.iconInfo);
//                        new ShowcaseView.Builder(getParent())
////                                .setTarget(target)
//                                .setContentTitle(R.string.hint)
//                                .setContentText(R.string.hint_explain)
//                                .replaceEndButton(button)
//                                .setStyle(R.style.CustomShowcaseTheme)
//                                .hideOnTouchOutside()
//                                .build();
////                    }


                }
//                SharedPreferences sharedPrefScoreEvent = getSharedPreferences("scoreEvent", 0);
//                String scoreEventString = sharedPrefScoreEvent.getString("score", "-1");
//                double scoreEvent = Double.parseDouble(scoreEventString);
                SharedPreferences sharedPref = getSharedPreferences("navDrawFileName", 0);
                String fileNameMark = sharedPref.getString("fileName", "error");
                String[] splitName = fileNameMark.split("\\.");
                String markName = splitName[0];

//                if(currentMarkerActivity != null) {
//                    switch (getColorForMarker(markName)){
//                        case "green":
////                            currentMarkerActivity.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
////                            currentMarkerActivity.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.estandarte_verde));
//                            currentMarkerActivity.setIcon(BitmapDescriptorFactory.fromBitmap(getBannerSized(markImageCorrectAnswer)));
//                            break;
//                        case "red":
////                            currentMarkerActivity.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
////                            currentMarkerActivity.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.estandarte_rojo));
//                            currentMarkerActivity.setIcon(BitmapDescriptorFactory.fromBitmap(getBannerSized(markImageWrongAnswer)));
//                            break;
//                        case "yellow":
////                            currentMarkerActivity.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
////                            currentMarkerActivity.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.estandarte_amarillo));
//                            currentMarkerActivity.setIcon(BitmapDescriptorFactory.fromBitmap(getBannerSized(markImageMidAnswer)));
//                            break;
//                        case "azure":
////                            currentMarkerActivity.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
////                            currentMarkerActivity.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.estandarte_azul));
//                            currentMarkerActivity.setIcon(BitmapDescriptorFactory.fromBitmap(getBannerSized(markImageNotAnswered)));
//                            break;
//                    }
////                    if (scoreEvent == 100) {
////                        currentMarkerActivity.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
////                    } else if (scoreEvent == 0) {
////                        currentMarkerActivity.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
////                    } else if (scoreEvent > 0 && scoreEvent < 100) {
////                        currentMarkerActivity.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
////                    } else {
////                        currentMarkerActivity.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
////                    }
//                }
                TextView textViewScore = findViewById(R.id.score);
                textViewScore.setText(getScoreOutOfTotal());
                prevNameCloseMarkers = new ArrayList<>(nameCloseMarkers);
            }

            public void createMarksDialogBuilder(List<String> nameCloseMarkers, final List<Marker> closeMarkers) {
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
                        SharedPreferences fileNameSP = getSharedPreferences("navDrawFileName", 0);
                        SharedPreferences.Editor nameEditor = fileNameSP.edit();
                        String fileNameChosen = markerChosen.get(1);
                        nameEditor.putString("fileName", fileNameChosen);
                        nameEditor.commit();
                        Intent intent;
                        boolean first;
                        List<Boolean> completed = checkCompletedMarkers();
                        if (completed.contains(Boolean.TRUE)) {
                            first = false;
                        } else {
                            first = true;
                        }
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
                //builder.setIcon(R.drawable.ic_info_images);
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
            public Bitmap getCharacterSized(int character) {
                //BitmapDrawable bitmapdraw = getResources().getDrawable(character);
                Bitmap b = BitmapFactory.decodeResource(getResources(), character); //bitmapdraw.getBitmap();
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int widthDisplay = size.x;
                int heightDisplay = size.y;
                int width;
                int heigth;
                if (widthDisplay < heightDisplay) {
                    width = (int) (widthDisplay * 0.1);
                    heigth = (int) (b.getHeight() * width) / b.getWidth();//(heightDisplay*0.12);
                } else {
                    heigth = (int) (heightDisplay * 0.2);
                    width = (int) (b.getWidth() * heigth) / b.getHeight();
                }
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, heigth, true);
                return smallMarker;
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub
            }
        });
    }

    /**
     * Hace un cálculo del tamaño de la imagen del marcador en función del tamaño de la pantalla.
     * @param banner Imagen del marcador que se quiere redimensionar.
     * @return Bitmap del marcador con el tamaño proporcionado.
     */
    public Bitmap getBannerSized(int banner){
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(banner);
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
            heigth = (int) (b.getHeight() * width) / b.getWidth();//(heightDisplay*0.12);
        }else{
            heigth = (int) (heightDisplay * 0.2);
            width = (int) (b.getWidth() * heigth) / b.getHeight();
        }
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, heigth, true);
        return smallMarker;
    }

    /**
     * Comprueba los marcadores que están cerca de la posición del jugador.
     * @return Lista con los marcadores a una distancia menor a 10 metros.
     */
    public List<Marker> getCloseMarkers(){
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
    public List<Boolean> checkCompletedMarkers(){
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
    public String getScoreOutOfTotal(){
        JSONObject obj;
        double score=0.0;
        try {
            obj = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
            score = obj.getDouble("score");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return String.valueOf(score)+"/"+String.valueOf(markerList.size()*100);
    }

    public double getScore(){
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
    public String getColorForMarker(String nMark){
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
    public void updatePreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this );
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("map_type")){
                    setMapType();
                }else if (key.equals("difficulty")){
                    setDifficulty();
                }
            }
        };
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this );
//        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//            @Override
//            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//                if (key.equals("map_type")){
//                    setMapType();
//                }else if (key.equals("difficulty")){
//                    setDifficulty();
//                }
//            }
//        };

        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Pone el mapa sobre el que se está jugando con el tipo de mapa que decida el usario
     * en los ajustes.
     */
    public void setMapType(){
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

    /**
     * Pone la dificultad de la aplicación en función de lo que haya decidido el usuario
     * en los ajustes.
     */
    public void setDifficulty(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this );
        difficulty = sharedPreferences.getString("difficulty", "easy");
    }


    @Override
    public void onRestart(){
        super.onRestart();
        addJSONmarkersAndFillDic();
        TextView textViewScore = findViewById(R.id.score);
        textViewScore.setText(getScoreOutOfTotal());
        checkEndGame();
    }

    public void checkEndGame(){
        List<Boolean> marksCompleted = checkCompletedMarkers();
        if(!marksCompleted.contains(false)){
            Intent intent = new Intent(this, EndGameActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
