package com.example.lazyworkout.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MyHolder> {
    @NonNull
    @NotNull
    @Override
    public CommunityAdapter.MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommunityAdapter.MyHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
