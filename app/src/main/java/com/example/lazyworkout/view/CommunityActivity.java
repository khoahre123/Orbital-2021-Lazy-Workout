package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.lazyworkout.R;
import com.example.lazyworkout.adapter.CommunityAdapter;
import com.example.lazyworkout.model.LocationUser;
import com.example.lazyworkout.model.User;
import com.example.lazyworkout.service.LocationService;
import com.example.lazyworkout.util.Database;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CommunityActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private Button goToChat;
    private BottomNavigationView bottomNav;
    private String uid = FirebaseAuth.getInstance().getUid();
    private final double radius = 10*1000;
    private String[] geolocation = new String[1];
    private GeoLocation center;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<LocationUser> matchingDocs = new ArrayList<>();
    private RecyclerView nearByUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        initViews();
        startLocationService();
        getOwnLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(99)
    private void startLocationService() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
        } else {
            EasyPermissions.requestPermissions(this, "Your location is needed for better community engagement", 99, perms);
        }
    }

    private void initViews() {
        goToChat = findViewById(R.id.communityGoToChatBtn);
        bottomNav = findViewById(R.id.bottomNav);
        nearByUser = findViewById(R.id.nearByUser);

        goToChat.setOnClickListener(this);
        bottomNav.setSelectedItemId(R.id.navCommunity);

        bottomNav.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navOverview:
                startActivity(new Intent(CommunityActivity.this, OverviewActivity.class));
                overridePendingTransition(0,0);
                return true;

            case R.id.navAchievement:
                startActivity(new Intent(CommunityActivity.this, AchievementActivity.class));
                overridePendingTransition(0,0);
                return true;

            case R.id.navCommunity:
                return true;

            case R.id.navSetting:
                startActivity(new Intent(CommunityActivity.this, SettingActivity.class));
                overridePendingTransition(0,0);
                return true;
        }
        return true;
    }

    public void getOwnLocation() {
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid).get().
                    addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null) {
                                geolocation[0] = documentSnapshot.getString("geohash");
                                double lat = documentSnapshot.getDouble("latitude");
                                double lng = documentSnapshot.getDouble("longitude");
                                center = new GeoLocation(lat, lng);
                            } else {
                                geolocation[0] = null;
                                center = new GeoLocation(0,0);
                                Log.d("Community", "task is null");
                            }
                            getNearestUser();
                        }
                    });
        }
    }

    public void getNearestUser() {
        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radius);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b: bounds) {
            Query q = db.collection("users").orderBy("geohash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);
            tasks.add(q.get());
        }

        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(t -> {

                    for (Task<QuerySnapshot> task: tasks) {
                        QuerySnapshot snap = task.getResult();
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            double lat = doc.getDouble("latitude");
                            double lng = doc.getDouble("longitude");

                            GeoLocation docLocation = new GeoLocation(lat,lng);
                            double distance = GeoFireUtils.getDistanceBetween(docLocation, center);
                            if (distance <= radius) {
                                String name = doc.getString("name");
                                String uid = doc.getString("uid");
                                String geohash = doc.getString("geohash");

                                matchingDocs.add(new LocationUser(name, uid, lat, lng, geohash, distance));
                            }
                        }
                    }
                    populateRecycler();
                });
    }

    public void populateRecycler() {
        CommunityAdapter communityAdapter = new CommunityAdapter(this, matchingDocs);
        nearByUser.setLayoutManager(new LinearLayoutManager(this));
        nearByUser.setAdapter(communityAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.communityGoToChatBtn):
                startActivity(new Intent(this, ChatList.class));
        }
    }
}