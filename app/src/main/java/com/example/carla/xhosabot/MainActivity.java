package com.example.carla.xhosabot;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //auth

        Log.d(TAG, "MainActivity with User: ");


        displayChatMessages();

        findViewById(R.id.send_button).setOnClickListener(view -> {
            submitMessage(mUserState.getId(),
                    new Message(mMessageInput.getText().toString(), false) );
            mMessageInput.setText("");
        });
    }


    private void displayChatMessages(){
        Query query = mFirebaseUtil.getFirestore()
                .collection(MESSAGES)
                .document(mUserState.getId())
                .collection(MESSAGES)
                .orderBy(MESSAGE_SEND_TIME, Query.Direction.ASCENDING)
                .limit(50);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        if(null != mMessageAdapter){
            mMessageAdapter.stopListening();
        }

        mMessageAdapter = new FirestoreRecyclerAdapter<Message, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message message) {
                holder.bindData(message);

            }

            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);
                return new MessageViewHolder(itemView);
            }

            @NonNull
            @Override
            public Message getItem(int position){
                return super.getItem(position);
            }
        };

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLayoutManager);
        mMessageRecyclerView.setAdapter(mMessageAdapter);
        mMessageRecyclerView.scrollToPosition(mMessageRecyclerView.getChildCount());
        mMessageAdapter.notifyDataSetChanged();
        mMessageRecyclerView.setHasFixedSize(true);
        mMessageAdapter.startListening();

        Log.d(TAG, "User viewed chat, take away notification");
        mFirebaseUtil.getFirestore()
                .collection(MESSAGES)
                .document(mUserState.getId()).update(MESSAGE_NEW_RECEIVED, false);

        hidePDialog();
    }
}
