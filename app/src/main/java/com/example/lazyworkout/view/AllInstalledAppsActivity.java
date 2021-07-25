    package com.example.lazyworkout.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
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
import android.net.Uri;
import android.os.Build;
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
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
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
        private String uid = FirebaseAuth.getInstance().getUid();

        private boolean prevPermissionGranted = true;

        public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 2323;

        private ConstraintLayout appsView;

        List<App> appModelList = new ArrayList<>();
        List<App> currentLockedApps = new ArrayList<>();

        AppAdapter adapter;

        Database db = new Database();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            Log.d(TAG, "onCreate");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_all_installed_apps);

            if (isAllPermissionGranted()) {
                loadLockedApps();
            }

        }

        @Override
        protected void onResume() {
            Log.d(TAG, "onResume");
            super.onResume();

            if (isAllPermissionGranted() && !(prevPermissionGranted)) {
                prevPermissionGranted = true;
                if (progressDialog != null) {
                    Log.d(TAG, "progress not null");
                    progressDialog.setTitle("Fetching Apps");
                    progressDialog.setMessage("Loading");
                    progressDialog.show();
                }
                loadLockedApps();
            }
        }

        public boolean isAllPermissionGranted() {
            if (!(permissionOverlayWindowGranted())) {
                Log.d(TAG, "usage stats not granted");
                prevPermissionGranted = false;
                requestPermissionOverlayWindow();
                return false;
            } else {
                Log.d(TAG, "usage stats granted");
                if (!(permissionUsageStatsGranted())) {
                    Log.d(TAG, "overlay window not granted");
                    prevPermissionGranted = false;
                    requestPermissionUsageStats();
                    return false;
                } else {
                    Log.d(TAG, "all permission granted");
                    return true;
                }
            }

        }

        public boolean permissionOverlayWindowGranted() {
            return ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) &&
                    Settings.canDrawOverlays(this));
        }

        public boolean permissionUsageStatsGranted() {
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

        public void requestPermissionOverlayWindow() {
            // Check if Android M or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Show alert dialog to the user saying a separate permission is needed
                // Launch the settings activity if the user prefers

                new MaterialAlertDialogBuilder(this)
                        .setTitle("User Permission")
                        .setMessage("Please allow " +
                                "LazyWorkout to appear on top of other apps first. " +
                                "The permission is used only to show lockscreen when user does not achieve daily walking goal.")
                        .setNeutralButton("Go to settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                            }
                        })
                        .setCancelable(false)
                        .show();

            }
        }

        public void requestPermissionUsageStats() {

            new MaterialAlertDialogBuilder(this)
                    .setTitle("User Permission")
                    .setMessage("Please allow " +
                            "LazyWorkout to access usage data first. " +
                            "Usage data is used only to get the list of all installed apps")
                    .setNeutralButton("Go to settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(this)) {
//                        PermissionDenied();
                    } else {
                        // Permission Granted-System will work
                    }

                }
            }
        }

        private void loadLockedApps() {
            if (uid != null) {
                DocumentReference userRef = db.fStore.collection(db.DB_NAME).document(uid);
                userRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            User user = document.toObject(User.class);
                            Map<String, Object> map = document.getData();
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

                    Log.d("AppAdapter", "dtb lock app = " + lockedAppsList.toString());
                    getInstalledApps(lockedAppsList);
                }
            });

            progressDialog.show();

            goToOverviewBtn.setOnClickListener(this);

            progressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    getInstalledApps(lockedAppsList);
                }
            });

            adapter = new AppAdapter(appModelList, this);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);


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
                    startActivity(new Intent(AllInstalledAppsActivity.this, OverviewActivity.class));;
                    break;

                default:
                    break;
            }
        }

        public void getInstalledApps(List<String> lockedAppsList) {
            Log.d(TAG, "get installed apps");

            List<ApplicationInfo>packageInfos = getPackageManager().getInstalledApplications(0);

            Log.d("AppAdapter", "before adding locked apps =" + appModelList.toString());

            for (int i = 0; i < packageInfos.size(); i++) {

                String name = packageInfos.get(i).loadLabel(getPackageManager()).toString();
                Drawable icon = packageInfos.get(i).loadIcon(getPackageManager());
                String packageName = packageInfos.get(i).packageName;

                if (packageName.equals("com.example.lazyworkout")) {
                    continue;
                }

                if (getPackageManager().getLaunchIntentForPackage(packageInfos.get(i).packageName) != null){
                    //If you're here, then this is a launch-able app
                    if (lockedAppsList == null) {
                        appModelList.add(new App(packageName, name, icon, 0));
                        continue;
                    }
                    if (!(lockedAppsList.isEmpty())) {
                        if (lockedAppsList.contains(packageName)) {
                            appModelList.add(new App(packageName, name, icon, 1)); // status 1: LOCKED
                        } else {
                            appModelList.add(new App(packageName, name, icon, 0)); // status 1: NOT LOCKED
                        }
                    } else {
                        appModelList.add(new App(packageName, name, icon, 0));
                    }
                }


            }

            Log.d("AppAdapter", "get installed locked app = " + lockedAppsList.toString());

            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
            Log.d(TAG, "done get installed apps");
        }

        @Override
        protected void onPause() {
            Log.d(TAG, "onPause");
            super.onPause();
        }

    }