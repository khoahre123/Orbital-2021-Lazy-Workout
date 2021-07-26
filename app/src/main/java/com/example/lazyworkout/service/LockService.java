package com.example.lazyworkout.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.lazyworkout.R;
import com.example.lazyworkout.model.User;
import com.example.lazyworkout.util.Database;
import com.example.lazyworkout.util.Time;
import com.example.lazyworkout.view.LockScreenActivity;
import com.example.lazyworkout.view.OverviewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class LockService extends Service {

    private static final String TAG = "LockService";

    public static boolean isServiceRunning;
    private String CHANNEL_ID = "NOTIFICATION_CHANNEL_LOCKING";

    private final Handler handler = new Handler(Looper.myLooper());
    Context context;
    Intent intent;

    Database db = new Database();
    private FirebaseAuth fAuth = db.fAuth;

    UsageStatsManager usageStatsManager;
    ActivityManager activityManager;

    String prevTasks;
    String recentTasks = "";

    private Timer timer;
    private TimerTask timerTask;
    private int counter = 0;


    public Context getContext() {
        return getApplicationContext();
    }

    public LockService() {
        Log.d(TAG, "constructor called");
        isServiceRunning = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        context = getContext();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            usageStatsManager = (UsageStatsManager) this.context.getSystemService(Context.USAGE_STATS_SERVICE);
        } else {
            activityManager = (ActivityManager) this.context.getSystemService(Context.ACTIVITY_SERVICE);
        }

        createNotificationChannel();
        isServiceRunning = true;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        handler.removeCallbacks(updateBroadcastData);

        if (fAuth.getCurrentUser() != null) {
            handler.post(updateBroadcastData);
            startTimer();
        }

        Intent notificationIntent = new Intent(this, OverviewActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service is Running")
                .setContentText("Listening for Screen Off/On events")
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
        super.onDestroy();

        if (fAuth.getCurrentUser() != null) {
            isServiceRunning = false;
            stopForeground(true);

            Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
            sendBroadcast(broadcastIntent);
        }

        stopTimerTask();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onRemoved");
        super.onTaskRemoved(rootIntent);

        if (fAuth.getCurrentUser() != null) {
            Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
            broadcastIntent.putExtra("service", "lockapp");
            sendBroadcast(broadcastIntent);
        }


        stopTimerTask();
    }

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

    public String getForegroundApp() {
        String currentApp = "NULL";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            List<ActivityManager.RunningAppProcessInfo> tasks = activityManager.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        return currentApp;
    }

    public boolean showLockScreen(){
        Log.d(TAG, "show home screen");

        Intent intent = new Intent(this, LockScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);

        return true;
    }

    public void checkLockedApps(String recentTasks) {

        Log.d(TAG, "is running");
        DocumentReference userRef = db.fStore.collection(db.DB_NAME).document(db.getID());
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    User user = document.toObject(User.class);
                    Map<String, Object> map = document.getData();

                    List<String> lockedAppsList = user.getLockedApps();

                    Log.d(TAG, "lockedApplist = " + lockedAppsList.toString());

                    if (lockedAppsList.contains(recentTasks)) {
                        Log.d(TAG, "in locked list " + recentTasks);

                        //TODO: time picker
                        if (!user.finishDailyGoal(Time.getToday()) && Time.isLockTime(System.currentTimeMillis(), user.getLockTimeMinute())) {
                            Log.d(TAG, "lock screen!!");
                            showLockScreen();
                        }
                    }

                } else {
                    Log.d(TAG, "no such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
            // Only allow the repeating timer while service is running (once service is stopped the flag state will change and the code inside the conditional statement here will not execute).
            // Call the method that broadcasts the data to the Activity..
            prevTasks = recentTasks;
            if (fAuth.getCurrentUser() == null) {
                stopSelf();
            } else {
                recentTasks = getForegroundApp();
                checkLockedApps(recentTasks);
                // Call "handler.postDelayed" again, after a specified delay.
                handler.postDelayed(this, 1000);
            }


        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}