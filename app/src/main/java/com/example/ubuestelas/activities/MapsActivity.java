//package com.example.ubuestelas.activities;
//
//import androidx.fragment.app.FragmentActivity;
//
//import android.os.Bundle;
//
//import com.example.ubuestelas.R;
//import com.example.ubuestelas.util.Util;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
//
//    private GoogleMap mMap;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//    }
//
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        JSONObject obj;
//        List<LatLng> markerList = new ArrayList<LatLng>();
//        try{
//            obj = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), "marksJSON.json"));
//            JSONArray townCentre = obj.getJSONArray("townCentre");
//            JSONObject town = townCentre.getJSONObject(0);
//            LatLng townLatLng = new LatLng(town.getDouble("latitude"), town.getDouble("longitude"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(townLatLng, 17.5f));
//            JSONArray marks = obj.getJSONArray("marks");
//            for (int i=0; i<marks.length(); i++){
//                JSONObject stela = marks.getJSONObject(i);
//                LatLng stelaLatLng = new LatLng(stela.getDouble("latitude"), stela.getDouble("longitude"));
//                markerList.add(stelaLatLng);
//                mMap.addMarker(new MarkerOptions().position(stelaLatLng).title(stela.getString("description")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//}
