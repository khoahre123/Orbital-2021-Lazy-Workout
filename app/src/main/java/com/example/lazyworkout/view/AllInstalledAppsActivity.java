    package com.example.lazyworkout.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lazyworkout.R;
import com.example.lazyworkout.model.App;
import com.example.lazyworkout.adapter.AppAdapter;
import com.example.lazyworkout.model.User;
import com.example.lazyworkout.util.Database;
import com.example.lazyworkout.util.Time;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

    public class AllInstalledAppsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AllInstalledAppsActivit";

    private Button goToOverviewBtn;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    private ConstraintLayout appsView;

    List<App> appModelList = new ArrayList<>();

    AppAdapter adapter;

    Database db = new Database();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_installed_apps);

        loadLockedApps();

        if (!(isAccessGranted())) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("User Permission")
                    .setMessage("Sorry. Please allow " +
                            "app usage permission first")
                    .setNeutralButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        if (progressDialog != null) {
            Log.d(TAG, "progress not null");
            progressDialog.setTitle("Fetching Apps");
            progressDialog.setMessage("Loading");
            progressDialog.show();
        }

        if (!(isAccessGranted())) {
            Log.d(TAG, "onResume " + isAccessGranted());
            new MaterialAlertDialogBuilder(this)
                    .setTitle("User Permission")
                    .setMessage("Sorry. Please allow " +
                            "app usage permission first")
                    .setNeutralButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private void loadLockedApps() {
        DocumentReference userRef = db.fStore.collection(db.DB_NAME).document(db.getID());
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    User user = document.toObject(User.class);
                    Map<String, Object> map = document.getData();
                    Log.d(TAG, "user map: " + map.toString());

                    List<String> lockedAppsList = user.getLockedApps();

                    initViews(lockedAppsList);

                } else {
                    Log.d(TAG, "no such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void initViews(List<String> lockedAppsList) {

        Log.d(TAG, "initViews");

        appsView = findViewById(R.id.allInstalledApps);

        goToOverviewBtn = findViewById(R.id.allApssBtn);
        recyclerView = findViewById(R.id.allAppsRecycleView);

        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Fetching Apps");
        progressDialog.setMessage("Loading");


        progressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Log.d(TAG, "about to get installed apps");
                getInstalledApps(lockedAppsList);
            }
        });

        progressDialog.show();

        goToOverviewBtn.setOnClickListener(this);

        adapter = new AppAdapter(appModelList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        progressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getInstalledApps(lockedAppsList);
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case (R.id.allApssBtn):
                startActivity(new Intent(this, OverviewActivity.class));
                break;
        }
    }

    public void getInstalledApps(List<String> lockedAppsList) {
        Log.d(TAG, "get installed apps");

        List<PackageInfo>packageInfos = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packageInfos.size(); i++) {
            String name = packageInfos.get(i).applicationInfo.loadLabel(getPackageManager()).toString();
            Drawable icon = packageInfos.get(i).applicationInfo.loadIcon(getPackageManager());
            String packageName = packageInfos.get(i).packageName;

            if (packageName.equals("com.example.lazyworkout")) {
                continue;
            }

            if (!(lockedAppsList.isEmpty())) {
                if (lockedAppsList.contains(packageName)) {
                    appModelList.add(new App(packageName, name, icon, 1));
                } else {
                    appModelList.add(new App(packageName, name, icon, 0));
                }
            } else {
                appModelList.add(new App(packageName, name, icon, 0));
            }

        }

        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
        Log.d(TAG, "done get installed apps");
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

}