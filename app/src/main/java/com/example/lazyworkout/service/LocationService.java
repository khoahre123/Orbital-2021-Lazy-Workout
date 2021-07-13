package com.example.lazyworkout.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lazyworkout.util.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service implements LocationListener  {
    private Timer timer;
    private TimerTask timerTask;
    private int counter = 0;
    private double latitude;
    private double longitude;
    private static long lastSaveTime = 0;
    private LocationManager locationManager;
    private String TAG = "Location Service";
    private String uid = FirebaseAuth.getInstance().getUid();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 ,0, this);
        startTimer();
        String lastTime = ((Long) lastSaveTime).toString();
        Log.d(TAG, lastTime);
        return START_STICKY;
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        updateIfNecessary();
    }

    public boolean updateIfNecessary() {
        if (lastSaveTime == 0 || System.currentTimeMillis() > lastSaveTime + Constant.SAVE_OFFSET_TIME) {
            Map<String, Double> object = new HashMap<>();
            object.put("longitude", longitude);
            object.put("latitude", latitude);
            FirebaseFirestore.getInstance().collection("users").document(uid).set(object, SetOptions.merge());
            lastSaveTime = System.currentTimeMillis();
        }
        return true;
    }
}
