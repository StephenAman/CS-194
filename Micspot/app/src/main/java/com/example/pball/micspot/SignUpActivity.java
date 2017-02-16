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

public class SignUpActivity extends Activity implements Callback<MicSpotService.Mic> {
    public static final String PREF_FILE = "SharedPrefs";
    private MicSpotService service;
    private MicSpotService.Mic mic;
    private ListView signupListView;
    private SignupAdapter listAdapter;
    private String micId;
    private String userId;
    private String jwt;
    private boolean isUserSignedUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        service = new MicSpotService();
        userId = getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("userId", null);
        jwt = getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("jwt", null);

        micId = getIntent().getExtras().getString("micId");
        try {
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

            // Check if user is signed up to any of the slots
            isUserSignedUp = false;
            for (MicSpotService.Signup signup : mic.nextInstance.signups) {
                if (signup != null && signup.userId.equals(userId)) {
                    isUserSignedUp = true;
                }
            }
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
            btSignup.setEnabled(true);

            if (signup == null) {
                // Slot is available
                tvName.setText("Available");
                btSignup.setText("Sign up");
                btSignup.setOnClickListener(new AdjustSignupListener(position, false));

                // Disable button if user is already signed up to a different slot
                if (isUserSignedUp) {
                    btSignup.setEnabled(false);
                }
            } else {
                tvName.setText(signup.name);
                if (signup.userId.equals(userId)) {
                    // Slot is taken by the app user
                    btSignup.setText("Remove");
                    btSignup.setOnClickListener(new AdjustSignupListener(position, true));
                } else {
                    // Slot is taken by someone else
                    btSignup.setText("Message");
                    btSignup.setOnClickListener(new MessageListener());
                }
            }
            tvPosition.setText(Integer.toString(position + 1));
            return convertView;
        }
    }

    public class AdjustSignupListener implements View.OnClickListener {
        private MicSpotService.SignupSlot slot;
        private boolean shouldDelete;

        public AdjustSignupListener(int position, boolean shouldDelete) {
            this.slot = new MicSpotService.SignupSlot(Integer.toString(position));
            this.shouldDelete = shouldDelete;
        }

        @Override
        public void onClick(View view) {
            MicSpotService.MicClient client = service.Create(jwt);
            Call<Void> call;
            if (shouldDelete) {
                call = client.removeSignup(mic.id, mic.nextInstance.instanceId, slot);
            } else {
                call = client.addSignup(mic.id, mic.nextInstance.instanceId, slot);
            }
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    try {
                        // Refetch mic from server to refresh signup list
                        service.getMic(micId, SignUpActivity.this, jwt);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    public class MessageListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // TODO: Figure out what to do here.
        }
    }
}
