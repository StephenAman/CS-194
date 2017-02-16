package com.example.pball.micspot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.CallbackManager;
import com.facebook.login.LoginResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements Callback<MicSpotService.JWTString> {
    static public final String PREF_FILE = "SharedPrefs";
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        if (getSharedPreferences(PREF_FILE, MODE_PRIVATE).getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(LoginActivity.this, MicMap.class);
            LoginActivity.this.startActivity(intent);
        }

        // Configure Facebook login
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        MicSpotService.GetJWT(
                                loginResult.getAccessToken().getUserId(),
                                loginResult.getAccessToken().getToken(),
                                LoginActivity.this
                        );
                    }

                    @Override
                    public void onCancel() {
                        Intent intent = new Intent(LoginActivity.this, MicMap.class);
                        LoginActivity.this.startActivity(intent);
                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                }
        );

        setContentView(R.layout.activity_login);
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
            SharedPreferences.Editor editor = getSharedPreferences(PREF_FILE, MODE_PRIVATE).edit();
            editor.putString("jwt", response.body().jwt);
            editor.putBoolean("isLoggedIn", true);
            editor.apply();
            Intent intent = new Intent(LoginActivity.this, MicMap.class);
            LoginActivity.this.startActivity(intent);
        }
    }

    @Override
    public void onFailure(Call<MicSpotService.JWTString> call, Throwable t) {
        t.printStackTrace();
    }
}
