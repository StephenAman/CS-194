package com.example.pball.micspot;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

        /*TODO: Override getHeaders() to add auth to request
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                GET_MAP_MICS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray micArray) {
                        try {
                            for (int i = 0; i < micArray.length(); i++) {
                                JSONObject jsonobject = micArray.getJSONObject(i);
                                int micId = jsonobject.getInt("micId");
                                String status = jsonobject.getString("status");
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

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }

            public View getInfoContents(Marker marker){
                LinearLayout parent = new LinearLayout(MicMap.this);
                parent.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                parent.setOrientation(LinearLayout.VERTICAL);

                TextView windowDetails = new TextView(MicMap.this);
                //TODO: Get mic data from db
                //query using the mic id and the other url, get venue
                //basis, start time, duration, set length
                JSONObject jsonMic = new JSONObject();
                try {
                    jsonMic.put("basis", "weekly");
                    jsonMic.put("micName", "Brainwash");
                    jsonMic.put("startDate", "2017-02-13T20:00:00.000");
                    jsonMic.put("duration", 120);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    windowDetails = populateText(windowDetails, jsonMic);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                parent.addView(windowDetails);

                Button signupButton = new Button(MicMap.this);
                signupButton.setText("Sign Up");

                parent.addView(signupButton);

                return parent;
            }
        });
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

    private TextView populateText(TextView windowText, JSONObject jsonMic) throws JSONException {
        String basis = (String) jsonMic.get("basis");
        String name = (String) jsonMic.get("micName");
        String dateStr = (String) jsonMic.get("startDate");
        int duration = (int) jsonMic.get("duration");
        String producer = "Pete"; //(String) jsonMic.get("createdBy");//TODO: make sure this works
        windowText.append(name + "\n");
        int startHour = Integer.parseInt(dateStr.substring(11, 13));
        int startMinute = Integer.parseInt(dateStr.substring(14,16));
        if(startHour > 12) {//TODO: pm
            startHour -= 12;
            int endHour = startHour + (duration/60);//assumes no mics go past midnigth
            String startTime = startHour + ":" + startMinute + "0";
            String endTime;
            if(duration % 60 == 0) {
                endTime = endHour + ":" + startMinute + "0";
            } else {
                int endMinute = (startMinute + (duration % 60)) % 60;
                endTime = endHour + ":" + endMinute + "0";
            }
            windowText.append(basis + ", " + startTime + "-" + endTime + " PM\n");
        }
        windowText.append("Producer: " + producer);
        return windowText;
    }
}
