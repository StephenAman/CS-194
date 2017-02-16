package com.example.pball.micspot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.EditText;
import android.content.Intent;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateMic extends AppCompatActivity {
    private static final int PLACE_PICKER_REQUEST = 1;
    private MicSpotService service;
    private EditText dateField;
    private EditText timeField;
    private EditText addressField;
    private Date chosenDate;
    private Date chosenTime;
    private Place chosenPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mic);
        service = new MicSpotService();

        // Configure selection fields
        dateField = (EditText) findViewById(R.id.mic_date_field);
        timeField = (EditText) findViewById(R.id.mic_time_field);
        addressField = (EditText) findViewById(R.id.mic_address_field);
        dateField.setKeyListener(null);
        timeField.setKeyListener(null);
        addressField.setKeyListener(null);

        // Configure text fields
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
}
