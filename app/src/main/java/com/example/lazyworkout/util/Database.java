package com.example.lazyworkout.util;

import android.content.Context;
import android.os.Build;
import android.telecom.Call;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.lazyworkout.model.Record;
import com.example.lazyworkout.model.TrackingRecord;
import com.example.lazyworkout.model.User;
import com.example.lazyworkout.service.StepCountingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Database {
    private static final String TAG = "Database";
    public static final String DB_NAME = "users";

    public FirebaseAuth fAuth= FirebaseAuth.getInstance();
    public FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public String getID() {
        String uid = fAuth.getCurrentUser().getUid();
        return uid;
    }

    public boolean signout() {
        fAuth.signOut();
        return true;
    }

    public void createNewUser(User newUser) {
        Log.d(TAG, "create new user = " + newUser.toString());
        fStore.collection(DB_NAME).document(newUser.getUid()).set(newUser);
    }

    // MAYBE WRONG: return steps made in a specific day
    public float getDistances(User user, long time) {
        Log.d(TAG, "getDistances");
        //TODO: not try-catch like this
        try {
            float distance = user.getDistances(time);
            Log.d(TAG + "getDistances", "distance =" + String.valueOf(distance));
            return distance;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    // update today distance, update totalDistance
    public void saveCurrentSteps(User user, float distance) {
        TrackingRecord updateRecord = user.getRecords().todayRecord(distance);
        Map<String, Object> data = new HashMap<>();
        data.put("records", updateRecord);

        fStore.collection(DB_NAME).document(getID()).set(data, SetOptions.merge());
    }

    //start new day distance, add distance to totalDistance
    public void insertNewDay(User user, long time, float distance) {
        TrackingRecord updateRecord = user.getRecords().newDayRecord(time, distance);
        Map<String, Object> data = new HashMap<>();
        data.put("records", updateRecord);

        fStore.collection(DB_NAME).document(getID()).set(data, SetOptions.merge());
    }

    public void updateGoal(float goal) {
        Map<String, Object> data = new HashMap<>();
        data.put("goal", goal);

        fStore.collection(DB_NAME).document(getID()).set(data, SetOptions.merge());
    }

    public void updateStepsize(float stepsize) {
        Map<String, Object> data = new HashMap<>();
        data.put("stepSize", stepsize);

        fStore.collection(DB_NAME).document(getID()).set(data, SetOptions.merge());
    }

    public void updateLockedApps(List<String> lockedApps) {
        Map<String, Object> data = new HashMap<>();
        data.put("lockedApps", lockedApps);

        fStore.collection(DB_NAME).document(getID()).set(data, SetOptions.merge());
    }

    public void updateLockTime(int minutes) {
        Map<String, Object> data = new HashMap<>();
        data.put("lockTimeMinute", minutes);

        fStore.collection(DB_NAME).document(getID()).set(data, SetOptions.merge());
    }
}


