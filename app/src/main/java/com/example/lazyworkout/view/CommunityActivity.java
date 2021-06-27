package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.lazyworkout.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class CommunityActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        initViews();
    }

    private void initViews() {
        bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setSelectedItemId(R.id.navOverview);

        bottomNav.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navOverview:
                startActivity(new Intent(CommunityActivity.this, OverviewActivity.class));
                overridePendingTransition(0,0);
                return true;

            case R.id.navAchievement:
                startActivity(new Intent(CommunityActivity.this, AchievementActivity.class));
                overridePendingTransition(0,0);
                return true;

            case R.id.navCommunity:
                return true;

            case R.id.navSetting:
                startActivity(new Intent(CommunityActivity.this, SettingActivity.class));
                overridePendingTransition(0,0);
                return true;
        }
        return false;
    }
}