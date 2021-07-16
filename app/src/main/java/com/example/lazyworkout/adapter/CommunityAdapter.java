package com.example.lazyworkout.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lazyworkout.R;
import com.example.lazyworkout.model.LocationUser;
import com.example.lazyworkout.view.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MyHolder> {

    private List<LocationUser> listData;
    private Context context;

    @NonNull
    @NotNull
    @Override
    public CommunityAdapter.MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.other_location, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommunityAdapter.MyHolder holder, int position) {
        LocationUser locationUser = listData.get(position);
        holder.bind(locationUser);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public CommunityAdapter(Context context, List<LocationUser> listData) {
        this.context = context;
        this.listData = listData;
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView locationImage;
        TextView locationNameView, locationDistance;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            locationImage.findViewById(R.id.locationImage);
            locationNameView.findViewById(R.id.locationNameView);
            locationDistance.findViewById(R.id.locationDistance);
        }

        public void bind(LocationUser locationUser) {
            String name = locationUser.getName();
            locationNameView.setText(name);
            Double distance = locationUser.getDistance();
            locationDistance.setText(distance.toString());
            String uid = locationUser.getUid();
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChatMessage.class);
                intent.putExtra("chatUID", uid);
                context.startActivity(intent);
            });
        }
    }
}
