package com.example.lazyworkout.model;

import android.util.Log;

import com.example.lazyworkout.util.Time;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TrackingRecord {
    public float totalDistances;
    public HashMap<String, Record> records;

    public TrackingRecord() {
        this.totalDistances = 0;
        this.records = new HashMap<>();
        Record record = new Record();
        records.put(String.valueOf(record.getTime()), record);
    }

    public TrackingRecord(float totalDistances) {
        this.totalDistances = totalDistances;
        this.records = new HashMap<>();
        Record record = new Record();
        records.put(String.valueOf(record.getTime()), record);
    }

    public TrackingRecord(float totalDistances, HashMap<String, Record> records) {
        this.totalDistances = totalDistances;
        this.records = records;
    }

    public float getTotalDistances() {
        return totalDistances;
    }

    public HashMap<String, Record> getRecords() {
        return records;
    }

    public float getDistances(long time) {
        if (isEmptyRecord(time)) {
            return 0;
        }
        return records.get(String.valueOf(time)).getDistance();
    }

    public boolean isEmptyRecord(long time) {
        return (records.get(String.valueOf(time)) == null);
    }

    public TrackingRecord newDayRecord(long time, float distance) {
        Record newRecord = new Record(time, distance);
        getRecords().put(String.valueOf(time), newRecord);
        totalDistances += distance;
        return this;
    }

    public TrackingRecord todayRecord(float distance) {
        Log.d("Tracking Record", String.valueOf(Time.getToday()));
        float todayCompletedDistance = getRecords().get(String.valueOf(Time.getToday())).getDistance();

        Record newRecord = new Record(Time.getToday(), distance);
        getRecords().put(String.valueOf(Time.getToday()), newRecord);

        totalDistances = totalDistances - todayCompletedDistance + distance;

        return this;
    }

    @Override
    public String toString() {
        String str = "";

        Iterator it = getRecords().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            str += pair.getKey() + " = " + pair.getValue();
            it.remove(); // avoids a ConcurrentModificationException
        }
        return str;
    }
}
