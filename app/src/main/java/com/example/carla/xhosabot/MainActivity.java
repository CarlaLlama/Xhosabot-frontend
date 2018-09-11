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

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int RC_SIGN_IN = 1;
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

        if(null != user){
            mUid = user.getUid();
        } else {
            setUpAuthUILogin();
        }

        Log.d(TAG, "MainActivity with User: " + mUid);

        bindView();
        displayChatMessages();

        findViewById(R.id.send_button).setOnClickListener(view -> {
            submitMessage(mUid,
                    new Message(mMessageInput.getText().toString(), false));
            mMessageInput.setText("");
        });
    }

    private void setUpAuthUILogin() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("za").build(),
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        //.setLogo(R.drawable.login_icon)
                        //.setTheme(R.style.LoginTheme)
                        .build(),
                RC_SIGN_IN);

    }
    private void bindView() {
        Log.d(TAG, "Binding main view links");

        mMessageInput = findViewById(R.id.message_input);
        mMessageRecyclerView = findViewById(R.id.messages_recycler_view);
    }

    private void displayChatMessages(){
        if(null == mFirebaseUtils.getFirestore().collection(mUid)){
            // figure out how to do this new user check
            mFirebaseUtils.getFirestore()
                    .collection(mUid)
                    .add(new Message("Welcome Message!", true));
        }

        Query query = mFirebaseUtils.getFirestore()
                .collection(mUid)
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
        mFirebaseUtils.getFirestore()
                .collection(userId)
                .add(message);

        mMessageRecyclerView.scrollToPosition(mMessageRecyclerView.getChildCount());
    }
}
