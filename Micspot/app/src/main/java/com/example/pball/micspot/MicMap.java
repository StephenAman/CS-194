package com.example.pball.micspot;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MicMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final String GET_MAP_MICS = "http://138.68.7.110:8080/api/mics";//move this probably
    JSONArray micArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Yo im here");
        JSONObject mic1 = new JSONObject();
        JSONObject mic2 = new JSONObject();
        micArray = new JSONArray();
        try {
            mic1.put("micId", 1);
            mic1.put("status", "yellow");
            mic1.put("venueLat", 37.776401);
            mic1.put("venueLng", -122.408711);
            mic2.put("micId", 2);
            mic2.put("status", "orange");
            mic2.put("venueLat", 37.752687);
            mic2.put("venueLng", -122.41386);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        micArray.put(mic1);
        micArray.put(mic2);

        System.out.println(micArray.toString());

        RequestQueue queue = Volley.newRequestQueue(this);

        /*
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                GET_MAP_MICS,
                paramArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray micArray) {
                        try {
                            for (int i = 0; i < micArray.length(); i++) {
                                JSONObject jsonobject = micArray.getJSONObject(i);
                                int micId = jsonobject.getInt("micId");
                                String status = jsonobject.getString("status");
                                System.out.println("Bodied!");
                                double venLat = jsonobject.getDouble("venueLat");
                                double venLong = jsonobject.getDouble("venueLat");
                                System.out.println("got here, check it: " + venLat);
                            }
                        }catch (JSONException e) {
                                e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                System.out.println(e);
            });{
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<String, String>();
                //Add your headers here
                return map;
            }
        };

        queue.add(request);
        queue.start();
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic_map);
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

        try {
            addMicsToMap();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addMicsToMap() throws JSONException {
        for(int i = 0; i < micArray.length(); i++){
            JSONObject mic = (JSONObject) micArray.get(i);
            LatLng micLocation = new LatLng(mic.getDouble("venueLat"), mic.getDouble("venueLng"));
            MarkerOptions marker = new MarkerOptions();
            String status = mic.getString("status");
            if(status.equals("yellow")) marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            else marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            mMap.addMarker(marker.position(micLocation)
                    .title("mic1"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(micLocation));
        }
    }
}
