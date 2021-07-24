package com.example.lazyworkout.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lazyworkout.util.Constant;
import com.example.lazyworkout.util.Time;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class User {

    private String uid;
    private String name;
    private float goal;
    private float stepSize;
    public TrackingRecord records;
    private double latitude;
    private double longitude;
    private String geohash;
    private List<String> lockedApps;
    private int lockTimeMinute;

    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
        this.goal = Constant.DEFAULT_GOAL;
        this.stepSize = Constant.DEFAULT_STEP_SIZE;
        this.records = new TrackingRecord();
    }

    public User() {
        this.uid = "";
        this.name = "";
        this.goal = 0;
        this.stepSize = 0;
        this.records = new TrackingRecord();
    }

    public User(String uid, String name, float goal, int stepSize, TrackingRecord records) {
        this.uid = uid;
        this.name = name;
        this.goal = goal;
        this.stepSize = stepSize;
        this.records = records;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public float getGoal() {
        return goal;
    }

    public float getStepSize() {
        return stepSize;
    }

    public TrackingRecord getRecords() {
        return records;
    }

    public String getGeohash() {return geohash; }

    public double getLatitude() {return latitude; }

    public double getLongitude() {return longitude; }

    public List<String> getLockedApps() {
        return lockedApps;
    }

    public int getLockTimeMinute() {
        return lockTimeMinute;
    }

    public float getDistances(long time) {
        try {
            return getRecords().getDistances(time);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public User setGoal(float goal) {
        this.goal = goal;
        return this;
    }

    public User setStepsize(float length) {
        this.stepSize = length;
        return this;
    }

    public boolean finishDailyGoal(long time) {
        float currentDistance = getDistances(time);
        Log.d("LockService", "current distance = " + currentDistance);
        Log.d("LockService", "daily goal = " + goal);
        return (currentDistance >= goal);
    }

    @Override
    public String toString() {
        return String.format("uid = %s, name = %s, goal = %s, stepSize = %s, tracking records = %s", uid, name, goal, stepSize, records);
    }
}