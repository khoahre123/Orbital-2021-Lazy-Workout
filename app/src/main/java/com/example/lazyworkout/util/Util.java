package com.example.lazyworkout.util;

public class Util {

    public static float getGoal(String str) {
        String goal = str.substring(0, str.length() - 2);
        return Float.parseFloat(goal);
    }

    public static float getStepsize(String str) {
        String goal = str.substring(0, str.length() - 2);
        return Float.parseFloat(goal) / 100000;
    }

    public static int computeStepcount(float goal, float stepsize) {
        return (int) (goal / stepsize);
    }
}