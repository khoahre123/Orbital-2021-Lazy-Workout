package com.example.lazyworkout.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MissionBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent("com.example.lazyworkout.achievement");
        newIntent.putExtra("command", "congrats");
        context.startActivity(newIntent);
    }
}
