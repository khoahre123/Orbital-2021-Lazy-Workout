package com.example.lazyworkout.view;

import androidx.appcompat.app.AppCompatActivity;

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

public class SettingStepActivity extends AppCompatActivity {

    private AutoCompleteTextView setStepInput;
    private Button btn;

    Database db = new Database();
    private String uid = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_step);

        initViews();
    }

    private void initViews() {
        setStepInput = findViewById(R.id.settingStepInput);  //TODO: revamp set step input
        btn = findViewById(R.id.settingStepBtn);

        float currentStep = getSharedPreferences(uid, Context.MODE_PRIVATE)
                .getFloat("step_size", Constant.DEFAULT_STEP_SIZE);
        setStepInput.setText((int) (currentStep * 100000) + " cm");

        ArrayAdapter<String> stepArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Constant.STEP_SETTING);
        setStepInput.setAdapter(stepArray);
        setStepInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                float stepSize = Util.getStepsize(item);
                db.updateStepsize(stepSize);
                getSharedPreferences(uid, Context.MODE_PRIVATE).edit()
                        .putFloat("step_size", (float) stepSize).commit();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingStepActivity.this, SettingActivity.class));
            }
        });
    }


}