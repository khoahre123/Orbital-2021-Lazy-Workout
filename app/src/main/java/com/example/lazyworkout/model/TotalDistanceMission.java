package com.example.lazyworkout.model;

public class TotalDistanceMission extends Mission{

    private double totalDistance;

    public TotalDistanceMission(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getTotalDistance() {
        return totalDistance;
    }
}
