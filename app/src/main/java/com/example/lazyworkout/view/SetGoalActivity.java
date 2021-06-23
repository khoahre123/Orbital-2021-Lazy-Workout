package com.example.lazyworkout.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

import com.example.lazyworkout.R;
import com.example.lazyworkout.pedometer.ProfileActivity;

public class SetGoalActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "SetGoalActivity";

    private AutoCompleteTextView setGoalInput, setStepInput;
    private Button confirmButton;
    private String[] goal = {"4.0 km", "4.5 km", "5.0 km", "5.5 km", "6.0 km", "6.5 km", "7.0 km", "7.5 km",
    "8.0 km", "8.5 km", "9.0 km", "9.5 km", "10.0 km"};
    private Integer[] foot = {30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45};
    private String goalSelected;
    private Integer footSelected;

    //TODO Apply a default value that user selected
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);
        initView();

    }

    public void initView() {
        setGoalInput = findViewById(R.id.setGoalInput);
        setStepInput = findViewById(R.id.setStepInput);
        confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this);
        ArrayAdapter<String> goalArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, goal);
        setGoalInput.setAdapter(goalArray);
        setGoalInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                goalSelected = item;
            }
        });
        ArrayAdapter<Integer> stepArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foot);
        setStepInput.setAdapter(stepArray);
        setStepInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer item = (Integer) parent.getItemAtPosition(position);
                footSelected = item;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case (R.id.confirmButton):
                if (goalSelected == null & footSelected == null) {
                    startActivity(new Intent(this, OverviewActivity.class));
                    break;
                }
                Bundle bundle = new Bundle();
                if (goalSelected != null) {
                    bundle.putString("goalSelected", goalSelected);
                }
                if (footSelected != null) {
                    bundle.putInt("footSelected", footSelected);
                }
                Intent intent = new Intent(this, OverviewActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
}
