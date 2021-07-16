package com.example.lazyworkout.model;

public class LocationUser {

    private String name, uid, geohash;
    private double latitude, longitude, distance;

    public LocationUser() { }

    public LocationUser(String name, String uid, double latitude, double longitude, String geohash, double distance) {
        this.name = name;
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geohash = geohash;
        this.distance = distance;
    }

    public String getName() {return name;}

    public String getUid() {return uid;}

    public String getGeohash() {return geohash;}

    public double getLatitude() {return latitude;}

    public double getLongitude() {return longitude;}

    public double getDistance() {return distance;}
}
