package com.example.pball.micspot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class SignUpFragment extends Fragment implements Callback<MicSpotService.Mic> {
    public static final String PREF_FILE = "SharedPrefs";
    private MicSpotService service;
    private MicSpotService.Mic mic;
    private ListView signupListView;
    private SignupAdapter listAdapter;
    private int numFreeSpots;
    private String micId;
    private String userId;
    private String jwt;
    private boolean isUserSignedUp;
    private boolean isUserProducer;

    static SignUpFragment newInstance(String micId) {
        SignUpFragment fragment = new SignUpFragment();

        // Pass micId to fragment.
        Bundle args = new Bundle();
        args.putString("micId", micId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        signupListView = (ListView) (rootView.findViewById (R.id.signup_list));
        signupListView.setAdapter(listAdapter);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new MicSpotService();
        userId = getActivity().getSharedPreferences(
                PREF_FILE, Context.MODE_PRIVATE
        ).getString("userId", null);
        jwt = getActivity().getSharedPreferences(
                PREF_FILE, Context.MODE_PRIVATE
        ).getString("jwt", null);

        micId = getArguments().getString("micId");
        try {
            service.getMic(micId, this, jwt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        listAdapter = new SignupAdapter(getActivity());
    }

    @Override
    public void onResponse(Call<MicSpotService.Mic> call,
                           Response<MicSpotService.Mic> response) {
        if (response.isSuccessful()) {
            // Store mic and refresh signup list
            mic = response.body();

            // Send mic in container activity
            ((MicPage)getActivity()).setMic(mic);

            // Check if user is signed up to any of the slots
            isUserSignedUp = false;
            for (MicSpotService.Signup signup : mic.nextInstance.signups) {
                if (signup != null && signup.userId.equals(userId)) {
                    isUserSignedUp = true;
                }
            }

            // Check if user is the mic producer
            isUserProducer = false;
            if (mic.createdBy.equals(userId)) {
                isUserProducer = true;
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
                if (isUserProducer || signup.userId.equals(userId)) {
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
                        service.getMic(micId, SignUpFragment.this, jwt);
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