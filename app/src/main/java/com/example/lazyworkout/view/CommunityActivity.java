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

import org.jetbrains.annotations.NotNull;

public class CommunityActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private Button goToChat;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        initViews();
    }

    private void initViews() {
        goToChat = findViewById(R.id.communityGoToChatBtn);
        bottomNav = findViewById(R.id.bottomNav);

        goToChat.setOnClickListener(this);
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
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.communityGoToChatBtn):
                startActivity(new Intent(this, ChatList.class));
        }
    }
}