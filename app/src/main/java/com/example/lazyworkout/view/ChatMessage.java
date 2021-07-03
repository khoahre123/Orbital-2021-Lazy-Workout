package com.example.lazyworkout.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lazyworkout.R;
import com.example.lazyworkout.adapter.BoxChatAdapter;
import com.example.lazyworkout.api.MessageHelper;
import com.example.lazyworkout.api.UserHelper;
import com.example.lazyworkout.model.Message;
import com.example.lazyworkout.model.UserMessage;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ChatMessage extends AppCompatActivity implements BoxChatAdapter.Listener, View.OnClickListener {
    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerView messageRecycler;
    private BoxChatAdapter boxChatAdapter;
    private EditText chatInput;
    private View chatBox;
    @Nullable private UserMessage modelCurrentUserMessage;
    private String currentChatName;
    private Button sendButton;
    private String combineString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        messageRecycler = findViewById(R.id.messageList);
        chatInput = findViewById(R.id.chatBoxInput);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);
        chatBox = findViewById(R.id.chatBoxView);
        String chatUID = getIntent().getStringExtra("chatUID");
        this.configureRecyclerView(chatUID);
        this.getCurrentUserFromFirestore();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.sendButton):
                if (!TextUtils.isEmpty(chatInput.getText()) && modelCurrentUserMessage != null) {
                    MessageHelper.createMessageForChat(chatInput.getText().toString(), this.combineString, modelCurrentUserMessage)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                }
                            });
                    this.chatInput.setText("");
                }
                break;
            default:
                break;
        }
    }

    // GET CURRENT USER FROM FIRESTORE
    private void getCurrentUserFromFirestore() {
        UserHelper.getUser(FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUserMessage = documentSnapshot.toObject(UserMessage.class);
            }
        });
    }

    private void configureRecyclerView(String chatName) {
        this.currentChatName = chatName;
        String ownUid = FirebaseAuth.getInstance().getUid();
        if (ownUid.compareTo(chatName) > 0) {
            this.combineString = chatName + "_" + ownUid;

        } else {
            this.combineString = ownUid + "_" + chatName;
        }
        this.boxChatAdapter = new
                BoxChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat(this.currentChatName)),
                Glide.with(this), this, FirebaseAuth.getInstance().getUid());
        boxChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                messageRecycler.smoothScrollToPosition(boxChatAdapter.getItemCount());
            }
        });
        this.boxChatAdapter.startListening();
        Integer i = this.boxChatAdapter.getItemCount();
        Log.d("Chat Message", i.toString());
        messageRecycler.setLayoutManager(new LinearLayoutManager(this));
        messageRecycler.setAdapter(this.boxChatAdapter);
        this.boxChatAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onStart() {
        super.onStart();
        boxChatAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (boxChatAdapter != null) {
            boxChatAdapter.stopListening();
        }
    }

    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).setLifecycleOwner(this).build();
    }
    @Override
    public void onDataChanged() {
        chatBox.setVisibility(this.boxChatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}