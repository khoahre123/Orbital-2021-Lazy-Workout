package com.example.lazyworkout.util;

import java.util.Calendar;

public class Time {

    public static final long MIDNIGHT = Time.midnight();

    public static long midnight() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
}
