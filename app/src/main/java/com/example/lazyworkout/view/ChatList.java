package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.example.lazyworkout.adapter.ChatListAdapter;
import com.example.lazyworkout.api.MessageHelper;
import com.example.lazyworkout.model.Message;
import com.example.lazyworkout.model.User;
import com.example.lazyworkout.model.UserList;
import com.example.lazyworkout.model.UserMessage;
import com.example.lazyworkout.view.ChatMessage;
import com.example.lazyworkout.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatList extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText textInputEditText;
    private TextInputLayout textInputLayout;
    private Button button;
    private RecyclerView userList;
    private ChatListAdapter chatListAdapter;
    private static final String TAG = "FindUserActivity";
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        initView();
        this.configureRecyclerView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.findButton):
                if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
                    break;
                }
                FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
                final String[] value = new String[1];
                mDatabase.collection("userLookup").document("findUserByEmail").get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        value[0] = document.getString(textInputEditText.getText().toString());
                                    } else {
                                        value[0] = null;
                                        Log.d(TAG, "document is null");
                                    }
                                    checkString(value);
                                }
                            }
                        });

                break;
            default:
                break;
        }
    }

    public void initView() {
        textInputEditText = findViewById(R.id.userFindInput);
        textInputLayout = findViewById(R.id.userFindLayout);
        button = findViewById(R.id.findButton);
        button.setOnClickListener(this);
        userList = findViewById(R.id.channelListView);
    }

    private void configureRecyclerView() {
        this.chatListAdapter = new ChatListAdapter(generateOptionsForAdapter(MessageHelper.getAllListMessage()),this);
        chatListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                userList.smoothScrollToPosition(0);
            }
        });
        this.chatListAdapter.startListening();
        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setAdapter(this.chatListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (chatListAdapter != null) {
            chatListAdapter.stopListening();
        }
    }

    private FirestoreRecyclerOptions<UserList> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<UserList>().setQuery(query, snapshot -> {
            Date modifiedDate = ((Timestamp) snapshot.get("modifiedDate")).toDate();
            String newestMessage = (String) snapshot.get("newestMessage");
            UserMessage sentUser = snapshot.get("sentUser", UserMessage.class);
            List<String> userList = (List<String>) snapshot.get("listUser");
            return new UserList(modifiedDate, newestMessage, sentUser, userList);
        }).setLifecycleOwner(this).build();
    }

    private void checkString(String[] value) {
        if (value[0] == null) {
            Log.d(TAG, "User not found");
        } else {
            Intent intent = new Intent(this, ChatMessage.class);
            intent.putExtra("chatUID", value[0]);
            startActivity(intent);
        }
    }
}