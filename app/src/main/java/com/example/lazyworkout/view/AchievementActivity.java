package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.lazyworkout.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class AchievementActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private Button signOut;
    private BottomNavigationView bottomNav;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        initViews();

    }

    private void initViews() {
        signOut = findViewById(R.id.signOut);
        bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setSelectedItemId(R.id.navOverview);

        signOut.setOnClickListener(this);
        bottomNav.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navOverview:
                startActivity(new Intent(AchievementActivity.this, OverviewActivity.class));
                overridePendingTransition(0,0);
                return true;

            case R.id.navAchievement:
                return true;

            case R.id.navCommunity:
                startActivity(new Intent(AchievementActivity.this, CommunityActivity.class));
                overridePendingTransition(0,0);
                return true;

            case R.id.navSetting:
                startActivity(new Intent(AchievementActivity.this, SettingActivity.class));
                overridePendingTransition(0,0);
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.signOut):
                auth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
        }
    }
}