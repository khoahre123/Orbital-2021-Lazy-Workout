package com.example.lazyworkout.pedometer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lazyworkout.BuildConfig;
import com.example.lazyworkout.R;
import com.example.lazyworkout.pedometer.util.API26Wrapper;
import com.example.lazyworkout.pedometer.util.Logger;
import com.example.lazyworkout.pedometer.util.Util;
import com.example.lazyworkout.view.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.util.Locale;


public class ProfileActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "ProfileActivity";
    private static final int DEFAULT_GOAL = 200;
    public static final float DEFAULT_STEP_SIZE = 50;

//    private Button logoutBtn;
//    private TextView stepCount;
//
//    private int steps;
//    private double fakeSteps;

    private TextView stepsView, totalView, averageView, goalView, todayView;
//    private PieModel sliceGoal, sliceCurrent;  //PIE LIBRARY
//    private PieChart pg;

    private int todayOffset, total_start, goal, since_boot, total_days;
    public final static NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
    private boolean showSteps = true;

    FirebaseAuth mAuth;

    @Override
    public  void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        initViews();
        setContentView(R.layout.activity_profile);
//        if (Build.VERSION.SDK_INT >= 26) {
//            API26Wrapper.startForegroundService(this,
//                    new Intent(this, SensorListener.class));
//        } else {
//            this.startService(new Intent(this, SensorListener.class));
//        }
        startService(new Intent(this, SensorListener.class));
        initViews();
        setContentView(R.layout.activity_profile);

//        startService(new Intent(this, SensorListener.class)); PREVIOUS

    }


    public void initViews() {
//        final View v = inflater.inflate(R.layout.activity_profile, null);
        stepsView = (TextView) findViewById(R.id.steps);
        totalView = (TextView) findViewById(R.id.total);
        averageView = (TextView) findViewById(R.id.average);
        goalView = (TextView) findViewById(R.id.goal);
        todayView = (TextView) findViewById(R.id.stepsToday);

//        goalView.setText(DEFAULT_GOAL); //TODO: Fragment_Settings.DEFAULT_GOAL



//        pg = (PieChart) v.findViewById(R.id.graph);

        // slice for the steps taken today
//        sliceCurrent = new PieModel("", 0, Color.parseColor("#99CC00"));
//        pg.addPieSlice(sliceCurrent);

        // slice for the "missing" steps until reaching the goal
//        sliceGoal = new PieModel("", Fragment_Settings.DEFAULT_GOAL, Color.parseColor("#CC0000"));
//        pg.addPieSlice(sliceGoal);

//        pg.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//                showSteps = !showSteps;
//                stepsDistanceChanged();
//            }
//        });
//
//        pg.setDrawValueInPie(false);
//        pg.setUsePieRotation(true);
//        pg.startAnimation();
//        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
//        this.getActionBar().setDisplayHomeAsUpEnabled(false);

        Database db = Database.getInstance(this);

        if (BuildConfig.DEBUG) db.logState();
        // read todays offset
        todayOffset = db.getSteps(Util.getToday());

        SharedPreferences prefs =
                this.getSharedPreferences("pedometer", Context.MODE_PRIVATE);

        goal = prefs.getInt("goal", DEFAULT_GOAL ); //TODO: Fragment_Settings.DEFAULT_GOAL
        since_boot = db.getCurrentSteps();
        int pauseDifference = since_boot - prefs.getInt("pauseCount", since_boot);

        // register a sensorlistener to live update the UI if a step is taken
        SensorManager sm = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (sensor == null) {
            new AlertDialog.Builder(this).setTitle(R.string.no_sensor)
                    .setMessage(R.string.no_sensor_explain)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(final DialogInterface dialogInterface) {
                            finish();
                        }
                    }).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else {
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, 0);
        }

        since_boot -= pauseDifference;

        total_start = db.getTotalWithoutToday();
        total_days = db.getDays();

        db.close();

//        stepsDistanceChanged();
    }

    /**
     * Call this method if the Fragment should update the "steps"/"km" text in
     * the pie graph as well as the pie and the bars graphs.
     */
//    private void stepsDistanceChanged() {
//        if (showSteps) {
//            ((TextView) getView().findViewById(R.id.unit)).setText(getString(R.string.steps));
//        } else {
//            String unit = getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE)
//                    .getString("stepsize_unit", Fragment_Settings.DEFAULT_STEP_UNIT);
//            if (unit.equals("cm")) {
//                unit = "km";
//            } else {
//                unit = "mi";
//            }
//            ((TextView) getView().findViewById(R.id.unit)).setText(unit);
//        }
//
//        updatePie();
//        updateBars();
//    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            SensorManager sm =
                    (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
            sm.unregisterListener(this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Logger.log(e);
        }
        Database db = Database.getInstance(this);
        db.saveCurrentSteps(since_boot);
        db.close();
    }

