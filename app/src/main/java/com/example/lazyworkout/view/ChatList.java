package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lazyworkout.view.ChatMessage;
import com.example.lazyworkout.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class ChatList extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText textInputEditText;
    private TextInputLayout textInputLayout;
    private Button button;
    private static final String TAG = "FindUserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        initView();
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