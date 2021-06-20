package com.example.lazyworkout.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lazyworkout.R;
import com.example.lazyworkout.util.Time;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.Locale;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    private static final String TAG = "OverviewActivity";

    public static final int DEFAULT_GOAL = 2500;
    public static final double DEFAULT_STEP_SIZE = 0.00075;
    public static final int DEFAULT_SENSOR = Sensor.TYPE_ACCELEROMETER;
    public static final boolean IS_STEP_COUNTER = false;
    DecimalFormat formatter = new DecimalFormat("#.##");


    private CircularProgressBar progressBar;
    private TextView stepsTaken, goal, unit;
    private LinearLayout stats;

    private SensorManager sensorManager;

    private boolean running = false;
    private float totalSteps = 0;
    private float previousTotalSteps = 0;
    private float currentSteps = 0;
    private boolean showSteps = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        loadData();
        initViews();

        Log.d(TAG, "onCreate: " + "current steps = " + String.valueOf(currentSteps));
//        resetSteps();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onClick(View v) {
        showSteps = !showSteps;
        switch (v.getId()) {
            case (R.id.overviewStats):
                if (showSteps) {
                    changedToStep();
                } else {
                    changedToDistance();
                }
                break;
        }
    }

    private void initViews() {
        progressBar = findViewById(R.id.overviewProgressCircular);
        stepsTaken = findViewById(R.id.overviewStepsTaken);
        goal = findViewById(R.id.overviewGoal);
        stats = findViewById(R.id.overviewStats);
        unit = findViewById(R.id.overviewUnit);

        stepsTaken.setText(formatter.format(currentSteps));
        goal.setText("/" + formatter.format(DEFAULT_GOAL));
        stats.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: " + "current steps = " + String.valueOf(currentSteps));
        super.onResume();
        running = true;
        Sensor sensor = (Sensor) sensorManager.getDefaultSensor(DEFAULT_SENSOR);

        if (sensor == null) {
            Toast.makeText(this, "No sensor detected in this device",
                    Toast.LENGTH_LONG).show();
        } else {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }

        if (System.currentTimeMillis() == Time.MIDNIGHT) {
            resetSteps();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: " + "current steps = " + String.valueOf(currentSteps));
        super.onDestroy();
        saveData();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {
            totalSteps = computeSteps(event);
            currentSteps = ((int) totalSteps) - ((int) previousTotalSteps);
            if (showSteps) {
                stepsTaken.setText(formatter.format(currentSteps));
                progressBar.setProgressWithAnimation(currentSteps);
            } else {
                stepsTaken.setText(formatter.format(currentSteps * DEFAULT_STEP_SIZE));
            }
        }
    }

    private void changedToDistance() {
        double distance = currentSteps * DEFAULT_STEP_SIZE;
        double distanceGoal = DEFAULT_GOAL * DEFAULT_STEP_SIZE;
        stepsTaken.setText(formatter.format(distance));
        goal.setText("/" + formatter.format(distanceGoal));
        unit.setText("km");
    }

    private void changedToStep() {
        stepsTaken.setText(String.valueOf((int) currentSteps));
        goal.setText("/" + formatter.format(DEFAULT_GOAL));
        unit.setText("steps");
    }

    private float computeSteps(SensorEvent event) {
        if (IS_STEP_COUNTER) {
            float realSteps = event.values[0];
            return realSteps;
        } else {
            float X = event.values[0];
            float Y = event.values[1];
            float Z = event.values[2];
            float fakeSteps = (float) (currentSteps + (float) 0.1 * Math.sqrt(X*X + Y*Y + Z*Z));
            return fakeSteps;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void resetSteps() {
        currentSteps = 0;
        previousTotalSteps = totalSteps;
        stepsTaken.setText(formatter.format(0));
        saveData();
        initViews();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat("steps_before_today", previousTotalSteps).apply();
        sharedPreferences.edit().putFloat("steps_today", currentSteps).apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        float savedStepsBefore = sharedPreferences.getFloat("steps_before_today", 0);
        float savedStepsToday = sharedPreferences.getFloat("steps_today", 0);
        Log.d(TAG, formatter.format(savedStepsBefore));
        Log.d(TAG, formatter.format(savedStepsToday));
        previousTotalSteps = savedStepsBefore;
        currentSteps = savedStepsToday;
        Log.d(TAG, "load data: " + "current steps = " + String.valueOf(currentSteps));
    }




}


