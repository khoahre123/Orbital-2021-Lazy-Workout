package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lazyworkout.R;
import com.example.lazyworkout.model.User;
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
    private TextView longestDayDistance, currentDistance, dayStreak;
    private DatabaseReference mDatabase;
    private Database db =  new Database();
    private float longestDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        initViews();
        getData();
        FirebaseDatabase.getInstance().getReference("users").child(db.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                getData();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void initViews() {
        bottomNav = findViewById(R.id.bottomNav);
        longestDayDistance = findViewById(R.id.longestDayDistance);
        currentDistance = findViewById(R.id.currentDistance);
        dayStreak = findViewById(R.id.dayStreak);

        bottomNav.setSelectedItemId(R.id.navOverview);

        bottomNav.setOnNavigationItemSelectedListener(this);


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

    public void getData() {
        Integer todayDistance = Math.round(getSharedPreferences(db.getID(), MODE_PRIVATE).getFloat("todayDistance", 0));
        DocumentReference userRef = db.fStore.collection(db.DB_NAME).document(db.getID());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        User user = documentSnapshot.toObject(User.class);
                        Integer longestDay = Math.round(user.getLongestDay());
                        if (todayDistance > longestDay) {
                            longestDayDistance.setText(String.format("%d km", todayDistance));
                            Map<String, Object> update = new HashMap<>();
                            update.put("longestDay", todayDistance);
                            FirebaseFirestore.getInstance().collection("users").document(db.getID()).update(update);
                        } else {
                            longestDayDistance.setText(String.format("%d km", longestDay));
                        }
                        Integer totalDistances = Math.round(user.getTotalDistances());
                        currentDistance.setText(String.format("%d km", totalDistances));
                        Integer longestStreak = Math.round(user.getLongestStreak());
                        String databaseStreak = getSharedPreferences(db.getID(), MODE_PRIVATE).getString("current_streak", "0 days");
                        Integer todayStreak = Math.round(Float.parseFloat(databaseStreak.substring(0, databaseStreak.length() - 4)));
                        if (todayStreak > longestStreak) {
                            dayStreak.setText(String.format("%d days", todayStreak));
                            Map<String, Object> update = new HashMap<>();
                            update.put("longestStreak", todayStreak);
                            FirebaseFirestore.getInstance().collection("users").document(db.getID()).update(update);
                        } else {
                            dayStreak.setText(String.format("%d days", longestStreak));
                        }
                    }
                }
            }
        });
    }

}