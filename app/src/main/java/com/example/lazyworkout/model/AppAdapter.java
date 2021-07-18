package com.example.lazyworkout.model;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lazyworkout.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.adapter_design_backend> {

    List<App> appModels = new ArrayList<>();
    Context context;

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