//    @Override
//    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
//        inflater.inflate(R.menu.main, menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(final MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_split_count:
//                Dialog_Split.getDialog(getActivity(),
//                        total_start + Math.max(todayOffset + since_boot, 0)).show();
//                return true;
//            default:
//                return ((Activity_Main) getActivity()).optionsItemSelected(item);
//        }
//    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        // won't happen
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (BuildConfig.DEBUG) Logger.log(
                "UI - sensorChanged | todayOffset: " + todayOffset + " since boot: " +
                        event.values[0]);
        if (event.values[0] > Integer.MAX_VALUE || event.values[0] == 0) {
            return;
        }
        if (todayOffset == Integer.MIN_VALUE) {
            // no values for today
            // we dont know when the reboot was, so set todays steps to 0 by
            // initializing them with -STEPS_SINCE_BOOT
            todayOffset = -(int) event.values[0];
            Database db = Database.getInstance(this);
            db.insertNewDay(Util.getToday(), (int) event.values[0]);
            db.close();
        }
        since_boot = (int) event.values[0];
        updatePie();
    }

    private void updatePie() {
        if (BuildConfig.DEBUG) Logger.log("UI - update steps: " + since_boot);
        // todayOffset might still be Integer.MIN_VALUE on first start
        int steps_today = Math.max(todayOffset + since_boot, 0);

        todayView.setText(String.valueOf(steps_today));

//        if (goal - steps_today > 0) {
//            // goal not reached yet
////            if (pg.getData().size() == 1) {
////                // can happen if the goal value was changed: old goal value was
////                // reached but now there are some steps missing for the new goal
////                pg.addPieSlice(sliceGoal);
////            }
//            sliceGoal.setValue(goal - steps_today);
//        } else {
//            // goal reached
//            pg.clearChart();
//            pg.addPieSlice(sliceCurrent);
//        }
//        pg.update();

        if (showSteps) {
            stepsView.setText(formatter.format(steps_today));
            totalView.setText(formatter.format(total_start + steps_today));
            averageView.setText(formatter.format((total_start + steps_today) / total_days));

//            stepsView.setText(String.valueOf(steps_today));
//            totalView.setText(String.valueOf(total_start + steps_today));
//            averageView.setText(String.valueOf((total_start + steps_today) / total_days));

        }
//        } else {
//            // update only every 10 steps when displaying distance
//            SharedPreferences prefs =
//                    getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);
//            float stepsize = prefs.getFloat("stepsize_value", DEFAULT_STEP_SIZE); //TODO: Fragment_Settings.DEFAULT_STEP_SIZE
//            float distance_today = steps_today * stepsize;
//            float distance_total = (total_start + steps_today) * stepsize;
//            if (prefs.getString("stepsize_unit", Fragment_Settings.DEFAULT_STEP_UNIT)
//                    .equals("cm")) {
//                distance_today /= 100000;
//                distance_total /= 100000;
//            } else {
//                distance_today /= 5280;
//                distance_total /= 5280;
//            }
//            stepsView.setText(formatter.format(distance_today));
//            totalView.setText(formatter.format(distance_total));
//            averageView.setText(formatter.format(distance_total / total_days));
//        }
    }









}






//    private void initViews() {
//
//        logoutBtn = findViewById(R.id.button);
//
//        stepCount = findViewById(R.id.profileStepCount);
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case (R.id.button):
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    @Override
//    protected void onResume() {
//
//        Log.d(TAG, "On Resume");
//        super.onResume();
//        SharedPreferences prefs = getSharedPreferences("pedometer", Context.MODE_PRIVATE);
//        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//
//        if (sensor == null) {
//            Toast.makeText(this, "Sensor not found!", Toast.LENGTH_SHORT).show();
//        } else {
//            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, 0);
//        }
//
//        fakeSteps = (double) prefs.getFloat("fakeSteps", 0);
////        if (fakeSteps == null) {
////            Log.d(TAG,null);
////        }
//
//        Log.d(TAG, "FakeSteps" + String.valueOf(fakeSteps));
//
//    }
//
//    @Override
//    protected void onPause() {
//        Log.d(TAG, "On Pause");
//        super.onPause();
//        try {
//            SensorManager sm =
//                    (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//            sm.unregisterListener(this);
//        } catch (Exception e) {
//            Log.d("com.example.lazyworkout", e.getMessage());
//        }
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        //doing nothing
//        fakeSteps = event.values[0];
////        stepCount.setText(String.valueOf(event.values[0]));
//        Log.d(TAG, "Steps" + String.valueOf(fakeSteps));
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        //doing nothing
//    }