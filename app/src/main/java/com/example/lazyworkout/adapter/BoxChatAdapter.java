package com.example.lazyworkout.adapter;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.lazyworkout.R;
import com.example.lazyworkout.model.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BoxChatAdapter extends FirestoreRecyclerAdapter<Message, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    //FOR DATA
    private final RequestManager glide;
    private final String idCurrentUser;
    private List<Message> messageList;
    private String date;

    //FOR COMMUNICATION
    private Listener callback;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position, @NonNull @NotNull Message model) {
        Message message = this.getItem(position);
        Log.d("Adapter", message.getMessage());
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.own_chat_bubble, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.other_chat_bubble, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    public interface Listener {
        void onDataChanged();
    }

    public BoxChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options, RequestManager glide,
                          Listener callback, String idCurrentUser) {
        super(options);
        this.glide = glide;
        this.callback = callback;
        this.idCurrentUser = idCurrentUser;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }


    @Override
    public int getItemViewType(int position) {
        String userId = this.getItem(position).getUserSender().getUid();
        Log.d("Adapter", userId);
        Log.d("Adapter", FirebaseAuth.getInstance().getUid());
        if (userId.equals(FirebaseAuth.getInstance().getUid())) {
            Log.d("Adapter", "1");
            return VIEW_TYPE_MESSAGE_SENT;
        }
        Log.d("Adapter", "2");
        return VIEW_TYPE_MESSAGE_RECEIVED;
    }


    public class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, dateText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.ownMessage);
            timeText = (TextView) itemView.findViewById(R.id.ownTime);
            dateText = itemView.findViewById(R.id.ownDate);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        void bind(Message message) {
            messageText.setText(message.getMessage());
            // Fix null java.util.Date.getTime()
            // Format the stored timestamp into a readable String using method.
            DateFormat dateFormat = new SimpleDateFormat("hh:mm");
            String strDate = dateFormat.format(message.getDateCreated());
            timeText.setText(strDate);

            DateFormat newDateFormat = new SimpleDateFormat("MMM d");
            String newStrDate = newDateFormat.format(message.getDateCreated());
            if (newStrDate.equals(BoxChatAdapter.this.date)) {
                dateText.setVisibility(View.GONE);
            } else {
                dateText.setText(newStrDate);
                BoxChatAdapter.this.date = newStrDate;
            }
        }
    }

    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText, dateText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.otherMessage);
            timeText = (TextView) itemView.findViewById(R.id.otherTime);
            nameText = (TextView) itemView.findViewById(R.id.otherChatName);
            profileImage = (ImageView) itemView.findViewById(R.id.otherPicture);
            dateText = itemView.findViewById(R.id.otherChatDate);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        void bind(Message message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            DateFormat dateFormat = new SimpleDateFormat("hh:mm");
            String strDate = dateFormat.format(message.getDateCreated());
            timeText.setText(strDate);

            DateFormat newDateFormat = new SimpleDateFormat("MMM d");
            String newStrDate = newDateFormat.format(message.getDateCreated());
            if (newStrDate.equals(BoxChatAdapter.this.date)) {
                dateText.setVisibility(View.GONE);
            } else {
                dateText.setText(newStrDate);
                BoxChatAdapter.this.date = newStrDate;
            }
            nameText.setText(message.getUserSender().getUsername());

            // Insert the profile image from the URL into the ImageView.
            Picasso.get().load(message.getUrlImage()).into(profileImage);
        }
    }
}
