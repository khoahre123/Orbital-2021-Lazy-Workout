package com.example.lazyworkout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lazyworkout.R;
import com.example.lazyworkout.model.App;
import com.example.lazyworkout.util.Database;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.adapter_design_backend> {

    List<App> appModels = new ArrayList<>();
    Context context;
    List<String> lockedApps = new ArrayList<>();
    Database db = new Database();

    public AppAdapter(List<App> appModels, Context context) {
        this.appModels = appModels;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public adapter_design_backend onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.app_adapter, parent, false);
        adapter_design_backend design = new adapter_design_backend(view);
        return design;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull adapter_design_backend holder, int position) {

        App app = appModels.get(position);

        holder.appName.setText(app.getAppName());
        holder.appIcon.setImageDrawable(app.getAppIcon());

        if (app.getStatus() == 0) { //app is unlocked
            holder.appStatus.setImageResource(R.drawable.ic_unlock);
        } else {
            holder.appStatus.setImageResource(R.drawable.ic_lock);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.getStatus() == 0)  {
                    app.setStatus(1);
                    holder.appStatus.setImageResource(R.drawable.ic_lock);
                    Toast.makeText(context, app.getAppName() + " is locked",
                            Toast.LENGTH_SHORT).show();
                    lockedApps.add(app.getPackageName());
                    db.updateLockedApps(lockedApps);
                } else {
                    app.setStatus(0);
                    holder.appStatus.setImageResource(R.drawable.ic_unlock);
                    Toast.makeText(context, app.getAppName() + " is unlocked",
                            Toast.LENGTH_SHORT).show();
                    lockedApps.remove(app.getPackageName());
                    db.updateLockedApps(lockedApps);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return appModels.size();
    }

    public class adapter_design_backend extends RecyclerView.ViewHolder{

        TextView appName;
        ImageView appIcon, appStatus;

        public adapter_design_backend(@NonNull @NotNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.appAdapterName);
            appIcon = itemView.findViewById(R.id.appAdapterIcon);
            appStatus = itemView.findViewById(R.id.appAdapterStatus);
        }
    }

}
