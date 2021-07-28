package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lazyworkout.R;
import com.example.lazyworkout.model.User;
import com.example.lazyworkout.service.LocationService;
import com.example.lazyworkout.service.LockService;
import com.example.lazyworkout.service.LockWorker;
import com.example.lazyworkout.service.StepCountingService;
import com.example.lazyworkout.service.TrackingWorker;
import com.example.lazyworkout.util.Constant;
import com.example.lazyworkout.util.Database;
import com.example.lazyworkout.util.Time;
import com.example.lazyworkout.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener, SensorEventListener {
    private static final String TAG = "OverviewActivity";

    public static float distanceGoal;
    public static float stepSize;

    public static int lockMinute;

    private boolean prevPermissionGranted = true;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 2323;
    private String uid = FirebaseAuth.getInstance().getUid();

    DecimalFormat formatter = new DecimalFormat("#.##");

    Database db = new Database();

    private CircularProgressBar progressBar;
    private TextView distanceTitle, lockTitle, lockStatusTitle, lockTimeTitle, distancesTaken, goal, unit, currStreak, lockStatus, lockTime;
    private LinearLayout stats;
    private MaterialCardView editGoal, editLockApp;
    private BottomNavigationView bottomNav;

    private Intent intent;
    private SensorManager sensorManager;

    private boolean showSteps = true;

    private float currentDistances;

    public OverviewActivity() {}


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        distanceGoal = getSharedPreferences(uid, Context.MODE_PRIVATE)
                .getFloat("goal", Constant.DEFAULT_GOAL);

        stepSize = getSharedPreferences(db.getID(), Context.MODE_PRIVATE)
                .getFloat("step_size", Constant.DEFAULT_STEP_SIZE);
        lockMinute = getSharedPreferences(db.getID(), Context.MODE_PRIVATE)
                .getInt("lock_minute", Constant.LOCK_TIME);



        getCurrentStreak();
        initViews();
        startTrackingService();
        startLockingServiceViaWorker();

//        intent = new Intent(this, StepCountingService.class);

//        startService(new Intent(getBaseContext(), LockService.class));//TODO


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        registerReceiver(broadcastReceiver, new IntentFilter(StepCountingService.BROADCAST_ACTION));

    }

    public void startTrackingServiceViaWorker() {
        Log.d(TAG, "startTrackingServiceViaWorker called");
        String UNIQUE_WORK_NAME = "StartTrackingServiceViaWorker";
        WorkManager workManager = WorkManager.getInstance(this);

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
        // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        TrackingWorker.class,
                        16,
                        TimeUnit.MINUTES)
                        .build();

        // to schedule a unique work, no matter how many times app is opened i.e. startTrackingServiceViaWorker gets called
        // do check for AutoStart permission
        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);

    }

    public void startLockingServiceViaWorker() {
        Log.d(TAG, "startLockingServiceViaWorker called");
        String UNIQUE_WORK_NAME = "StartLockingServiceViaWorker";
        WorkManager workManager = WorkManager.getInstance(this);
        startService(new Intent(this, LockService.class));

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
        // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        LockWorker.class,
                        16,
                        TimeUnit.MINUTES)
                        .build();

        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
        // do check for AutoStart permission
        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);

    }


    @AfterPermissionGranted(98)
    private void startTrackingService() {
        String[] perm = {Manifest.permission.ACTIVITY_RECOGNITION};
        if (EasyPermissions.hasPermissions(this, perm)) {
            Log.d(TAG, "have permission");
            startService(new Intent(this, StepCountingService.class));

            //TODO: foreground
//            Log.d(TAG, "startService called");
//            if (!StepCountingService.isServiceRunning) {
//                Intent serviceIntent = new Intent(this, StepCountingService.class);
//                ContextCompat.startForegroundService(this, serviceIntent);

            startTrackingServiceViaWorker();

        } else {
            Log.d(TAG, "NOT done permission");
            EasyPermissions.requestPermissions(this, "Your location is needed for better community engagement", 98, perm);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public boolean isAllPermissionGranted() {
        if (!(permissionOverlayWindowGranted())) {
            Log.d(TAG, "overlay window not granted");
            prevPermissionGranted = false;
            requestPermissionOverlayWindow();
            return true;
        } else {
            Log.d(TAG, "usage stats granted");
            if (!(permissionUsageStatsGranted())) {
                Log.d(TAG, "usage stat not granted");
                prevPermissionGranted = false;
                requestPermissionUsageStats();
                return true;
            } else {
                Log.d(TAG, "all permission granted");
                return true;
            }
        }

    }

    public void startService() {
        Log.d(TAG, "startService called");
        if (!StepCountingService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, StepCountingService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }
    }

    public void onStartServiceClick(View v) {
        startService();
    }

    public boolean permissionOverlayWindowGranted() {
        return ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) &&
                Settings.canDrawOverlays(this));
    }

    public boolean permissionUsageStatsGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void requestPermissionOverlayWindow() {
        Log.d(TAG, "request overlay");
        // Check if Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers

            new MaterialAlertDialogBuilder(this)
                    .setTitle("User Permission")
                    .setMessage("Please allow " +
                            "LazyWorkout to appear on top of other apps first. " +
                            "The permission is used only to show lockscreen when user does not achieve daily walking goal.")
                    .setNeutralButton("Go to settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                        }
                    })
                    .setCancelable(false)
                    .show();

        }
    }

    public void requestPermissionUsageStats() {
        Log.d(TAG, "request usage stats");
        new MaterialAlertDialogBuilder(this)
                .setTitle("User Permission")
                .setMessage("Please allow " +
                        "LazyWorkout to access usage data first. " +
                        "Usage data is used only to get the list of all installed apps")
                .setNeutralButton("Go to settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void initViews() {
        Log.d(TAG, "initViews");
        progressBar = findViewById(R.id.overviewProgressCircular);
        distancesTaken = (TextView) findViewById(R.id.overviewStepsTaken);
        distanceTitle = findViewById(R.id.overviewDistance);
        lockStatusTitle = findViewById(R.id.overviewLockStatus);
        lockTimeTitle = findViewById(R.id.overviewLockTime);
        lockTitle = findViewById(R.id.overviewLockTitle);
        lockStatus = findViewById(R.id.overviewLockStatus);
        goal = findViewById(R.id.overviewGoal);
        stats = findViewById(R.id.overviewStats);
        unit = findViewById(R.id.overviewUnit);
        currStreak = findViewById(R.id.overviewStreak);
        lockStatus = findViewById(R.id.overviewLockStatus); //TODO: impl lock app
        lockTime = findViewById(R.id.overviewLockTime); //TODO: impl initial setting

        setTextViewDrawableColor(distanceTitle, R.color.black);
        setTextViewDrawableColor(lockTitle, R.color.black);
        setTextViewDrawableColor(lockStatusTitle, R.color.black);
        setTextViewDrawableColor(lockTimeTitle, R.color.black);

        formatStringLockStatus();
        formatStringLockTime();

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
        editLockApp.setOnClickListener(this);
        unit.setOnClickListener(this);

        bottomNav.setSelectedItemId(R.id.navOverview);

        bottomNav.setOnNavigationItemSelectedListener(this);

    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(getColor(color), PorterDuff.Mode.SRC_IN));
            }
        }
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
                Intent intent = new Intent(this, SettingGoalActivity.class);;
                startActivity(intent);
                break;

            case (R.id.editLock):
                startActivity(new Intent(this, SettingLockActivity.class));
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

    private void formatStringLockStatus() {
        String activated = isLockActivated() ? " ON" : " OFF";
        String statusText = getString(R.string.lock_status);

        Spannable spannable = new SpannableString(statusText + activated);

        if (isLockActivated()) {
            spannable.setSpan(new ForegroundColorSpan(Color.RED), statusText.length(),
                    (statusText + activated).length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannable.setSpan(new ForegroundColorSpan(Color.BLUE), statusText.length(),
                    (statusText + activated).length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), statusText.length(),
                (statusText + activated).length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        lockStatus.setText(spannable, TextView.BufferType.SPANNABLE);
    }

    private void formatStringLockTime() {

        String timeText = getString(R.string.lock_time);
        String timePeriod = " " + Time.getTime(lockMinute) + " - 12:00 AM";
        String time = " " + Time.getTime(lockMinute);

        if (isLockActivated()) {
            Spannable spannable = new SpannableString(timeText + timePeriod);

            spannable.setSpan(new ForegroundColorSpan(Color.BLACK),
                    timeText.length(), (timeText + timePeriod).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    timeText.length(), (timeText + timePeriod).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            lockTime.setText(spannable, TextView.BufferType.SPANNABLE);
        } else {
            if (currentDistances >= distanceGoal) {
                lockTime.setText(timeText + " nice bro no lock today");
            } else {
                Spannable spannable = new SpannableString(timeText + time);

                spannable.setSpan(new ForegroundColorSpan(Color.BLACK),
                        timeText.length(), (timeText + time).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                        timeText.length(), (timeText + time).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                lockTime.setText(spannable, TextView.BufferType.SPANNABLE);
            }

        }


    }

    public boolean isLockActivated() {
        return (currentDistances < distanceGoal) && (Time.isLockTime(System.currentTimeMillis(), lockMinute));
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
        Log.d(TAG, "onResume");
        super.onResume();

        isAllPermissionGranted();

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
        if (intent.getStringExtra("command").equals("not_update")) {
            currentDistances = intent.getExtras().getFloat("today_distance");
//            Log.d(TAG, formatter.format(currentDistances));

            progressBar.setProgressWithAnimation(currentDistances);

            formatStringLockStatus();
            formatStringLockTime();

            if (showSteps) {
                changedToStep();
            } else {
                changedToDistance();
                if (showSteps) {
                    changedToStep();
                } else {
                    changedToDistance();
                }
            }
        }else{
                getCurrentStreak();
            }
        }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float currentSteps = event.values[0];
        Float todayDistance = getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("todayDistance", 0);
        Float totalDistances = getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("totalDistances", 0);
        Float longestDay = getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("longestDay", 0);
        currentDistances = (float) (currentSteps * stepSize);
        todayDistance += currentDistances;
        totalDistances += currentDistances;
        getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("totalDistances", totalDistances).apply();
        getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("todayDistance", todayDistance).apply();
        if (todayDistance > longestDay) {
            getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("longestDay", todayDistance).apply();
        }
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
        if (uid != null) {
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
                        getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("todayDistance", todayDistance).apply();
                        // Check if the field has been created on firebase or not, if not then created. Also update value in local database
                        if (document.getLong("longestDay") == null) {
                            Map<String, Object> longestDay = new HashMap<>();
                            longestDay.put("longestDay", todayDistance);
                            getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("longestDay", todayDistance).apply();
                            FirebaseFirestore.getInstance().collection("users").document(db.getID()).set(longestDay, SetOptions.merge());
                        } else { // Compare value of current distance vs longest distance and update if needed
                            Integer longestDay = Math.round(getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("longestDay", 0));
                            if (todayDistance > longestDay) {
                                getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("longestDay", todayDistance).apply();
                            }
                        }
                        getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("todayDistance", todayDistance).apply();
                        Log.d(TAG, "goal = " + distanceGoal);
                        if (todayDistance >= distanceGoal) {
                            streak++;
                            Log.d(TAG, "today counted, distance = " + todayDistance + ", streak = 1");
                        }

                        long time = Time.getToday() - Time.ONE_DAY_MILLIS;
                        float distance = user.getDistances(time);
                        while (distance >= distanceGoal) {
                            streak++;
                            time = time - Time.ONE_DAY_MILLIS;
                            distance = user.getDistances(time);
                            Log.d(TAG, "time = " + time + ",distance = " + distance + ", streak = " + streak);
                        }
                        Log.d(TAG, "current streak = " + streak);
                        getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("currentStreak", streak).apply();
                        // Check if the field has been created on firebase or not, if not then created. Also update value in local database
                        if (document.getLong("longestStreak") == null) {
                            Map<String, Object> longestStreak = new HashMap<>();
                            longestStreak.put("longestStreak", streak);
                            longestStreak.put("currentStreak", streak);
                            getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("longestStreak", streak).apply();
                            FirebaseFirestore.getInstance().collection("users").document(db.getID()).set(longestStreak, SetOptions.merge());
                        } else { // Compare value of current distance vs longest distance and update if needed
                            Integer longestStreak = Math.round(getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("longestStreak", 0));
                            if (streak > longestStreak) {
                                getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("longestStreak", streak).apply();
                            }
                        }
                        currStreak.setText(" current streak: " + streak + " days");
                        getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                                .putString("current_streak", streak + " days").commit();
                        Integer totalDistances = Math.round(user.getTotalDistances());
                        getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("totalDistances", totalDistances).apply();
                    } else {
                        Log.d(TAG, "no such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                sendBroadcast(new Intent("com.example.lazyworkout.achievement"));
            });
        }

    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }
}


