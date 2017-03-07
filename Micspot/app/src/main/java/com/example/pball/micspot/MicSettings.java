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
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MicSettings extends AppCompatActivity {
    private static final String PREF_FILE = "SharedPrefs";
    private static final int PLACE_PICKER_REQUEST = 1;
    private MicSpotService service;
    private EditText dateField;
    private EditText timeField;
    private EditText addressField;
    private Date chosenDate;
    private MicSpotService.Mic mic;
    private Date chosenTime;
    private Place chosenPlace;
    private String micId;
    private String userId;
    private String jwt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            mic = b.getParcelable("MicSpotService.Mic");
        }
        setContentView(R.layout.activity_settings);


        // Configure selection fields
        dateField = (EditText) findViewById(R.id.mic_date_field);
        timeField = (EditText) findViewById(R.id.mic_time_field);
        addressField = (EditText) findViewById(R.id.mic_address_field);
        dateField.setKeyListener(null);
        timeField.setKeyListener(null);
        addressField.setKeyListener(null);





        // Configure spinner
        Spinner spinner = (Spinner) findViewById(R.id.mic_meeting_basis_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meeting_basis_options, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        addressField.setText(mic.venueAddress);



//        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-YYYY");
//        dateField.setText(formatter.format(mic.startDate.getTime()));


//        formatter = new SimpleDateFormat("hh:mm a");
//        timeField.setText(formatter.format(mic.startDate.getTime()));




        EditText nameField = (EditText) findViewById(R.id.mic_name_field);
        EditText durationField = (EditText) findViewById(R.id.mic_duration_field);
        EditText setTimeField = (EditText) findViewById(R.id.mic_set_time_field);
        EditText numSlotsField = (EditText) findViewById(R.id.mic_num_slots_field);
        nameField.setText(mic.micName);
        durationField.setText( (int)mic.duration + "");
        setTimeField.setText( (int)mic.setTime + "");
        numSlotsField.setText((int) mic.numSlots+"");


        //timeField.setText(mic.setTime);
//        spinner.setAdapter(adapter);
//        if (!mic.meetingBasis.equals(null)) {
//            int spinnerPosition = adapter.getPosition(mic.meetingBasis);
//            spinner.setSelection(spinnerPosition);
//        }

    }


    public void showDatePicker(View v) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        DatePickerDialog dialog = new DatePickerDialog(
                this, new DateFieldListener(), year, month, day
        );
        dialog.show();
    }

    public class DateFieldListener implements OnDateSetListener {
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, 0, 0);
            chosenDate = c.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-YYYY");
            dateField.setText(formatter.format(c.getTime()));
        }
    }

    public void showTimePicker(View v) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        TimePickerDialog dialog = new TimePickerDialog(
                this, new TimeFieldListener(), hour, 0, false
        );
        dialog.show();
    }

    public class TimeFieldListener implements OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            Calendar c = Calendar.getInstance();
            c.set(2000, 0, 0, hour, minute);
            chosenTime = c.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
            timeField.setText(formatter.format(c.getTime()));
        }
    }

    public void showPlacePicker(View v) {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                chosenPlace = place;
                addressField.setText(place.getAddress());
            }
        }
    }

    public void tryCreateMic(View v) {
        // Validate fields
        EditText nameField = (EditText) findViewById(R.id.mic_name_field);
        EditText durationField = (EditText) findViewById(R.id.mic_duration_field);
        EditText setTimeField = (EditText) findViewById(R.id.mic_set_time_field);
        EditText numSlotsField = (EditText) findViewById(R.id.mic_num_slots_field);

        if (
                isEmpty(dateField) || isEmpty(timeField) || isEmpty(addressField) ||
                isEmpty(nameField) || isEmpty(durationField) || isEmpty(setTimeField) ||
                isEmpty(numSlotsField)) {
            Toast toast = Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        // Get Spinner selection
        String meetingBasis = ((Spinner)findViewById(R.id.mic_meeting_basis_spinner))
                .getSelectedItem().toString();

        // Combine date and time fields into one "date-time" object
        Calendar a = Calendar.getInstance();
        a.setTime(chosenDate);
        Calendar b = Calendar.getInstance();
        b.setTime(chosenTime);
        a.set(Calendar.HOUR_OF_DAY, b.get(Calendar.HOUR_OF_DAY));
        a.set(Calendar.MINUTE, b.get(Calendar.MINUTE));
        a.set(Calendar.SECOND, 0);
        a.set(Calendar.MILLISECOND, 0);

        // Populate CreateMicData
        MicSpotService.CreateMicData data = new MicSpotService.CreateMicData(
                nameField.getText().toString(),
                chosenPlace.getName().toString(),
                chosenPlace.getAddress().toString(),
                (float) chosenPlace.getLatLng().latitude,
                (float) chosenPlace.getLatLng().longitude,
                a.getTime(),
                Integer.parseInt(durationField.getText().toString()),
                meetingBasis,
                Integer.parseInt(setTimeField.getText().toString()),
                Integer.parseInt(numSlotsField.getText().toString())
        );

        // Send request
        String jwt = getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("jwt", null);
        MicSpotService.MicClient client = service.Create(jwt);
        Call<Void> call = client.createMic(data);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(
                            MicSettings.this, "Mic was successfully created", Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(MicSettings.this, "Failed to create mic", Toast.LENGTH_SHORT)
                        .show();
                }
                finish();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    private boolean isEmpty(EditText text) {
        return text.getText().toString().trim().length() == 0;
    }
}
