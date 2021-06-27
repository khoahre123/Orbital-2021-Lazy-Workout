package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lazyworkout.R;
import com.example.lazyworkout.service.StepCountingService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.jetbrains.annotations.NotNull;

import java.security.Permission;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.Locale;

<<<<<<< HEAD
public class OverviewActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener, SensorEventListener {
=======
import kotlin.collections.IntIterator;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
>>>>>>> d2f512f1a0507b0d8191526e3b6cfdf6dac2bbb6
    private static final String TAG = "OverviewActivity";

    public static final int DEFAULT_GOAL = 2500;
    public static final double DEFAULT_STEP_SIZE = 0.00075;
    public static final int DEFAULT_SENSOR = Sensor.TYPE_STEP_COUNTER;
    private static final int PHYSICAL_ACTIVITY = 1;
    DecimalFormat formatter = new DecimalFormat("#.##");


    private CircularProgressBar progressBar;
    private TextView stepsTaken, goal, unit;
    private LinearLayout stats;
<<<<<<< HEAD
    private BottomNavigationView bottomNav;
=======
    private RelativeLayout overview;
    private Button edit;
>>>>>>> d2f512f1a0507b0d8191526e3b6cfdf6dac2bbb6

    private Intent intent;
    private SensorManager sensorManager;

    private boolean running = false;
    private float totalSteps = 0;
    private float previousTotalSteps = 0;
    
    private boolean showSteps = true;

    private float currentSteps;


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        initViews();

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYSICAL_ACTIVITY);
        }

        intent = new Intent(this, StepCountingService.class);
        startService(new Intent(getBaseContext(), StepCountingService.class));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        registerReceiver(broadcastReceiver, new IntentFilter(StepCountingService.BROADCAST_ACTION));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

        }
    }

<<<<<<< HEAD
=======
    @Override
    public void onClick(View v) {
        showSteps = !showSteps;
        switch (v.getId()) {
            case (R.id.overviewStats):
            case (R.id.overviewLayout):
                if (showSteps) {
                    changedToStep();
                } else {
                    changedToDistance();
                }
                break;

            case (R.id.editGoal):
                onPause();
                startActivity(new Intent(this, SetGoalActivity.class));
                break;

            default:
                break;
        }
    }

>>>>>>> d2f512f1a0507b0d8191526e3b6cfdf6dac2bbb6
    private void initViews() {
        Log.d(TAG, "initViews");
        progressBar = findViewById(R.id.overviewProgressCircular);
        stepsTaken = (TextView) findViewById(R.id.overviewStepsTaken);
        overview = findViewById(R.id.overviewLayout);
        goal = findViewById(R.id.overviewGoal);
        stats = findViewById(R.id.overviewStats);
        unit = findViewById(R.id.overviewUnit);
<<<<<<< HEAD
        bottomNav = findViewById(R.id.bottomNav);
=======
        edit = findViewById(R.id.editGoal);
>>>>>>> d2f512f1a0507b0d8191526e3b6cfdf6dac2bbb6

        progressBar.setProgressWithAnimation(currentSteps);
        stepsTaken.setText(String.valueOf((int) currentSteps));
        goal.setText("/" + formatter.format(DEFAULT_GOAL));

        stats.setOnClickListener(this);
        overview.setOnClickListener(this);
        edit.setOnClickListener(this);

        bottomNav.setSelectedItemId(R.id.navOverview);

        bottomNav.setOnNavigationItemSelectedListener(this);

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

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navOverview:
                return true;

            case R.id.navAchievement:
                startActivity(new Intent(OverviewActivity.this, AchievementActivity.class));
                overridePendingTransition(0,0);
                return true;

            case R.id.navCommunity:
                startActivity(new Intent(OverviewActivity.this, CommunityActivity.class));
                overridePendingTransition(0,0);
                return true;

            case R.id.navSetting:
                startActivity(new Intent(OverviewActivity.this, SettingActivity.class));
                overridePendingTransition(0,0);
                return true;

        }
        return false;
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO: IMPL SENSORMANAGER
        Sensor sensor = (Sensor) sensorManager.getDefaultSensor(DEFAULT_SENSOR);

        if (sensor == null) {
            Toast.makeText(this, "No sensor detected in this device",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

        private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        // call updateUI passing in our intent which is holding the data to display.
        updateViews(intent);
    }
};

    private void updateViews(Intent intent) {
        currentSteps = intent.getExtras().getFloat("today_distance");
        Log.d(TAG, formatter.format(currentSteps));

        progressBar.setProgressWithAnimation(currentSteps);

        if (showSteps) {
            changedToStep();
        } else {
            changedToDistance();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        currentSteps = event.values[0];
        Log.d(TAG, formatter.format(currentSteps));
        if (showSteps) {
            stepsTaken.setText(formatter.format(currentSteps));
            progressBar.setProgressWithAnimation(currentSteps);
        } else {
            stepsTaken.setText(formatter.format(currentSteps * DEFAULT_STEP_SIZE));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


