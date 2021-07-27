package com.example.lazyworkout.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lazyworkout.R;
import com.example.lazyworkout.adapter.AppAdapter;
import com.example.lazyworkout.model.App;
import com.example.lazyworkout.model.User;
import com.example.lazyworkout.util.Database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingLockedAppsActivity extends AppCompatActivity {

    private static final String TAG = "SettingInstalledAppsAct";

    private Button btn;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private String uid = FirebaseAuth.getInstance().getUid();

    List<App> appModelList = new ArrayList<>();
    AppAdapter adapter;

    Database db = new Database();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_locked_apps);

        loadLockedApps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (progressDialog != null) {
            progressDialog.setTitle("Fetching Apps");
            progressDialog.setMessage("Loading");
            progressDialog.show();
        }
    }

    private void loadLockedApps() {
        if (uid != null) {
            DocumentReference userRef = db.fStore.collection(db.DB_NAME).document(db.getID());
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

        btn = findViewById(R.id.settingLockedAppsBtn);
        recyclerView = findViewById(R.id.settingLockedAppsView);

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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                    }
                }
            }


        }

        Log.d("AppAdapter", "get installed locked app = " + lockedAppsList.toString());

        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
        Log.d(TAG, "done get installed apps");
    }
}