package com.example.lazyworkout.service;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lazyworkout.model.User;
import com.example.lazyworkout.util.Database;
import com.example.lazyworkout.view.OverviewActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class CheckAppLaunchThread extends Thread {

    private static final String TAG = "LockService";

    private Context context;
    private Handler handler;
    private ActivityManager actMan;
    private int timer = 100;
    public static String lastUnlocked;

    Database db = new Database();

// private String lastUnlocked;

    public CheckAppLaunchThread(Handler mainHandler, Context context) {
        this.context = context;
        this.handler = mainHandler;
        actMan = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        this.setPriority(MAX_PRIORITY);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Log.d(TAG, "run check app thread");

        context.startService(new Intent(context, LockService.class));
        Looper.prepare();
        String prevTasks;
        String recentTasks = "";

        prevTasks = recentTasks;
        Log.d("Thread", "Inside Thread");
        while (true) {
            try {
                String topPackageName = "";
                if (Build.VERSION.SDK_INT >= 21) {
                    UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                    long time = System.currentTimeMillis();
                    // We get usage stats for the last 10 seconds
                    List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 5, time);
                    if (stats != null) {
                        SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                        for (UsageStats usageStats : stats) {
                            mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                        }
                        if (mySortedMap != null && !mySortedMap.isEmpty()) {
                            topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                        }
                    }
                } else {
                    topPackageName = actMan.getRunningAppProcesses().get(0).processName;
                }
                recentTasks = topPackageName;
                Log.d(TAG, "recent task = " + recentTasks);
                Log.d(TAG, "current context = " + context);
                Thread.sleep(timer);
                if (recentTasks.length() == 0 || recentTasks.equals(
                        prevTasks)) {
                } else {
                    DocumentReference userRef = db.fStore.collection(db.DB_NAME).document(db.getID());
                    String finalRecentTasks = recentTasks;
                    userRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                User user = document.toObject(User.class);
                                Map<String, Object> map = document.getData();

                                List<String> lockedAppsList = user.getLockedApps();

                                Log.d(TAG, "lockedApplist = " + lockedAppsList.toString());

                                if (lockedAppsList.contains(finalRecentTasks)) {
                                    Log.d(TAG, "in locked list " + finalRecentTasks);

                                    handler.post(new ReturnHomeScreen(context, finalRecentTasks));

                                }

                            } else {
                                Log.d(TAG, "no such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            prevTasks = recentTasks;

        }

    }


    class ToastRunnable implements Runnable {

        String message;

        public ToastRunnable(String text) {
            message = text;
        }

        @Override
        public void run() {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        }
    }


    class ReturnHomeScreen implements Runnable {
        private static final String TAG = "LockService";

        private Context mContext;
        private String pkgName;

        public ReturnHomeScreen(Context mContext, String pkgName) {
            this.mContext = mContext;
            this.pkgName = pkgName;
        }

        @Override
        public void run() {

            Log.d(TAG, "run return home screen");
            Log.d(TAG, "current context = " + mContext.toString());

            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            this.mContext.startActivity(startMain);


        }

    }
}
