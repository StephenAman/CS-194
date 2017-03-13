package com.example.pball.micspot;

import android.app.Service;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by pball on 3/6/2017.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String REG_TOKEN = "REG_TOKEN";

    @Override
    public void onTokenRefresh(){
        String recentToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN, recentToken);
        super.onTokenRefresh();
    }
}
