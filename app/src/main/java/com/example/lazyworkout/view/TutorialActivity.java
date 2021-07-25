package com.example.lazyworkout.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.lazyworkout.R;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TutorialActivity";

    private Button btn;
    private ImageView lazy, notLazy;
    private ConstraintLayout imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        initViews();
    }

    private void initViews() {

        imgView = findViewById(R.id.tutorialLazyImg);

        lazy = findViewById(R.id.tutorialLazy);
        notLazy = findViewById(R.id.tutorialNotLazy);

        btn = findViewById(R.id.tutorialBtn);
        btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.tutorialBtn):
                startActivity(new Intent(this, InitialSettingGoalActivity.class));
        }
    }
}