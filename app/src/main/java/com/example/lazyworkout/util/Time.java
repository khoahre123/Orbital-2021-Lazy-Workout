package com.example.lazyworkout.util;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;

public class Time {

    public static final int ONE_DAY_MILLIS = 86400000;
    public static final int ONE_MINUTE_MILLIS = 60000;
    public static final int ONE_HOUR_MINUTE = 60;

    public static long getToday() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long getTomorrow() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 1);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, 1);
        return c.getTimeInMillis();
    }

    public static Timestamp convertMillis(long time) {
        Date date = new Date(time);
        return new Timestamp(date);
    }
    
    public static int convertMinute(int hour, int minute) {
        return hour * ONE_HOUR_MINUTE + minute;
    }

    public static boolean isLockTime(long currentTime, int lockTimeMinute) {
        long startLockTime = Time.getToday() + lockTimeMinute * ONE_MINUTE_MILLIS;
        long endLockTime = Time.getToday() + ONE_DAY_MILLIS;
        Log.d("LockService", "start lock time = " + startLockTime / ONE_MINUTE_MILLIS);
        Log.d("LockService", "current time = " + System.currentTimeMillis() / ONE_MINUTE_MILLIS);
        Log.d("LockService", "end lock time = " + endLockTime / ONE_MINUTE_MILLIS);

        return (currentTime >= startLockTime) && (currentTime <= endLockTime);
    }
}
