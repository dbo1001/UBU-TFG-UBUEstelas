package com.example.ubuestelas.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    MarkerOptions currentLocation;
    List<Marker> markerList;
    Marker currentLocationMarker;
    HashMap<Marker,List<String>> dicMarkerAct;
    Marker currentMarkerActivity = null;

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


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
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

    public void addJSONmarkersAndFillDic(){
        JSONObject obj;
        markerList = new ArrayList<Marker>();
        dicMarkerAct = new HashMap<Marker,List<String>>();
        try {
            obj = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), "marksJSON.json"));
            JSONArray townCentre = obj.getJSONArray("townCentre");
            JSONObject town = townCentre.getJSONObject(0);
            LatLng townLatLng = new LatLng(town.getDouble("latitude"), town.getDouble("longitude"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(townLatLng, 17.5f));
            JSONArray marks = obj.getJSONArray("marks");
            for (int i = 0; i < marks.length(); i++) {
                JSONObject mark = marks.getJSONObject(i);
                LatLng stelaLatLng = new LatLng(mark.getDouble("latitude"), mark.getDouble("longitude"));
                Marker marker = mMap.addMarker(new MarkerOptions().position(stelaLatLng).title(mark.getString("description")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getCurrentLocation(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SharedPreferences sharedPrefChar = getSharedPreferences("characterSelected",0);
        final int characterDrawable = sharedPrefChar.getInt("drawableCharac",R.drawable.character01);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            List<String> prevNameCloseMarkers = new ArrayList<>();
            Bitmap characterSized = getCharacterSized(characterDrawable);
            @Override
            public void onLocationChanged(Location location) {

                if(currentLocationMarker!= null){
                    currentLocationMarker.remove();
                }
                currentLocation = new MarkerOptions();

                currentLocation.position(new LatLng(location.getLatitude(), location.getLongitude())).title("Estoy aquí").icon(BitmapDescriptorFactory.fromBitmap(characterSized));

                currentLocationMarker = mMap.addMarker(currentLocation);

                final List<Marker> closeMarkers = getCloseMarkers();
                List<String> nameCloseMarkers = new ArrayList<>();

                for(Marker marker : closeMarkers){
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    nameCloseMarkers.add(marker.getTitle());
                }

                if (!prevNameCloseMarkers.equals(nameCloseMarkers) && !nameCloseMarkers.isEmpty()) {
                    builder.setTitle(getString(R.string.choose_stela));
                    String[] closeMarkersString = new String[nameCloseMarkers.size()];
                    closeMarkersString = nameCloseMarkers.toArray(closeMarkersString);
                    builder.setItems(closeMarkersString, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            currentMarkerActivity=closeMarkers.get(which);
                            List<String> markerChosen = dicMarkerAct.get(currentMarkerActivity);
                            SharedPreferences fileNameSP = getSharedPreferences("navDrawFileName", 0);
                            SharedPreferences.Editor nameEditor = fileNameSP.edit();
                            String fileNameChosen = markerChosen.get(1);
                            nameEditor.putString("fileName", fileNameChosen);
                            nameEditor.commit();
                            Intent intent;
                            switch (markerChosen.get(0)){
                                case "test":
                                    intent = new Intent(getBaseContext(), TypeTestActivity.class);
                                    startActivity(intent);

                                    break;
                                case "puzzle":
                                    intent = new Intent(getBaseContext(), TypePuzzleActivity.class);
                                    startActivity(intent);
                                    break;
                            }
                        }
                    });
// create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                SharedPreferences sharedPrefScoreEvent = getSharedPreferences("scoreEvent", 0);
                String scoreEventString = sharedPrefScoreEvent.getString("score", "-1");
                double scoreEvent = Double.parseDouble(scoreEventString);
                if(currentMarkerActivity != null) {
                    if (scoreEvent == 100) {
                        currentMarkerActivity.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else if (scoreEvent == 0) {
                        currentMarkerActivity.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    } else if (scoreEvent > 0 && scoreEvent < 100) {
                        currentMarkerActivity.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    } else {
                        currentMarkerActivity.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    }
                }
                TextView textViewScore = findViewById(R.id.score);
                textViewScore.setText(getScoreOutOfTotal());
                prevNameCloseMarkers = new ArrayList<>(nameCloseMarkers);
            }

            public Bitmap getCharacterSized(int character){
                BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(character);
                Bitmap b = bitmapdraw.getBitmap();
                int width = (int) (b.getWidth()*0.6);
                int heigth = (int) (b.getHeight()*0.6);
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, heigth, false);
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

    public List<Marker> getCloseMarkers(){
        List<Boolean> completedMarkers = checkCompletedMarkers();
        List<Marker> closeMarkers= new ArrayList<>();
        int counter = 0;
        for (Marker marker : markerList) {
            if (!completedMarkers.get(counter)) {
                if (Util.getDistanceFromLatLong(currentLocationMarker.getPosition(), marker.getPosition()) <= 10) {
                    closeMarkers.add(marker);
                }
                counter++;
            }
        }
        return closeMarkers;
    }

    public List<Boolean> checkCompletedMarkers(){
        JSONObject obj;
        List<Boolean> completedMarkers= new ArrayList<>();
        try {
            obj = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
            JSONArray marks = obj.getJSONArray("marks");
            for (int i = 0; i <= marks.length(); i++){
                JSONObject mark = marks.getJSONObject(i);
                completedMarkers.add(mark.getBoolean("solved"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return completedMarkers;
    }

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

}
