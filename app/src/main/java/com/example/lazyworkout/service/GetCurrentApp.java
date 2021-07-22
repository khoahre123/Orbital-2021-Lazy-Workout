package com.example.lazyworkout.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class GetCurrentApp extends Service {

    private static final String TAG = "GetCurrentApp";


    CheckAppLaunchThread currentThread;
    Handler handler;
    Context context;

//    public GetCurrentApp(CheckAppLaunchThread thread) {
//        this.currentThread = thread;
//    }

    public Context getContext() {
        return getBaseContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        handler = new Handler(getMainLooper());
        context = getContext();

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(startMain);

        Log.d(TAG, "current context = " + context);
//        currentThread.setContext(context);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}