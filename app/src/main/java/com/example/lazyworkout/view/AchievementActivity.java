package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lazyworkout.R;
import com.example.lazyworkout.model.User;
import com.example.lazyworkout.service.StepCountingService;
import com.example.lazyworkout.util.Database;
import com.example.lazyworkout.util.Time;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseAppLifecycleListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AchievementActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNav;
    private TextView longestDayDistance, currentDistance, dayStreak, dailyWalkingNow, dailyWalkingMission, totalWalkingNow, totalWalkingMission;
    private TextView streakNow, streakMission;
    private ProgressBar dailyWalkingProgress, totalWalkingProgress, streakProgress;
    private DatabaseReference mDatabase;
    private Database db =  new Database();
    private float longestDistance;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Log.d("Achievement", "1");
                getDataFromFirebase();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        setContentView(R.layout.activity_achievement);

        initViews();
        getDataFromLocal();
        setMission();
        registerReceiver(broadcastReceiver, new IntentFilter(StepCountingService.ACHIEVEMENT_ACTION));
        intent = new Intent("com.example.lazyworkout.achievement");
        intent.putExtra("command", "congrats");

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("command") == "update") {
                getDataFromLocal();
                updateMission();
            } else {

            }
        }
    };

    private void initViews() {
        bottomNav = findViewById(R.id.bottomNav);
        longestDayDistance = findViewById(R.id.longestDayDistance);
        currentDistance = findViewById(R.id.currentDistance);
        dayStreak = findViewById(R.id.dayStreak);
        dailyWalkingProgress = findViewById(R.id.dailyWalkingProgress);
        dailyWalkingMission = findViewById(R.id.dailyWalkingMission);
        dailyWalkingNow = findViewById(R.id.dailyWalkingNow);
        streakProgress = findViewById(R.id.streakProgress);
        streakMission = findViewById(R.id.streakMission);
        streakNow = findViewById(R.id.streakNow);
        totalWalkingProgress = findViewById(R.id.totalWalkingProgress);
        totalWalkingMission = findViewById(R.id.totalWalkingMission);
        totalWalkingNow = findViewById(R.id.totalWalkingNow);
        bottomNav.setSelectedItemId(R.id.navAchievement);

        bottomNav.setOnNavigationItemSelectedListener(this);
        getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("dailyWalkingMission", 4).apply();
        getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("totalWalkingMission", 7).apply();
        getSharedPreferences(db.getID(), MODE_PRIVATE).edit().putFloat("streakMission", 2).apply();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navOverview:
                startActivity(new Intent(AchievementActivity.this, OverviewActivity.class));
                overridePendingTransition(0,0);
                return true;

            case R.id.navAchievement:
                return true;

            case R.id.navCommunity:
                startActivity(new Intent(AchievementActivity.this, CommunityActivity.class));
                overridePendingTransition(0,0);
                return true;

            case R.id.navSetting:
                startActivity(new Intent(AchievementActivity.this, SettingActivity.class));
                overridePendingTransition(0,0);
                return true;
        }
        return true;
    }

    public void getDataFromLocal() {
        Integer longestDistance = Math.round(getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("todayDistance", 0));
        Integer totalDistances = Math.round(getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("totalDistances", 0));
        Integer longestStreak = Math.round(getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("longestStreak", 0));
        longestDayDistance.setText(String.format("%d km", longestDistance));
        currentDistance.setText(String.format("%d km", totalDistances));
        dayStreak.setText(String.format("%d days", longestStreak));
    }

    public void setMission() {
        Integer dailyWalking = Math.round(getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("dailyWalkingMission", 4));
        dailyWalkingProgress.setMax(dailyWalking);
        dailyWalkingMission.setText(String.format(" / %d km", dailyWalking));
        Integer totalWalking = Math.round(getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("totalWalkingMission", 7));
        totalWalkingProgress.setMax(totalWalking);
        totalWalkingMission.setText(String.format(" / %d km", totalWalking));
        Integer streak = Math.round(getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("streakMission", 2));
        streakProgress.setMax(streak);
        streakMission.setText(String.format(" / %d days", streak));
    }

    public void updateMission() {
        Float todayDistance = getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("todayDistance", 0);
        Float totalDistances = getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("totalDistances", 0);
        Integer currentStreak = Math.round(getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("currentStreak", 0));
        Float dailyWalking = getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("dailyWalkingMission", 4);
        Float totalWalking = getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("totalWalkingMission", 7);
        Integer streak = Math.round(getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("streakMission", 2));
        if (todayDistance >= dailyWalking) {
            if (dailyWalking > 10) {
                dailyWalking += 3;
            } else {
                dailyWalking += 2;
            }
            dailyWalkingMission.setText(String.format(" / %d km", Math.round(dailyWalking)));
            dailyWalkingProgress.setProgress(0);
            dailyWalkingProgress.setMax(Math.round(dailyWalking));
            sendBroadcast(intent);
        } else {
            dailyWalkingNow.setText(String.format("%.2f", todayDistance));
            dailyWalkingProgress.setProgress(Math.round(todayDistance));
        }

        if (totalDistances > totalWalking) {
            if (totalWalking > 15) {
                totalWalking += 7;
            } else {
                totalWalking += 5;
            }
            totalWalkingMission.setText(String.format(" / %d km", Math.round(totalWalking)));
            totalWalkingProgress.setProgress(0);
            totalWalkingProgress.setMax(Math.round(totalWalking));
            sendBroadcast(intent);
        } else {
            totalWalkingNow.setText(String.format("%.2f", totalDistances));
            totalWalkingProgress.setProgress(Math.round(totalDistances));
        }

        if (currentStreak > streak) {
            if (streak > 5) {
                streak += 2;
            } else {
                streak += 1;
            }
            streakMission.setText(String.format(" / %d km", streak));
            streakProgress.setProgress(0);
            streakProgress.setMax(streak);
            sendBroadcast(intent);
        } else {
            streakNow.setText(String.format("%d", currentStreak));
            streakProgress.setProgress(currentStreak);
        }
    }

    public void getDataFromFirebase() {
        Integer todayDistance = Math.round(getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("todayDistance", 0));
        DocumentReference userRef = db.fStore.collection(db.DB_NAME).document(db.getID());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        User user = documentSnapshot.toObject(User.class);
                        Float longestDay = user.getLongestDay();
                        if (todayDistance > longestDay) {
                            longestDayDistance.setText(String.format("%d km", todayDistance));
                            Map<String, Object> update = new HashMap<>();
                            update.put("longestDay", todayDistance);
                            FirebaseFirestore.getInstance().collection("users").document(db.getID()).update(update);
                        } else {
                            longestDayDistance.setText(String.format("%.2f km", longestDay));
                        }
                        Float totalDistances = user.getTotalDistances();
                        currentDistance.setText(String.format("%.2f km", totalDistances));
                        Float longestStreak = user.getLongestStreak();
                        String databaseStreak = getSharedPreferences(db.getID(), MODE_PRIVATE).getString("current_streak", "0 days");
                        Float todayStreak = Float.parseFloat(databaseStreak.substring(0, databaseStreak.length() - 4));
                        if (todayStreak > longestStreak) {
                            dayStreak.setText(String.format("%.2f days", todayStreak));
                            Map<String, Object> update = new HashMap<>();
                            update.put("longestStreak", todayStreak);
                            FirebaseFirestore.getInstance().collection("users").document(db.getID()).update(update);
                        } else {
                            dayStreak.setText(String.format("%.2f days", longestStreak));
                        }
                    }
                }
            }
        });
    }

}