package com.example.pball.micspot;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignUpActivity extends Activity {

    private ListView signupListView;
    private ArrayAdapter<String> listAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signupListView = (ListView) findViewById(R.id.signup_list);
        ArrayList<String> signupList = new ArrayList<String>();

        //TODO: load this array by querying the backend
        String[] signupArr = {"Brady Hold", " ", " ", "Omar Qureshi", "Brian Blanco", "Phill Giliver", " ", "Mark Smalls"};
        signupList.addAll(Arrays.asList(signupArr));
        System.out.println("before listadapter");
        listAdapter = new ArrayAdapter<String>(this, R.layout.filled_signup, R.id.signup_name, signupList);
        //listAdapter
        System.out.println("second to last line of signup");
        signupListView.setAdapter(listAdapter);

    }



}
