package com.example.pball.micspot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CreateMic extends AppCompatActivity {
    private MicSpotService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mic);
        service = new MicSpotService();
    }
}
