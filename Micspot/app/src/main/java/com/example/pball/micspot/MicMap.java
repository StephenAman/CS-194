package com.example.pball.micspot;

import android.graphics.Bitmap;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MicMap extends FragmentActivity implements OnMapReadyCallback,
                                                        Callback<List<MicSpotService.MicSummary>> {
    private GoogleMap mMap;
    private MicSpotService service;
    private Map<String, MicSpotService.MicSummary> mics;

    JSONArray micArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        service = new MicSpotService(this);
        mics = new HashMap<String, MicSpotService.MicSummary>();

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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onResponse(Call<List<MicSpotService.MicSummary>> call,
                           Response<List<MicSpotService.MicSummary>> response) {
        if (response.isSuccessful()) {
            // Store response in mic map
            mics.clear();
            for (MicSpotService.MicSummary mic  : response.body()) {
                mics.put(mic.micId, mic);
            }
            // Refresh map markers
            addMicsToMap(response.body());
        }
    }

    @Override
    public void onFailure(Call<List<MicSpotService.MicSummary>> call,
                          Throwable t) {
        // TODO(joachimr): Implement.
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
            service.Test();
        } catch (IOException e) {
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
                // parent.add
                parent.addView(signupButton);
                // parent.addView(signupButton);

                return parent;
            }
        });
    }

    /**
     * Clears existing markers and adds markers for the given mics.
     * The camera position is moved to include all the markers.
     */
    private void addMicsToMap(List<MicSpotService.MicSummary> mics) {
        mMap.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MicSpotService.MicSummary mic : mics) {
            LatLng micLocation = new LatLng(mic.venueLat, mic.venueLng);
            MarkerOptions marker = new MarkerOptions();
            float markerColor = BitmapDescriptorFactory.HUE_ORANGE;
            if (mic.status.equals("yellow")) {
                markerColor = BitmapDescriptorFactory.HUE_YELLOW;
            }
            marker.icon(BitmapDescriptorFactory.defaultMarker(markerColor));
            builder.include(micLocation);
            mMap.addMarker(marker.position(micLocation).title(mic.micId));
        }
        int padding = 10;
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding));
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
