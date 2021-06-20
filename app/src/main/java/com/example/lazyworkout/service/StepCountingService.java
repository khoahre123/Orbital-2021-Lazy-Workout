package com.example.lazyworkout.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;

public class StepCountingService extends Service implements SensorEventListener {

    private static final String TAG = "StepCountingService";
    //TODO: String BROADCAST_ACTION

    SensorManager sensorManager;
    Sensor stepCounterSensor;

    int currentStepsDetected;

    int stepCounter;
    int newStepCounter;
    int counter = 0;

    NotificationManager notificationManager;
    private final Handler handler = new Handler();
    Intent intent;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}