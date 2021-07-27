package com.example.lazyworkout.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllInstalledAppsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllInstalledAppsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    ProgressDialog progressDialog;

    private ArrayList<App> appModelList = new ArrayList<>();
    private AppAdapter appAdapter;

    private String uid = FirebaseAuth.getInstance().getUid();
    Database db = new Database();

    public AllInstalledAppsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllInstalledAppsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllInstalledAppsFragment newInstance(String param1, String param2) {
        AllInstalledAppsFragment fragment = new AllInstalledAppsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_all_installed_apps, container, false);
        recyclerView = view.findViewById(R.id.settingLockRecycleView);
        loadLockedApps();


        return view;
    }

    private void initViews(List<String> lockedAppsList) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressDialog = new ProgressDialog(getContext());

        progressDialog.setTitle("Fetching Apps");
        progressDialog.setMessage("Loading");


        progressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getInstalledApps(lockedAppsList);
            }
        });

        progressDialog.show();

        progressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getInstalledApps(lockedAppsList);
            }
        });

        appAdapter = new AppAdapter(appModelList, getContext());
        recyclerView.setAdapter(appAdapter);

    }

    public void getInstalledApps(List<String> lockedAppsList) {

        List<ApplicationInfo>packageInfos = getActivity().getPackageManager().getInstalledApplications(0);

        Log.d("AppAdapter", "before adding locked apps =" + appModelList.toString());

        for (int i = 0; i < packageInfos.size(); i++) {

            String name = packageInfos.get(i).loadLabel(getActivity().getPackageManager()).toString();
            Drawable icon = packageInfos.get(i).loadIcon(getActivity().getPackageManager());
            String packageName = packageInfos.get(i).packageName;

            if (packageName.equals("com.example.lazyworkout")) {
                continue;
            }

            if (getActivity().getPackageManager().getLaunchIntentForPackage(packageInfos.get(i).packageName) != null){
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

        appAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
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

                    }
                } else {

                }
            });
        }
    }
}