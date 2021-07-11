package com.example.lazyworkout.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lazyworkout.model.Message;
import com.example.lazyworkout.model.UserList;
import com.example.lazyworkout.model.UserMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

public class MessageHelper {

    private static final String COLLECTION_NAME = "messages";
    private static final String TAG = "getMessage";

    // --- GET ---

    public static ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            String str = snapshot.getValue(String.class);
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {
            Log.w(TAG, "loadPost:onCancelled", error.toException());
        }
    };

    public static Query getAllMessageForChat(String chat){
        String ownUid = FirebaseAuth.getInstance().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String path;
        if (ownUid.compareTo(chat) > 0) {
            path = chat + "_" + ownUid;

        } else {
            path = ownUid + "_" + chat;
        }
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String childPath = "/chats" + path;
                if (!snapshot.hasChild(childPath)) {
                    Log.d(TAG, "3");
                    FirebaseFirestore.getInstance().collection("chats").document(path).collection(COLLECTION_NAME);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.wtf(TAG, error.getMessage());
            }
        });
        Log.d(TAG, path);
        return ChatHelper.getChatCollection()
                .document(path)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    public static Query getAllListMessage() {
        String ownUid = FirebaseAuth.getInstance().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        return ChatHelper.getChatCollection().whereArrayContains("listUser", ownUid);
    }

    public static Task<DocumentReference> createMessageForChat(String textMessage, String chat,  UserMessage userMessageSender,
                                                               String userMessageReceiver){

        // 1 - Create the Message object
        Message message = new Message(textMessage, userMessageSender);
        UserList userList = new UserList(textMessage, userMessageSender, userMessageReceiver);
        Log.d(TAG, ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME).toString());

        Log.d(TAG, userList.getUserList().toString());
        ChatHelper.getChatCollection().document(chat).set(userList.getUserList());
        // 2 - Store Message to Firestore
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .add(message);
    }
}