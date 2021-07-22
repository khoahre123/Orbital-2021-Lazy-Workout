package com.example.lazyworkout.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class LockService extends Service {

    private static final String TAG = "LockService";
    public int counter = 0;
    private Timer timer;
    private TimerTask timerTask;

    private Handler handler;
    private Context context;
    CheckAppLaunchThread launchChecker;

    public LockService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        handler = new Handler(Looper.myLooper());
        context = getBaseContext();
        launchChecker = new CheckAppLaunchThread(handler, context);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        handler.removeCallbacks(updateBroadcastData);

        handler.post(updateBroadcastData);

        startTimer();

        while (true) {
            if (!launchChecker.isAlive())
                launchChecker.start();
            return START_STICKY;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved");
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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

    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
            // Only allow the repeating timer while service is running (once service is stopped the flag state will change and the code inside the conditional statement here will not execute).
            // Call the method that broadcasts the data to the Activity..\
            Handler handler = new Handler(Looper.myLooper());
            launchChecker.setContext(getApplication().getBaseContext());
            Log.d(TAG, "update context = " + getApplication().getBaseContext());
            // Call "handler.postDelayed" again, after a specified delay.
            handler.postDelayed(this, 1000);

        }
    };

//    public String getForegroundApp() {
//        String currentApp = "NULL";
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
//            long time = System.currentTimeMillis();
//            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
//            if (appList != null && appList.size() > 0) {
//                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
//                for (UsageStats usageStats : appList) {
//                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
//                }
//                if (mySortedMap != null && !mySortedMap.isEmpty()) {
//                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
//                }
//            }
//        } else {
//            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
//            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
//            currentApp = tasks.get(0).processName;
//        }
//
//        return currentApp;
//    }
//
//    public boolean showHomeScreen(){
//        Intent startMain = new Intent(Intent.ACTION_MAIN);
//        startMain.addCategory(Intent.CATEGORY_HOME);
//        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(startMain);
//        return true;
//    }
}