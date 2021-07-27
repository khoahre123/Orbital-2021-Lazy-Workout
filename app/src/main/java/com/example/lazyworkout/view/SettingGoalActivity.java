package com.example.lazyworkout.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.lazyworkout.R;
import com.example.lazyworkout.util.Constant;
import com.example.lazyworkout.util.Database;
import com.example.lazyworkout.util.Util;
import com.google.firebase.auth.FirebaseAuth;

public class SettingGoalActivity extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView setGoalInput;
    private Button btn;

    private String uid = FirebaseAuth.getInstance().getUid();

    Database db = new Database();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_goal);

        initViews();
    }

    private void initViews() {
        setGoalInput = findViewById(R.id.settingGoalInput);
        btn = findViewById(R.id.settingGoalBtn);

        btn.setOnClickListener(this);

        float currentGoal = getSharedPreferences(uid, Context.MODE_PRIVATE)
                .getFloat("goal", Constant.DEFAULT_GOAL);
        setGoalInput.setText(currentGoal + " km");

        ArrayAdapter<String> goalArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Constant.GOAL_SETTING);
        setGoalInput.setAdapter(goalArray);
        setGoalInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                float goal = Util.getGoal(item);
                db.updateGoal(goal);
                getSharedPreferences(uid, Context.MODE_PRIVATE).edit()
                        .putFloat("goal", (float) goal).commit();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case (R.id.settingGoalBtn):
                finish();
                break;

            default:
                break;

        }
    }
}
