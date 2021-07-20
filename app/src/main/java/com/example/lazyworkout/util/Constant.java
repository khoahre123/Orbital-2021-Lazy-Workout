package com.example.lazyworkout.util;

import android.app.AlarmManager;
import android.hardware.Sensor;

public class Constant {

    public static final float DEFAULT_GOAL = (float) 4.0;
    public static final float DEFAULT_STEP_SIZE = (float) 0.00075;
    public static final int DEFAULT_SENSOR = Sensor.TYPE_STEP_COUNTER;
    public static final int PHYSICAL_ACTIVITY = 1;
    public static final int USAGE_STATS_SERVICE = 2;

    //intervals between database updates
    public final static long SAVE_OFFSET_TIME = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    public final static float SAVE_OFFSET_DISTANCES = (float) 0.2;


}
