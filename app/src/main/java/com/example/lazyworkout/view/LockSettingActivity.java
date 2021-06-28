package com.example.lazyworkout.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lazyworkout.R;
import com.example.lazyworkout.util.Constant;

public class LockSettingActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "LockSettingActivity";

    private Button goToProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setting);

//        if(ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
//            //ask for permission
//            Log.d(TAG, "ask permission");
//            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, Constant.PHYSICAL_ACTIVITY);
//        }

        initViews();
    }

    private void initViews() {
        goToProfile = findViewById(R.id.lockSettingBtn);

        goToProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.lockSettingBtn):
                startActivity(new Intent(this, OverviewActivity.class));
        }
    }
}