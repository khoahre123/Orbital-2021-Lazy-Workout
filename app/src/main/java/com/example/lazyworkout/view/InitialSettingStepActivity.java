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

public class InitialSettingStepActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backArrow;
    private TextView skip;
    private AutoCompleteTextView setStepInput;
    private String[] foot = {"40 cm", "45 cm", "50 cm", "55 cm", "60 cm", "65 cm",
            "70 cm", "75 cm", "80 cm", "85 cm", "90 cm"};
    private Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setting_step);

        initViews();
    }

    private void initViews() {
        backArrow = findViewById(R.id.initialStepBackArrow);
        skip = findViewById(R.id.initialStepSkip);
        setStepInput = findViewById(R.id.initialStepInput);  //TODO: revamp set step input
        btn = findViewById(R.id.initialStepBtn);

        backArrow.setOnClickListener(this);
        skip.setOnClickListener(this);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case (R.id.initialStepBackArrow):
                startActivity(new Intent(this, InitialSettingGoalActivity.class));
                break;

            case (R.id.initialStepSkip):
                startActivity(new Intent(this, TimePickerActivity.class));
                break;

            case (R.id.initialStepBtn):
                break;//TODO:

            default:
                break;
        }
    }
}