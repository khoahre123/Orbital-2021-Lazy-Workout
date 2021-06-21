package com.example.lazyworkout.service;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.lazyworkout.R;

import java.util.Timer;
import java.util.TimerTask;

public class StepCountingService extends Service implements SensorEventListener {

    private static final String TAG = "StepCountingService";
    public static final String BROADCAST_ACTION = "com.example.lazyworkout.mybroadcast";
    private static final int PHYSICAL_ACTIVITY = 1;
    private static final int MICROSECONDS_IN_ONE_MINUTE = 60000000;
    public static int DEFAULT_SENSOR = Sensor.TYPE_STEP_COUNTER;
    public static boolean IS_STEP_COUNTER = true;



    SensorManager sensorManager;
    Sensor stepCounterSensor;

    private final Handler handler = new Handler(Looper.myLooper());

    float since_boot;
    int sensor_triggered = 0;
    int counter = 0;

    private Timer timer;
    private TimerTask timerTask;

    NotificationManager notificationManager;

    Intent intent;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();

        since_boot = PreferenceManager.getDefaultSharedPreferences(this)
                .getFloat("since_boot", 0);
        Log.d("since_boot", "onStart" + String.valueOf(since_boot));
        intent = new Intent(BROADCAST_ACTION);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL, (int) (5 * MICROSECONDS_IN_ONE_MINUTE));

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

        PreferenceManager.getDefaultSharedPreferences(this).edit().putFloat("since_boot", since_boot).apply();
        Log.d("since_boot", "onDestroy" + String.valueOf(since_boot));
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(intent);
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

        PreferenceManager.getDefaultSharedPreferences(this).edit().putFloat("since_boot", since_boot).apply();
        Log.d("since_boot", "onRemoved" + String.valueOf(since_boot));
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

//        since_boot = computeSteps(event);
        since_boot = computeSteps(event);

        Log.d("sensorChanged", String.valueOf(since_boot));
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
            sensor_triggered++;
            float fakeSteps = (float) (since_boot + 0.001 * Math.sqrt(X*X + Y*Y + Z*Z));
            Log.d("fakeSteps", String.valueOf(fakeSteps));
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
        Log.d(TAG, "Data to Activity");
        Log.d(TAG, String.valueOf(since_boot));

        intent.putExtra("since_boot", since_boot);
        sendBroadcast(intent);
    }
}

