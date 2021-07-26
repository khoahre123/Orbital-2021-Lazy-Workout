    package com.example.lazyworkout.service;


import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
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
import android.os.Build;
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
import com.example.lazyworkout.util.Util;
import com.example.lazyworkout.view.OverviewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class StepCountingService extends Service implements SensorEventListener, FirebaseAuth.AuthStateListener {

    private static final String TAG = "StepCountingService";
    public static final String BROADCAST_ACTION = "com.example.lazyworkout.mybroadcast";
    public static final String ACHIEVEMENT_ACTION = "com.example.lazyworkout.achievement";
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

    //save statistics when signing out
    public static float todayDistances;
    public static float sinceBoot;

    private float steps;
    private float stepSize;

    private Timer timer;
    private TimerTask timerTask;
    private int counter = 0;

    private Database db = new Database();
    private FirebaseAuth fAuth = db.fAuth;

    NotificationManager notificationManager;
    public static boolean isServiceRunning;
    private String CHANNEL_ID = "NOTIFICATION_CHANNEL";

    Intent intent;

    public StepCountingService() {
        Log.d(TAG, "constructor called");
        isServiceRunning = false;
    }


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

        createNotificationChannel();
        isServiceRunning = true;

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

            if (fAuth.getCurrentUser() != null) {
                // call our handler with or without delay.
                handler.post(updateBroadcastData); // 0 seconds
            }


            startTimer();

        }
        Intent notificationIntent = new Intent(this, OverviewActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(Util.formatter.format(todayDistances) + " km completed today")
                .setContentText("Keep up the good work!")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setColor(getResources().getColor(R.color.pink))
                .build();

        startForeground(1, notification);
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String appName = getString(R.string.app_name);
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    appName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        Log.d(TAG, "user after destroying = " + db.fAuth.getCurrentUser());
        super.onDestroy();


        if (fAuth.getCurrentUser() != null) {
            //JUST FOR EMULATOR
            getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                    .putFloat("since_boot", sinceBoot).commit();

            getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                    .putFloat("today_distance", todayDistances).commit();

            isServiceRunning = false;
            stopForeground(true); //CAN MOVE ON DESTROY TO LATER

            Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
            sendBroadcast(broadcastIntent);
        }
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

        Log.d(TAG, "onTaskRemoved " + String.valueOf(todayDistances));
        Log.d(TAG, "onTaskRemoved saved " + String.valueOf(getSharedPreferences(db.getID(), Context.MODE_PRIVATE)
                .getFloat("today_distance", 0)));

        if (fAuth.getCurrentUser() != null) {
            getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                    .putFloat("since_boot", sinceBoot).commit();

            getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                    .putFloat("today_distance", todayDistances).commit();
            Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
            sendBroadcast(broadcastIntent);
        }
        stopTimerTask();
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (fAuth.getCurrentUser() == null) {
            this.stopSelf();
        } else {
            steps = computeSteps(event);
            sinceBoot = (float) (steps * stepSize);
            float prevPause = getSharedPreferences(db.getID(), Context.MODE_PRIVATE)
                    .getFloat("pauseCount", -1);
            Log.d(TAG, "prev pause = " + prevPause);
            if (prevPause == -1) {
                getSharedPreferences(db.getID(), Context.MODE_PRIVATE).edit()
                        .putFloat("pauseCount", sinceBoot).commit();
            }

            todayDistances = sinceBoot - getSharedPreferences(db.getID(), Context.MODE_PRIVATE)
                    .getFloat("pauseCount", 0);

            Log.d(TAG, "since boot " + sinceBoot);
            Log.d(TAG, "today distance = " + todayDistances);
            updateIfNecessary();
        }
    }

    public boolean updateIfNecessary() {

        if (sinceBoot > lastSaveDistances + Constant.SAVE_OFFSET_DISTANCES ||
                    (sinceBoot > 0 && System.currentTimeMillis() > lastSaveTime + Constant.SAVE_OFFSET_TIME)) {

            DocumentReference userRef = db.fStore.collection(db.DB_NAME).document(db.getID());
            Map<String, Object> update = new HashMap<>();
            update.put("longestDay", getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("longestDay", 0));
            update.put("currentStreak", getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("currentStreak", 0));
            update.put("longestStreak", getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("longestStreak", 0));
            userRef.update(update);
            Intent otherIntent = new Intent(BROADCAST_ACTION);
            otherIntent.putExtra("command", "update");
            sendBroadcast(otherIntent);
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

        if (fAuth.getCurrentUser() != null) {
            // start of new day
            if (distanceDtb == 0.0) {
                Log.d(TAG, "distance today = 0");

                // how current total steps differ from last save steps (from yesterday)
                Log.d(TAG, "compute pauseDiff = " + String.valueOf(todayDistances));

                // add new day to the database
                db.insertNewDay(user, Time.getToday(), todayDistances);
                Log.d(TAG, "insert new day");

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
                broadcastAchievement();
                // Call "handler.postDelayed" again, after a specified delay.
                handler.postDelayed(this, 1000);

        }
    };

    private void broadcastSensorValue() {
        Log.d(TAG, "Data to Activity distance sent = " + String.valueOf(todayDistances));

        intent.putExtra("today_distance", todayDistances);
        intent.putExtra("command", "not_update");
        sendBroadcast(intent);
    }

    private void broadcastAchievement() {
        Intent achievement_intent = new Intent(ACHIEVEMENT_ACTION);
        achievement_intent.putExtra("command", "update");
        sendBroadcast(achievement_intent);
    }

    @Override
    public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {

    }
}

