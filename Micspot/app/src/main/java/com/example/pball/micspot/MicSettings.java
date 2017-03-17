package com.example.pball.micspot;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.view.View;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MicSettings extends AppCompatActivity {
    private static final String PREF_FILE = "SharedPrefs";
    private static final int PLACE_PICKER_REQUEST = 1;
    private MicSpotService service;
    private MicSpotService.Mic mic;
    private EditText dateField;
    private EditText timeField;
    private EditText signupDateField;
    private EditText signupTimeField;
    private Spinner eventStatusSpinner;
    private EditText durationField;
    private EditText setTimeField;
    private EditText numSlotsField;
    private Spinner meetingBasisSpinner;

    private Date chosenDate;
    private Date chosenTime;
    private Date chosenSignupDate = null;
    private Date chosenSignupTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        service = new MicSpotService();
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            mic = b.getParcelable("MicSpotService.Mic");
        }

        // Set the status bar to the same color as our Micspot Orange
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#FF5920"));
        }

        // Get fields
        dateField = (EditText) findViewById(R.id.mic_date_field);
        timeField = (EditText) findViewById(R.id.mic_time_field);
        signupDateField = (EditText) findViewById(R.id.mic_signup_date_field);
        signupTimeField = (EditText) findViewById(R.id.mic_signup_time_field);
        eventStatusSpinner = (Spinner) findViewById(R.id.mic_event_status_spinner);
        durationField = (EditText) findViewById(R.id.mic_duration_field);
        setTimeField = (EditText) findViewById(R.id.mic_set_time_field);
        numSlotsField = (EditText) findViewById(R.id.mic_num_slots_field);
        meetingBasisSpinner = (Spinner) findViewById(R.id.mic_meeting_basis_spinner);

        // Configure date/time fields to be non-clickable
        dateField.setKeyListener(null);
        timeField.setKeyListener(null);
        signupDateField.setKeyListener(null);
        signupTimeField.setKeyListener(null);

        // Configure spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meeting_basis_options, android.R.layout.simple_spinner_dropdown_item);
        meetingBasisSpinner.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(this, R.array.event_status_options,
                android.R.layout.simple_spinner_dropdown_item);
        eventStatusSpinner.setAdapter(adapter);

        // Setup fields based on current mic values.
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-YYYY");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        dateField.setText(dateFormat.format(mic.startDate));
        timeField.setText(timeFormat.format(mic.startDate));
        chosenDate = mic.startDate;
        chosenTime = mic.startDate;
        if (mic.nextInstance.signupsOpenDate != null) {
            chosenSignupDate = mic.nextInstance.signupsOpenDate;
            chosenSignupTime = mic.nextInstance.signupsOpenDate;
            signupDateField.setText(dateFormat.format(mic.nextInstance.signupsOpenDate));
            signupTimeField.setText(timeFormat.format(mic.nextInstance.signupsOpenDate));
        }
        if (mic.nextInstance.cancelled == 1) {
            eventStatusSpinner.setSelection(1);
        } else {
            eventStatusSpinner.setSelection(0);
        }
        durationField.setText(Integer.toString(mic.duration));
        setTimeField.setText(Integer.toString(mic.setTime));
        numSlotsField.setText(Integer.toString(mic.numSlots));
        switch (mic.meetingBasis.toLowerCase()) {
            case "none":
                meetingBasisSpinner.setSelection(0);
                break;
            case "daily":
                meetingBasisSpinner.setSelection(1);
                break;
            case "weekly":
                meetingBasisSpinner.setSelection(2);
                break;
            case "biweekly":
                meetingBasisSpinner.setSelection(3);
                break;
            case "monthly":
                meetingBasisSpinner.setSelection(4);
                break;
        }
    }

    public void showDatePicker(View v) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        DatePickerDialog dialog = new DatePickerDialog(
                this, new DateFieldListener(true), year, month, day
        );
        dialog.show();
    }

    public void showSignupDatePicker(View v) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        DatePickerDialog dialog = new DatePickerDialog(
                this, new DateFieldListener(false), year, month, day
        );
        dialog.show();
    }

    public class DateFieldListener implements OnDateSetListener {
        boolean isEventDate;

        public DateFieldListener(boolean isEventDate) {
            this.isEventDate = isEventDate;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, 0, 0);
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-YYYY");
            if (isEventDate) {
                chosenDate = c.getTime();
                dateField.setText(formatter.format(c.getTime()));
            } else {
                chosenSignupDate = c.getTime();
                signupDateField.setText((formatter.format(c.getTime())));
            }
        }
    }

    public void showTimePicker(View v) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        TimePickerDialog dialog = new TimePickerDialog(
                this, new TimeFieldListener(true), hour, 0, false
        );
        dialog.show();
    }

    public void showSignupTimePicker(View v) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        TimePickerDialog dialog = new TimePickerDialog(
                this, new TimeFieldListener(false), hour, 0, false
        );
        dialog.show();
    }

    public class TimeFieldListener implements OnTimeSetListener {
        boolean isEventTime;

        public TimeFieldListener(boolean isEventTime) {
            this.isEventTime = isEventTime;
        }

        public void onTimeSet(TimePicker view, int hour, int minute) {
            Calendar c = Calendar.getInstance();
            c.set(2000, 0, 0, hour, minute);
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
            if (isEventTime) {
                chosenTime = c.getTime();
                timeField.setText(formatter.format(c.getTime()));
            } else {
                chosenSignupTime = c.getTime();
                signupTimeField.setText((formatter.format(c.getTime())));
            }

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

    public void tryUpdateMic(View v) {
        // Validate fields
        if (isEmpty(dateField) || isEmpty(timeField) || isEmpty(durationField) ||
                isEmpty(setTimeField) || isEmpty(numSlotsField)) {
            Toast toast = Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        // If the user has chosen a signup time, they must also pick a signup date.
        if (chosenSignupTime != null && chosenSignupDate == null) {
            Toast.makeText(this,
                    "You must select a signup date since you have chosen a signup time.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Update event date.
        Date eventDate = MergeDateAndTime(chosenDate, chosenTime);
        MicSpotService.EventDateWrapper eventDateWrapper = new MicSpotService.EventDateWrapper(
                eventDate,
                Integer.parseInt(durationField.getText().toString()),
                1
        );

        // Update signups date.
        Date signupsOpenDate = null;
        if (chosenSignupDate != null) {
            signupsOpenDate = MergeDateAndTime(chosenSignupDate, chosenSignupTime);
        }

        // Get Spinner selection
        int cancelled = eventStatusSpinner.getSelectedItemPosition();
        String meetingBasis = meetingBasisSpinner.getSelectedItem().toString();

        // Create update mic data
        MicSpotService.UpdateInstanceData data = new MicSpotService.UpdateInstanceData(
                eventDateWrapper,
                signupsOpenDate,
                Integer.parseInt(numSlotsField.getText().toString()),
                Integer.parseInt(setTimeField.getText().toString()),
                cancelled,
                meetingBasis
        );

        // Send request
        String jwt = getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("jwt", null);

        MicSpotService.MicClient client = service.Create(jwt);
        Call<Void> call = client.updateInstance(mic.id, mic.nextInstance.instanceId, data);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(
                            MicSettings.this, "Mic was successfully updated", Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(MicSettings.this, "Failed to update mic", Toast.LENGTH_SHORT)
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

    /**
     * Merges month/day/year from date and hour/minute from time into one Date object.
     * Defaults to midnight if time is null.
     */
    private Date MergeDateAndTime(Date date, Date time) {
        Calendar a = Calendar.getInstance();
        a.setTime(date);
        if (time == null) {
            a.set(Calendar.HOUR_OF_DAY, 0);
            a.set(Calendar.MINUTE, 0);
        } else {
            Calendar b = Calendar.getInstance();
            b.setTime(time);
            a.set(Calendar.HOUR_OF_DAY, b.get(Calendar.HOUR_OF_DAY));
            a.set(Calendar.MINUTE, b.get(Calendar.MINUTE));
        }
        a.set(Calendar.SECOND, 0);
        a.set(Calendar.MILLISECOND, 0);
        return a.getTime();
    }
}
