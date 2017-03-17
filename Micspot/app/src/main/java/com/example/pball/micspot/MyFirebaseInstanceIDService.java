package com.example.pball.micspot;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    static private final String PREF_FILE = "SharedPrefs";

    /**
     * Send token to backend server every time it is refreshed.
     */
    @Override
    public void onTokenRefresh(){
        // Make sure user is logged in.
        if (!getSharedPreferences(PREF_FILE, MODE_PRIVATE).getBoolean("isLoggedIn", false)) {
            return;
        }
        String token = FirebaseInstanceId.getInstance().getToken();
        String jwt = getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("jwt", null);
        String userId = getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("userId", null);
        MicMap.refreshFirebaseToken(token, jwt, userId);
    }
}
