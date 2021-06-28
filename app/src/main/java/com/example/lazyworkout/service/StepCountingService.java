    package com.example.lazyworkout.service;


import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.lazyworkout.R;
import com.example.lazyworkout.model.User;
import com.example.lazyworkout.util.Constant;
import com.example.lazyworkout.util.Database;
import com.example.lazyworkout.util.Time;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class StepCountingService extends Service implements SensorEventListener, FirebaseAuth.AuthStateListener {

    private static final String TAG = "StepCountingService";
    public static final String BROADCAST_ACTION = "com.example.lazyworkout.mybroadcast";
    private static final int PHYSICAL_ACTIVITY = 1;
    private static final int MICROSECONDS_IN_ONE_MINUTE = 60000000;

    //JUST FOR EMULATOR: fake sensor for emulator
    public static int DEFAULT_SENSOR = Sensor.TYPE_STEP_COUNTER;
    public static boolean IS_STEP_COUNTER = true;

    SensorManager sensorManager;
    Sensor stepCounterSensor;

    private final Handler handler = new Handler(Looper.myLooper());

    private static float lastSaveDistances;
    private static long lastSaveTime;
    private float todayDistances;
    private float sinceBoot;
    private float steps;
    private float stepSize;

    private Timer timer;
    private TimerTask timerTask;
    private int counter = 0;

    private Database db = new Database();
    private FirebaseAuth fAuth = db.fAuth;

    NotificationManager notificationManager;

    Intent intent;


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();

        todayDistances = getSharedPreferences(db.getID(), Context.MODE_PRIVATE)
                .getFloat("today_distance", 0);

        //JUST FOR EMULATOR
        sinceBoot = getSharedPreferences(db.getID(), Context.MODE_PRIVATE)
                .getFloat("since_boot", 0);

        stepSize = getSharedPreferences(db.getID(), Context.MODE_PRIVATE)
                .getFloat("step_size", Constant.DEFAULT_STEP_SIZE);

        Log.d(TAG, "onCreate today distance = " + String.valueOf(todayDistances));
        Log.d(TAG, "onCreate previous save = " + String.valueOf(sinceBoot));
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(DEFAULT_SENSOR);
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL, (int) (10 * MICROSECONDS_IN_ONE_MINUTE));

        if (stepCounterSensor == null) {
            Log.d(TAG, "no sensor");
        } else {
            Log.d(TAG, "have sensor");
            // remove any existing callbacks to the handler
            handler.removeCallbacks(updateBroadcastData);
            // call our handler with or without delay.
            handler.post(updateBroadcastData); // 0 seconds

            startTimer();

        }
        return START_STICKY;


    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        //JUST FOR EMULATOR
        getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                .putFloat("since_boot", sinceBoot).commit();

        getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                .putFloat("today_distance", todayDistances).commit();
        Log.d(TAG, "onDestroy " + String.valueOf(todayDistances));

        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        stopTimerTask();
    }

    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onRemoved");
        super.onTaskRemoved(rootIntent);

        //JUST FOR EMULATOR
        getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                .putFloat("since_boot", sinceBoot).commit();

        getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                .putFloat("today_distance", todayDistances).commit();

        Log.d(TAG, "onTaskRemoved " + String.valueOf(todayDistances));
        Log.d(TAG, "onTaskRemoved saved " + String.valueOf(getSharedPreferences(db.getID(), Context.MODE_PRIVATE)
                .getFloat("today_distance", 0)));

        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        steps = computeSteps(event);
        sinceBoot = (float) (steps * stepSize);
        todayDistances = sinceBoot - getSharedPreferences(db.getID(), Context.MODE_PRIVATE)
                .getFloat("pauseCount", 0);
        updateIfNecessary();
    }

    public boolean updateIfNecessary() {

        if (sinceBoot > lastSaveDistances + Constant.SAVE_OFFSET_DISTANCES ||
                    (sinceBoot > 0 && System.currentTimeMillis() > lastSaveTime + Constant.SAVE_OFFSET_TIME)) {
                Log.d(TAG, "updateIfNecessary");

            DocumentReference userRef = db.fStore.collection(db.DB_NAME).document(db.getID());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        User user = document.toObject(User.class);
                        Map<String, Object> map = document.getData();
                        Log.d(TAG, "user map: " + map.toString());
                        float distance = user.getDistances(Time.getToday());

                        Log.d(TAG, "distance 1 = " + String.valueOf(distance));

                        updateUser(user, distance);

                    } else {
                        Log.d(TAG, "no such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
            return true;
        } else {
            return false;
        }

    }

    private void updateUser(User user, float distanceDtb) {

        Log.d(TAG, "distance database = " + String.valueOf(distanceDtb));

        // start of new day
        if (distanceDtb == 0.0) {
            Log.d(TAG, "distance today = 0");

            // how current total steps differ from last save steps (from yesterday)
            Log.d(TAG, "compute pauseDiff = " + String.valueOf(todayDistances));

            // add new day to the database
            db.insertNewDay(user, Time.getToday(), todayDistances);
            Log.d(TAG, "insert new day = ");

            // update pauseCount for the new day
            if (todayDistances > 0) {
                getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                        .putFloat("pauseCount", sinceBoot).commit();
            }
        }

        // steps in today only
        Log.d(TAG, "pause difference = " + String.valueOf(todayDistances));

        // update today steps
        db.saveCurrentSteps(user, todayDistances);
        Log.d(TAG, "save current steps");
        lastSaveDistances = sinceBoot;
        lastSaveTime = System.currentTimeMillis();
//                showNotification(); // update notification //TODO
    }


    private float computeSteps(SensorEvent event) {
        if (IS_STEP_COUNTER) {
            float realSteps = event.values[0];
            Log.d("realSteps", String.valueOf(event.values[0]));
            return realSteps;
        } else {
            float X = event.values[0];
            float Y = event.values[1];
            float Z = event.values[2];
            float fakeSteps = (float) (steps + Math.sqrt(X*X + Y*Y + Z*Z));
//            Log.d("fakeSteps", String.valueOf(fakeSteps));
            return fakeSteps;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //doing nothing
    }

    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
              // Only allow the repeating timer while service is running (once service is stopped the flag state will change and the code inside the conditional statement here will not execute).
                // Call the method that broadcasts the data to the Activity..
                broadcastSensorValue();
                // Call "handler.postDelayed" again, after a specified delay.
                handler.postDelayed(this, 1000);

        }
    };

    private void broadcastSensorValue() {
        Log.d(TAG, "Data to Activity distance sent = " + String.valueOf(todayDistances));

        intent.putExtra("today_distance", todayDistances);
        sendBroadcast(intent);
    }

    @Override
    public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {

    }
}
