package com.example.lazyworkout.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lazyworkout.R;
import com.example.lazyworkout.util.Database;
import com.example.lazyworkout.util.Time;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.w3c.dom.Text;

public class TimePickerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backArrow;
    private TextView skip;
    private Button btn;

    Database db = new Database();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        initViews();
    }

    private void initViews() {
        backArrow = findViewById(R.id.timePickerBackArrow);
        skip = findViewById(R.id.timePickerSkip);
        btn = findViewById(R.id.timePickerBtn);

        backArrow.setOnClickListener(this);
        skip.setOnClickListener(this);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case (R.id.timePickerBackArrow):
                startActivity(new Intent(this, InitialSettingStepActivity.class));
                break;

            case (R.id.timePickerSkip):
                startActivity(new Intent(this, AllInstalledAppsActivity.class));

            case (R.id.timePickerBtn):
                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(18)
                        .setMinute(00)
                        .build();

                timePicker.show(getSupportFragmentManager(), "TIME_PICKER");

                timePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();
                        int lockTimeMinute = Time.convertMinute(hour, minute);
                        db.updateLockTime(lockTimeMinute);
                        getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                                .putInt("lock_minute", lockTimeMinute).commit();
                        startActivity(new Intent(TimePickerActivity.this, AllInstalledAppsActivity.class));
                    }
                });
            }
        }
    }
