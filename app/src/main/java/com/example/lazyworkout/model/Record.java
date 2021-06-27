package com.example.lazyworkout.model;

import com.example.lazyworkout.util.Time;

public class Record {
    private long time;
    private float distance;

    public Record() {
        this.time = Time.getToday();
        this.distance = 0;
    }

    public Record(long time, float distance) {
        this.time = time;
        this.distance = distance;
    }

    public long getTime() {
        return time;
    }

    public float getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return String.format("time = %s, distance = %s", time, distance);
    }
}
