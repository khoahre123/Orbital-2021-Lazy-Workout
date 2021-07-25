package com.example.lazyworkout.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lazyworkout.R;

public class InitialSettingGoalActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backArrow;
    private TextView skip;
    private AutoCompleteTextView setGoalInput;
    private String[] goal = {"4.0 km", "4.5 km", "5.0 km", "5.5 km", "6.0 km", "6.5 km", "7.0 km", "7.5 km",
            "8.0 km", "8.5 km", "9.0 km", "9.5 km", "10.0 km"};
    private Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setting_goal);

        initViews();
    }

    private void initViews() {
        backArrow = findViewById(R.id.initialGoalBackArrow);
        skip = findViewById(R.id.initialGoalSkip);
        setGoalInput = findViewById(R.id.initialGoalInput);  //TODO: revamp set goal input
        btn = findViewById(R.id.initialGoalBtn);

        backArrow.setOnClickListener(this);
        skip.setOnClickListener(this);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case (R.id.initialGoalBackArrow):
                startActivity(new Intent(this, TutorialActivity.class));
                break;

            case (R.id.initialGoalSkip):
                startActivity(new Intent(this, InitialSettingStepActivity.class));
                break;

            case (R.id.initialGoalBtn):
                break;//TODO:

            default:
                break;
        }
    }
}