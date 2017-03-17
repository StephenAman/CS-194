package com.example.pball.micspot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.CallbackManager;
import com.facebook.login.LoginResult;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements Callback<MicSpotService.JWTString> {
    static public final String PREF_FILE = "SharedPrefs";
    private Intent loggedInIntent;
    Button backToMap;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Check if user is already logged in
        if (getSharedPreferences(PREF_FILE, MODE_PRIVATE).getBoolean("isLoggedIn", false)) {
            loggedInIntent = new Intent(LoginActivity.this, MicMap.class);
            backToMap = (Button) findViewById(R.id.map_button);
            backToMap.setVisibility(View.VISIBLE);
            backToMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginActivity.this.startActivity(loggedInIntent);
                }
            });
            LoginActivity.this.startActivity(loggedInIntent);
        }

        // Configure Facebook login
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        SharedPreferences.Editor editor =
                                getSharedPreferences(PREF_FILE, MODE_PRIVATE).edit();
                        editor.putString("userId", loginResult.getAccessToken().getUserId());
                        editor.apply();
                        MicSpotService.GetJWT(
                                loginResult.getAccessToken().getUserId(),
                                loginResult.getAccessToken().getToken(),
                                LoginActivity.this
                        );
                        if(getSharedPreferences(PREF_FILE, MODE_PRIVATE).getBoolean("isLoggedIn", false)){
                            System.out.println("VIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIS");
                            backToMap.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancel() {}

                    @Override
                    public void onError(FacebookException error) {}
                }
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResponse(Call<MicSpotService.JWTString> call,
                           Response<MicSpotService.JWTString> response) {
        if (response.isSuccessful()) {
            LogIn(response.body().jwt);
        }
    }

    @Override
    public void onFailure(Call<MicSpotService.JWTString> call, Throwable t) {
        t.printStackTrace();
    }

    /**
     * Performs the steps necessary to log the user in to the application, such as storing the JWT,
     * and sending an updated Firebase token to the web server.
     */
    public void LogIn(String jwt) {
        // Update storage variables
        String userId = getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("userId", null);
        SharedPreferences.Editor editor = getSharedPreferences(PREF_FILE, MODE_PRIVATE).edit();
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        editor.putString("jwt", jwt);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        // Refresh Firebase token
        MicMap.refreshFirebaseToken(firebaseToken, jwt, userId);

        // Replace login button with logout button, show back to map button.
        // TODO: Do here.
        Intent intent = new Intent(LoginActivity.this, MicMap.class);
        LoginActivity.this.startActivity(intent);
    }

    /**
     * Performs the steps necessary to log the user out of the application.
     */
    public void LogOut() {
        // Update storage variables
        SharedPreferences.Editor editor = getSharedPreferences(PREF_FILE, MODE_PRIVATE).edit();
        editor.remove("userId");
        editor.remove("jwt");
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        // Replace logout button with login button, hide back to map button.
        // TODO: Do here.
    }
}
