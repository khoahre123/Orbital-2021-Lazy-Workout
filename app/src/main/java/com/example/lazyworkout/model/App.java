package com.example.lazyworkout.model;

import android.graphics.drawable.Drawable;

public class App {

    String packageName;
    String appName;
    Drawable appIcon;
    int status;

    public App(String packageName, String appName, Drawable appIcon, int status) {
        this.packageName = packageName;
        this.appName = appName;
        this.appIcon = appIcon;
        this.status = status;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public int getStatus() {
        return status;
    }
}
