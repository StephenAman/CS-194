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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    static public final String PREF_FILE = "SharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mics = new HashMap<String, MicSpotService.MicSummary>();
        service = new MicSpotService();

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
        t.printStackTrace();
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
            String jwt = getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("jwt", null);
            System.out.println("JWT is: " + jwt);
            service.GetAllMics(jwt, this);
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
                MicSpotService.MicSummary mic = mics.get(marker.getTitle());
                windowDetails = populateText(windowDetails, mic);

                parent.addView(windowDetails);
                Button signupButton = new Button(MicMap.this);
                signupButton.setText("Sign Up");
                parent.addView(signupButton);
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

    private TextView populateText(TextView windowText, MicSpotService.MicSummary mic) {
        DateFormat date = new SimpleDateFormat("MMMM dd");
        DateFormat time = new SimpleDateFormat("h:mm a");
        String dateString = date.format(mic.startDate);
        String timeString = time.format(mic.startDate) + "-" + time.format(mic.endDate);
        String basis = mic.meetingBasis.substring(0, 1).toUpperCase() +
                       mic.meetingBasis.substring(1);

        windowText.append(mic.micName + "\n");
        windowText.append(basis + " " + timeString + "\n");
        windowText.append("Producer: " + mic.createdBy + "\n");
        windowText.append("Next event: " + dateString);
        return windowText;
    }
}
