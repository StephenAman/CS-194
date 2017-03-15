package com.example.pball.micspot;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.app.NotificationManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.content.ContextWrapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment implements Callback<MicSpotService.Mic> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static final String PREF_FILE = "SharedPrefs";
    private MicSpotService service;
    private MicSpotService.Mic mic;
    private ListView signupListView;
    private SignupAdapter listAdapter;//gotta change this
    private int numFreeSpots;
    private String micId;
    private String userId;
    private String jwt;
    private boolean isUserSignedUp;
    private boolean isUserProducer;

    private OnFragmentInteractionListener mListener;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        service = new MicSpotService();
        userId = getActivity().getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("userId", null);
        userId = getActivity().getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("userId", null);
        jwt = getActivity().getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("jwt", null);
        listAdapter = new SignupAdapter(getContext());

        micId = getActivity().getIntent().getExtras().getString("micId");
        try {
            Activity a = (Activity) getActivity();
            SignUpActivity s = (SignUpActivity) a; //this could be problematic
            service.getMic(micId, s, jwt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        signupListView = (ListView) getActivity().findViewById(R.id.signup_list);
        signupListView.setAdapter(listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onFailure(Call<MicSpotService.Mic> call,
                          Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onResponse(Call<MicSpotService.Mic> call,
                           Response<MicSpotService.Mic> response) {
        if (response.isSuccessful()) {
            // Store mic and refresh signup list
            mic = response.body();

            // Check if user is signed up to any of the slots
            isUserSignedUp = false;
            numFreeSpots = 0;
            for (MicSpotService.Signup signup : mic.nextInstance.signups) {
                if (signup != null && signup.userId.equals(userId)) {
                    isUserSignedUp = true;
                }
                if(signup == null) numFreeSpots++;
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
                numFreeSpots++;
            } else {
                call = client.addSignup(mic.id, mic.nextInstance.instanceId, slot);
                numFreeSpots--;
                if(numFreeSpots == 0) {
                    //notifyFullList();
                }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
