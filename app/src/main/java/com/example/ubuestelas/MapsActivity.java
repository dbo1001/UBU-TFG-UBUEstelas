package com.example.ubuestelas;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        LatLng laraDeLosInfantes = new LatLng(42.123126, -3.445355);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(laraDeLosInfantes, 17.5f));
        JSONObject obj;
        try{
            obj = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), "stelasJSON.json"));
            JSONArray stelas = obj.getJSONArray("stelas");
            for (int i=0; i<stelas.length(); i++){
                JSONObject stela = stelas.getJSONObject(i);
                LatLng stelaLatLng = new LatLng(stela.getDouble("latitude"), stela.getDouble("longitude"));
                mMap.addMarker(new MarkerOptions().position(stelaLatLng).title(stela.getString("description")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
