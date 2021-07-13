package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.lazyworkout.R;
import com.example.lazyworkout.service.LocationService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CommunityActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private Button goToChat;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        initViews();
        startLocationService();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(99)
    private void startLocationService() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
        } else {
            EasyPermissions.requestPermissions(this, "Your location is needed for better community engagement", 99, perms);
        }
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