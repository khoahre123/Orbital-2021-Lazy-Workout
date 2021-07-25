package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lazyworkout.R;
import com.example.lazyworkout.model.User;
import com.example.lazyworkout.service.LockService;
import com.example.lazyworkout.service.StepCountingService;
import com.example.lazyworkout.util.Constant;
import com.example.lazyworkout.util.Database;
import com.example.lazyworkout.util.Time;
import com.example.lazyworkout.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener, SensorEventListener {
    private static final String TAG = "OverviewActivity";

    public static float distanceGoal;
    public static float stepSize;

    DecimalFormat formatter = new DecimalFormat("#.##");

    Database db = new Database();

    private CircularProgressBar progressBar;
    private TextView distancesTaken, goal, unit, currStreak, lockStatus, lockTime;
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

        getCurrentStreak();
        initViews();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            //ask for permission
            Log.d(TAG, "ask permission");
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, Constant.PHYSICAL_ACTIVITY);
        }

        intent = new Intent(this, StepCountingService.class);
        startService(new Intent(getBaseContext(), StepCountingService.class));
        startService(new Intent(getBaseContext(), LockService.class));//TODO

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
        currStreak = findViewById(R.id.overviewStreak);
        lockStatus = findViewById(R.id.overviewLockStatus); //TODO: impl lock app
        lockTime = findViewById(R.id.overviewLockTime); //TODO: impl initial setting

        editGoal = findViewById(R.id.editGoal);
        editLockApp = findViewById(R.id.editLock); //TODO: routing to setting

        bottomNav = findViewById(R.id.bottomNav);

        progressBar.setProgressWithAnimation(currentDistances);
        progressBar.setProgressMax(distanceGoal);

        String currStreakTxt = getSharedPreferences(db.getID(), Context.MODE_PRIVATE)
                .getString("current_streak", "calculating...");
        currStreak.setText("current streak: " + currStreakTxt);
        distancesTaken.setText(String.valueOf((int) currentDistances));
        goal.setText("/" + formatter.format(distanceGoal));

        stats.setOnClickListener(this);
        editGoal.setOnClickListener(this);
        unit.setOnClickListener(this);

        bottomNav.setSelectedItemId(R.id.navOverview);

        bottomNav.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public void onClick(View v) {
        showSteps = !showSteps;
        switch (v.getId()) {
            case (R.id.overviewStats):
            case (R.id.overviewLayout):

            case (R.id.overviewUnit):
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
                overridePendingTransition(0, 0);
                return true;

            case R.id.navCommunity:
                startActivity(new Intent(OverviewActivity.this, CommunityActivity.class));
                overridePendingTransition(0, 0);
                return true;

            case R.id.navSetting:
                startActivity(new Intent(OverviewActivity.this, SettingActivity.class));
                overridePendingTransition(0, 0);
                return true;

        }
        return true;
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

    public void getCurrentStreak() {
        Log.d(TAG, "get current streak start");
        DocumentReference userRef = db.fStore.collection(db.DB_NAME).document(db.getID());
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "get current streak ongoing");
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    User user = document.toObject(User.class);
                    Map<String, Object> map = document.getData();
                    int streak = 0;
                    float todayDistance = user.getDistances(Time.getToday());
                    if (document.getLong("longestDay") == null) {
                        Map<String, Object> longestDay = new HashMap<>();
                        longestDay.put("longestDay", todayDistance);
                        FirebaseFirestore.getInstance().collection("users").document(db.getID()).set(longestDay, SetOptions.merge());
                    }
                    getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("todayDistance", todayDistance).apply();
                    Log.d(TAG, "goal = " + distanceGoal);

                    if (todayDistance >= distanceGoal) {
                        streak++;
                        Log.d(TAG, "today counted, distance = " + todayDistance + ", streak = 1");
                    }

                    long time = Time.getToday() - Time.ONE_DAY;
                    float distance = user.getDistances(time);
                    while (distance >= distanceGoal) {
                        streak++;
                        time = time - Time.ONE_DAY;
                        distance = user.getDistances(time);
                        Log.d(TAG, "time = " + time + ",distance = " + distance + ", streak = " + streak);
                    }
                    Log.d(TAG, "current streak = " + streak);
                    Map<String, Object> update = new HashMap<>();
                    update.put("currentStreak", streak);
                    FirebaseFirestore.getInstance().collection("users").document(db.getID()).set(update, SetOptions.merge());
                    if (document.getLong("longestStreak") == null) {
                        Map<String, Object> longestStreak = new HashMap<>();
                        longestStreak.put("longestStreak", streak);
                        FirebaseFirestore.getInstance().collection("users").document(db.getID()).set(longestStreak, SetOptions.merge());
                    }
                    currStreak.setText(" current streak: " + streak + " days");
                        getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                            .putString("current_streak", streak + " days").commit();
                } else {
                    Log.d(TAG, "no such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }
}


