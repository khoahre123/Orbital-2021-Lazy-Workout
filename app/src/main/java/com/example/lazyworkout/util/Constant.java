package com.example.lazyworkout.util;

import android.app.AlarmManager;
import android.hardware.Sensor;

public class Constant {

    public static final float DEFAULT_GOAL = (float) 4.0;
    public static final float DEFAULT_STEP_SIZE = (float) 0.00075;
    public static final int DEFAULT_SENSOR = Sensor.TYPE_STEP_COUNTER;
    public static final int PHYSICAL_ACTIVITY = 1;
    public static final int USAGE_STATS_SERVICE = 2;
    public static final boolean LOCK_ACTIVATED = false;
    public static final int LOCK_TIME = 1080; //18h
    public static final String[] GOAL_SETTING = {"4.0 km", "4.5 km", "5.0 km", "5.5 km", "6.0 km", "6.5 km", "7.0 km", "7.5 km",
            "8.0 km", "8.5 km", "9.0 km", "9.5 km", "10.0 km"};
    public static String[] STEP_SETTING = {"40 cm", "45 cm", "50 cm", "55 cm", "60 cm", "65 cm",
            "70 cm", "75 cm", "80 cm", "85 cm", "90 cm"};

    //intervals between database updates
    public final static long SAVE_OFFSET_TIME = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    public final static float SAVE_OFFSET_DISTANCES = (float) 0.2;


}
