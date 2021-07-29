package com.example.lazyworkout.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.lazyworkout.util.Constant;
import com.example.lazyworkout.util.Time;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class User {

    private String uid;
    private String name;
    private float goal = Constant.DEFAULT_GOAL;
    private float stepSize = Constant.DEFAULT_STEP_SIZE;
    private float longestDay, currentStreak, longestStreak = 0;
    public TrackingRecord records = new TrackingRecord();
    private double latitude, longitude = 0;
    private String geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(0,0));
    private List<String> lockedApps = new ArrayList<>();
    private int lockTimeMinute = Constant.LOCK_TIME;

    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
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

    public float getTotalDistances() {
        return getRecords().getTotalDistances();
    }

    public float getCurrentStreak() {
        return currentStreak;
    }

    public float getLongestDay() {
        return longestDay;
    }

    public float getLongestStreak() {
        return longestStreak;
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