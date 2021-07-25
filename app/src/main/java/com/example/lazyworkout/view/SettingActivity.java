package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import com.example.lazyworkout.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.internal.bind.DateTypeAdapter;

import org.jetbrains.annotations.NotNull;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNav;
    private static final String TAG = "SetGoalActivity";

    private AutoCompleteTextView setGoalInput, setStepInput;
    private Button confirmButton, signOutButton;
    private String[] goal = {"4.0 km", "4.5 km", "5.0 km", "5.5 km", "6.0 km", "6.5 km", "7.0 km", "7.5 km",
            "8.0 km", "8.5 km", "9.0 km", "9.5 km", "10.0 km"};
    private String[] foot = {"40 cm", "45 cm", "50 cm", "55 cm", "60 cm", "65 cm",
            "70 cm", "75 cm", "80 cm", "85 cm", "90 cm"};
    private String goalSelected;
    private Integer footSelected;

    Database db = new Database();

    //TODO Apply a default value that user selected
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();

    }

    public void initView() {
        setGoalInput = findViewById(R.id.setGoalInput);
        setStepInput = findViewById(R.id.setStepInput);
        confirmButton = findViewById(R.id.confirmButton);
        signOutButton = findViewById(R.id.signOutBtn);
        bottomNav = findViewById(R.id.bottomNav);
        confirmButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        ArrayAdapter<String> goalArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, goal);
        setGoalInput.setAdapter(goalArray);
        setGoalInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                float goal = Util.getGoal(item);
                db.updateGoal(goal);
                getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                        .putFloat("goal", (float) goal).commit();
            }
        });
        ArrayAdapter<String> stepArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foot);
        setStepInput.setAdapter(stepArray);
        setStepInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                float stepSize = Util.getStepsize(item);
                db.updateStepsize(stepSize);
                getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                        .putFloat("step_size", stepSize).commit();
            }
        });

        bottomNav.setSelectedItemId(R.id.navSetting);
        bottomNav.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case (R.id.confirmButton):
                if (goalSelected == null & footSelected == null) {
                    startActivity(new Intent(this, OverviewActivity.class));
                    break;
                }

            case (R.id.signOutBtn):
                getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                        .putFloat("since_boot", StepCountingService.sinceBoot).commit();

                getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                        .putFloat("today_distance", StepCountingService.todayDistances).commit();

                boolean doneSignout = db.signout();
                stopService(new Intent(this, StepCountingService.class));

                startActivity(new Intent(this, LoginActivity.class));

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