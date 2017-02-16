package com.example.pball.micspot;

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignUpActivity extends Activity implements Callback<MicSpotService.Mic> {
    public static final String PREF_FILE = "SharedPrefs";
    private MicSpotService service;
    private MicSpotService.Mic mic;
    private ListView signupListView;
    private SignupAdapter listAdapter;
    private String micId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        service = new MicSpotService();

        micId = getIntent().getExtras().getString("micId");
        try {
            String jwt = getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("jwt", null);
            service.getMic(micId, this, jwt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        signupListView = (ListView) findViewById(R.id.signup_list);
        listAdapter = new SignupAdapter(this);
        signupListView.setAdapter(listAdapter);
    }

    @Override
    public void onResponse(Call<MicSpotService.Mic> call,
                           Response<MicSpotService.Mic> response) {
        if (response.isSuccessful()) {
            // Store mic and refresh signup list
            mic = response.body();
            listAdapter.clear();
            listAdapter.addAll(mic.nextInstance.signups);
        }
    }

    @Override
    public void onFailure(Call<MicSpotService.Mic> call,
                          Throwable t) {
        t.printStackTrace();
    }

    public class SignupAdapter extends ArrayAdapter<MicSpotService.Signup> {
        public SignupAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MicSpotService.Signup signup = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getContext()).inflate(R.layout.filled_signup, parent, false
                );
            }

            Button btSignup = (Button) convertView.findViewById(R.id.signup_button);
            TextView tvPosition = (TextView) convertView.findViewById(R.id.signup_num);
            TextView tvName = (TextView) convertView.findViewById(R.id.signup_name);
            if (signup == null) {
                tvName.setText("Available");
                btSignup.setText("Sign up");
            } else {
                tvName.setText(signup.name);
                btSignup.setText("Message");
            }
            tvPosition.setText(Integer.toString(position + 1));
            return convertView;
        }
    }
}
