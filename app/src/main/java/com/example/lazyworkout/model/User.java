package com.example.lazyworkout.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lazyworkout.util.Time;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class User {

    private String uid;
    private String name;
    private float goal;
    private int footSize;
    public TrackingRecord records;

    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
        this.goal = (float) 4.0;
        this.footSize = 37;
        this.records = new TrackingRecord();
    }

    public User() {
        this.uid = null;
        this.name = null;
        this.goal = 0;
        this.footSize = 0;
        this.records = new TrackingRecord();
    }

    public User(String uid, String name, float goal, int footSize, TrackingRecord records) {
        this.uid = uid;
        this.name = name;
        this.goal = goal;
        this.footSize = footSize;
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

    public int getFootSize() {
        return footSize;
    }

    public TrackingRecord getRecords() {
        return records;
    }

    public float getDistances(long time) {
        try {
            return getRecords().getDistances(time);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return String.format("uid = %s, name = %s, goal = %s, footsize = %s, tracking records = %s", uid, name, goal, footSize, records);
    }
}