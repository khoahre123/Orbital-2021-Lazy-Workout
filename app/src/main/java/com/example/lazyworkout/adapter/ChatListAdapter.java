package com.example.lazyworkout.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lazyworkout.R;
import com.example.lazyworkout.model.User;
import com.example.lazyworkout.model.UserList;
import com.example.lazyworkout.view.ChatMessage;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class ChatListAdapter extends FirestoreRecyclerAdapter<UserList, ChatListAdapter.MyHolder> {

    private Context context;
    private List<UserList> listUser;
    private HashMap<String, String> lastMessageMap = new HashMap<>();

    public ChatListAdapter(FirestoreRecyclerOptions<UserList> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_chat_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull MyHolder holder, int position, @NonNull @NotNull UserList model) {
        UserList userList = this.getItem(position);
        holder.bind(userList);
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView profileImage, onlineStatus;
        TextView nameView, lastMessageView;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            onlineStatus = itemView.findViewById(R.id.onlineStatus);
            nameView = itemView.findViewById(R.id.nameView);
            lastMessageView = itemView.findViewById(R.id.lastMessage);
        }

        public void bind(UserList userList) {
            String lastMessage = userList.getNewestMessage();
            if (lastMessage == null || lastMessage.equals("default")) {
                lastMessageView.setVisibility(View.GONE);
            } else {
                lastMessageView.setVisibility(View.VISIBLE);
                lastMessageView.setText(lastMessage);
            }
            String uid;
            String ownUid = FirebaseAuth.getInstance().getUid();
            String sentUser = userList.getSentUid();
            String receiveUser = userList.getReceiveUid();
            if (ownUid.equals(sentUser)) {
                uid = receiveUser;
            } else {
                uid = sentUser;
            }
            Log.d("Chatlist", uid);
            final String[] name = new String[1];
            FirebaseFirestore.getInstance().collection("users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    name[0] = documentSnapshot.getString("name");
                } else {
                    name[0] = "nah";
                }
                setName(name);
            });
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChatMessage.class);
                intent.putExtra("chatUID", uid);
                context.startActivity(intent);
            });
        }

        public void setName(String[] name) {
            nameView.setText(name[0]);
        }
    }
}
