package com.example.lazyworkout.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SensorRestarterBroadcas";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");

//        String service = intent.getStringExtra("service");
//
//        if (service.equals("stepcounter")){
//            context.startService(new Intent(context, StepCountingService.class));
//        } else if (service.equals("locking")) {
//            context.startService(new Intent(context, LockService.class));
//        }

        Log.d(TAG, "onReceive called");

        // We are starting MyService via a worker and not directly because since Android 7
        // (but officially since Lollipop!), any process called by a BroadcastReceiver
        // (only manifest-declared receiver) is run at low priority and hence eventually
        // killed by Android.
        WorkManager workManager = WorkManager.getInstance(context);
        OneTimeWorkRequest startServiceRequest = new OneTimeWorkRequest.Builder(TrackingWorker.class)
                .build();
        workManager.enqueue(startServiceRequest);

    }
}