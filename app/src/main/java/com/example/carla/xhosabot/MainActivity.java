package com.example.carla.xhosabot;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String USERS = "Users";
    public static final String MESSAGES = "Messages";
    public static final String MESSAGE_SEND_TIME = "Messages";
    private FirebaseUtils mFirebaseUtils = new FirebaseUtils();
    private FirestoreRecyclerAdapter<Message, MessageViewHolder> mMessageAdapter;
    private RecyclerView mMessageRecyclerView;
    private EditText mMessageInput;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUid = user.getUid();

        Log.d(TAG, "MainActivity with User: " + mUid);

        bindView();
        displayChatMessages();

        findViewById(R.id.send_button).setOnClickListener(view -> {
            submitMessage(mUid,
                    new Message(mMessageInput.getText().toString(), false));
            mMessageInput.setText("");
        });
    }

    private void bindView() {
        Log.d(TAG, "Binding main view links");

        mMessageInput = findViewById(R.id.message_input);
        mMessageRecyclerView = findViewById(R.id.messages_recycler_view);
    }



    private void displayChatMessages(){
        Query query = mFirebaseUtils.getFirestore()
                .collection(USERS)
                .document(mUid)
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

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLayoutManager);
        mMessageRecyclerView.setAdapter(mMessageAdapter);
        mMessageRecyclerView.scrollToPosition(mMessageRecyclerView.getChildCount());
        mMessageAdapter.notifyDataSetChanged();
        mMessageRecyclerView.setHasFixedSize(true);
        mMessageAdapter.startListening();
    }

    public void submitMessage(String userId, Message message){
        Log.d(TAG, "Submitting message: " + message.getMessageText());
        CollectionReference collectionReference = mFirebaseUtils.getFirestore()
                .collection(USERS)
                .document(userId)
                .collection(MESSAGES);

        collectionReference.add(message);
        mMessageRecyclerView.scrollToPosition(mMessageRecyclerView.getChildCount());
    }
}
