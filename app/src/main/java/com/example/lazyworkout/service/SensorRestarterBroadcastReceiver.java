package com.example.lazyworkout.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");

        String service = intent.getStringExtra("service");

        if (service.equals("stepcounter")){
            context.startService(new Intent(context, StepCountingService.class));
        }
        else if (service.equals("locking")) {
            context.startService(new Intent(context, LockService.class));
        }

    }
}