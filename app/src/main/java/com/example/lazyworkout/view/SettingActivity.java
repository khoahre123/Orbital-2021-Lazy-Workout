package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.lazyworkout.MainActivity;
import com.example.lazyworkout.R;
import com.example.lazyworkout.service.StepCountingService;
import com.example.lazyworkout.util.Database;
import com.example.lazyworkout.util.Time;
import com.example.lazyworkout.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.gson.internal.bind.DateTypeAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNav;
    private static final String TAG = "SetGoalActivity";

    private AutoCompleteTextView setGoalInput, setStepInput;
    private Button confirmButton, signOutButton;
    private CardView setGoalCardView, setSizeCardView, listAppCardView, lockedAppCardView, lockTimeCardView;
    private String[] goal = {"4.0 km", "4.5 km", "5.0 km", "5.5 km", "6.0 km", "6.5 km", "7.0 km", "7.5 km",
            "8.0 km", "8.5 km", "9.0 km", "9.5 km", "10.0 km"};
    private String[] foot = {"40 cm", "45 cm", "50 cm", "55 cm", "60 cm", "65 cm",
            "70 cm", "75 cm", "80 cm", "85 cm", "90 cm"};
    private String goalSelected;
    private Integer footSelected;
    private String uid = FirebaseAuth.getInstance().getUid();
    private String userType;

    Database db = new Database();

    //TODO Apply a default value that user selected
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        for (UserInfo user: Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getProviderData()) {

            if (user.getProviderId().equals("google.com")) {
                userType = "google";
            }
        }
    }

    public void initView() {
        setGoalCardView = findViewById(R.id.setGoalCardView);
        setSizeCardView = findViewById(R.id.setSizeCardView);
        listAppCardView = findViewById(R.id.listAppCardView);
        lockedAppCardView = findViewById(R.id.lockedAppCardView);
        lockTimeCardView = findViewById(R.id.lockTimeCardView);
        signOutButton = findViewById(R.id.signOutBtn);
        bottomNav = findViewById(R.id.bottomNav);
        signOutButton.setOnClickListener(this);
        ArrayAdapter<String> stepArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foot);
        bottomNav.setSelectedItemId(R.id.navSetting);
        bottomNav.setOnNavigationItemSelectedListener(this);
        setSizeCardView.setOnClickListener(this);
        setGoalCardView.setOnClickListener(this);
        listAppCardView.setOnClickListener(this);
        lockTimeCardView.setOnClickListener(this);
        lockedAppCardView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case (R.id.setGoalCardView):
                startActivity(new Intent(this, SettingGoalActivity.class));
                break;

            case (R.id.setSizeCardView):
                startActivity(new Intent(this, SettingStepActivity.class));
                break;

            case (R.id.listAppCardView):
                startActivity(new Intent(this, SettingInstalledAppsActivity.class));
                break;

            case (R.id.lockedAppCardView):
                startActivity(new Intent(this, SettingLockedAppsActivity.class));
                break;

            case (R.id.lockTimeCardView):
                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(18)
                        .setMinute(00)
                        .setTitleText("Daily starting locktime")
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
                        getSharedPreferences(uid, Context.MODE_PRIVATE).edit()
                                .putInt("lock_minute", lockTimeMinute).commit();
                    }
                });
                break;

            case (R.id.signOutBtn):
                if (uid != null) {
                    getSharedPreferences(uid, Context.MODE_PRIVATE).edit()
                            .putFloat("since_boot", StepCountingService.sinceBoot).commit();

                    getSharedPreferences(uid, Context.MODE_PRIVATE).edit()
                            .putFloat("today_distance", StepCountingService.todayDistances).commit();
                }
                Log.d(TAG, userType);
                if (userType.equals("google")) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    boolean doneSignout = db.signout();
                    stopService(new Intent(this, StepCountingService.class));
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;


            default:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navOverview:
                startActivity(new Intent(SettingActivity.this, OverviewActivity.class));
                overridePendingTransition(0,0);
                return true;

            case R.id.navAchievement:
                startActivity(new Intent(SettingActivity.this, AchievementActivity.class));
                overridePendingTransition(0,0);
                return true;

            case R.id.navCommunity:
                startActivity(new Intent(SettingActivity.this, CommunityActivity.class));
                overridePendingTransition(0,0);
                return true;

            case R.id.navSetting:
                return true;
        }
        return true;
    }

}