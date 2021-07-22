package com.example.lazyworkout.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lazyworkout.R;

public class LockScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;
    private Button goToOverviewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        initViews();
    }

    private void initViews() {
        textView = findViewById(R.id.lockScreenText);
        goToOverviewBtn = findViewById(R.id.lockScreenBtn);

        goToOverviewBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case (R.id.lockScreenBtn):
                startActivity(new Intent(this, OverviewActivity.class));
                break;

            default:
                break;
        }
    }
}