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

        return (currentTime >= startLockTime) && (currentTime <= endLockTime);
    }

    public static int getMinute(int totalMinute) {
        return totalMinute % 60;
    }

    public static int getHour(int totalMinute) {
        int hour = (totalMinute / 60) <= 12 ? (totalMinute / 60) : (totalMinute / 60 - 12);
        return hour;
    }

    public static String getTime(int totalMinute) {
        String str = String.format("%02d", Time.getHour(totalMinute)) + ":" +
                String.format("%02d", Time.getMinute(totalMinute));
        String time = (totalMinute / 60) <= 12 ? str + " AM" : str + " PM";
        return time;
    }
}
