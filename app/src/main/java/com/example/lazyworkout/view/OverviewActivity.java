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
import android.widget.TextView;
import android.widget.Toast;


import com.example.lazyworkout.R;
import com.example.lazyworkout.service.StepCountingService;
import com.example.lazyworkout.util.Constant;
import com.example.lazyworkout.util.Database;
import com.example.lazyworkout.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.jetbrains.annotations.NotNull;

import java.security.Permission;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.Locale;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener, SensorEventListener {
    private static final String TAG = "OverviewActivity";

    public static float distanceGoal;
    public static float stepSize;

    DecimalFormat formatter = new DecimalFormat("#.##");

    Database db = new Database();

    private CircularProgressBar progressBar;
    private TextView distancesTaken, goal, unit, streak, lockStatus, lockTime;
    private LinearLayout stats;
    private MaterialCardView editGoal, editLockApp;
    private BottomNavigationView bottomNav;

    private Intent intent;
    private SensorManager sensorManager;
    
    private boolean showSteps = true;

    private float currentDistances;



    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        distanceGoal = getSharedPreferences(db.getID(), Context.MODE_PRIVATE)
                .getFloat("goal", Constant.DEFAULT_GOAL);
        stepSize = getSharedPreferences(db.getID(), Context.MODE_PRIVATE)
                .getFloat("step_size", Constant.DEFAULT_STEP_SIZE);

        initViews();

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            Log.d(TAG, "ask permission");
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, Constant.PHYSICAL_ACTIVITY);
        }

        intent = new Intent(this, StepCountingService.class);
        startService(new Intent(getBaseContext(), StepCountingService.class));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        registerReceiver(broadcastReceiver, new IntentFilter(StepCountingService.BROADCAST_ACTION));
    }

    private void initViews() {
        Log.d(TAG, "initViews");
        progressBar = findViewById(R.id.overviewProgressCircular);
        distancesTaken = (TextView) findViewById(R.id.overviewStepsTaken);
        goal = findViewById(R.id.overviewGoal);
        stats = findViewById(R.id.overviewStats);
        unit = findViewById(R.id.overviewUnit);
        streak = findViewById(R.id.overviewStreak); //TODO: impl compute streak
        lockStatus = findViewById(R.id.overviewLockStatus); //TODO: impl lock app
        lockTime = findViewById(R.id.overviewLockTime); //TODO: impl initial setting

        editGoal = findViewById(R.id.editGoal);
        editLockApp = findViewById(R.id.editLock); //TODO: routing to setting

        bottomNav = findViewById(R.id.bottomNav);

        progressBar.setProgressWithAnimation(currentDistances);
        progressBar.setProgressMax(distanceGoal);

        distancesTaken.setText(String.valueOf((int) currentDistances));
        goal.setText("/" + formatter.format(distanceGoal));

        stats.setOnClickListener(this);
        editGoal.setOnClickListener(this);

        bottomNav.setSelectedItemId(R.id.navOverview);

        bottomNav.setOnNavigationItemSelectedListener(this);

    }

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
                startActivity(new Intent(this, SettingActivity.class));
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
        distancesTaken.setText(formatter.format(currentDistances));
        goal.setText("/" + formatter.format(distanceGoal));
        unit.setText("km");

    }

    private void changedToStep() {
        int currentSteps = Util.computeStepcount(currentDistances, stepSize);
        int stepGoal = Util.computeStepcount(distanceGoal, stepSize);

        distancesTaken.setText(String.valueOf((int) currentSteps));
        goal.setText("/" + formatter.format(stepGoal));
        unit.setText("steps");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor sensor = (Sensor) sensorManager.getDefaultSensor(Constant.DEFAULT_SENSOR);

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
        currentDistances = intent.getExtras().getFloat("today_distance");
        Log.d(TAG, formatter.format(currentDistances));

        progressBar.setProgressWithAnimation(currentDistances);

        if (showSteps) {
            changedToStep();
        } else {
            changedToDistance();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float currentSteps = event.values[0];
        currentDistances = (float) (currentSteps * stepSize);
        Log.d(TAG, formatter.format(currentDistances));
        if (showSteps) {
            distancesTaken.setText(formatter.format((int) currentSteps));
            progressBar.setProgressWithAnimation(currentSteps);
        } else {
            distancesTaken.setText(formatter.format(currentDistances));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


