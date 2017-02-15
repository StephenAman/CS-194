package com.example.pball.micspot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.CallbackManager;
import com.facebook.login.LoginResult;


public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Check if user is already logged in.

        // Configure Facebook login
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // TODO: Get JWT from server
                    }

                    @Override
                    public void onCancel() {
                        // TODO: Remove this
                        System.out.println("This happened");
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

}
