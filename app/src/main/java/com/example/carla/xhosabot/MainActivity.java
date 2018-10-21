package com.example.carla.xhosabot;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String MESSAGE_SEND_TIME = "messageSendTime";

    private FirebaseUtils mFirebaseUtils = new FirebaseUtils();
    private MessagesList mMessagesList;
    private MessagesListAdapter<Message> mMessagesListAdapter;
    private List<Message> mMessages;
    private EditText mMessageInput;
    private String mUid;

    private ImageLoader mImageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            mUid = user.getUid();
            Log.d(TAG, "MainActivity with User: " + mUid);

            bindView();
            displayChatMessages();

            findViewById(R.id.send_button).setOnClickListener(view -> {
                if(!mMessageInput.getText().toString().equals("")) {
                    Message inputMessage = new Message(mMessageInput.getText().toString(), false);
                    inputMessage.setId(mUid);
                    inputMessage.setUser();
                    submitMessage(inputMessage);
                    mMessageInput.setText("");
                }else{
                    Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Log.d(TAG, "No user signed in. Open LoginActivity");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }


    private void bindView() {
        Log.d(TAG, "Binding main view links");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.doctor_inv);
        actionBar.setTitle(R.string.xhosa_bot);
        actionBar.setSubtitle(R.string.available);

        mMessageInput = findViewById(R.id.message_input);
        mMessagesList = findViewById(R.id.messages_list);

        mImageLoader = (ImageView imageView, String url, Object payload) -> {
            if (url != null && url.equalsIgnoreCase("Bot")) {
                Picasso.get().load(R.drawable.doctor).into(imageView);
            } else if (url != null && url.equalsIgnoreCase("gif")) {
                Picasso.get().load(R.drawable.dots).into(imageView);
            }
        };

        mMessagesListAdapter = new MessagesListAdapter<>(mUid, mImageLoader);
    }


    private void displayChatMessages(){
        mFirebaseUtils.getFirestore()
                .collection(mUid)
                .orderBy(MESSAGE_SEND_TIME, Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }
                    Message message = null;
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "New message: " + dc.getDocument().getData());
                                message =  dc.getDocument().toObject(Message.class);
                                message.setId(mUid);
                                message.setUser();
                                mMessagesListAdapter.addToStart(message, true);
                                mMessagesList.setAdapter(mMessagesListAdapter);
                                break;
                            default:
                                return;
                        }
                    }
                    if(null!=message && message.isMessageFromBot()){
                        removeWaitForBotMessage();
                    }else {
                        waitForBotMessage();
                    }

                });
    }

    public void submitMessage(Message message){
        Log.d(TAG, "Submitting message: " + message.getMessageText());
        mFirebaseUtils.getFirestore()
                .collection(mUid)
                .add(message);
    }

    public Message waitForBotMessage(){
        Message typingMessage = new Message("", true);
        typingMessage.setId("WAIT_MESSAGE");
        typingMessage.setUser();

        typingMessage.setTypingImage();
        Log.d(TAG, "MADE NEW WAIT MESSAGE");
        mMessagesListAdapter.addToStart(typingMessage, true);
        return typingMessage;
    }

    public void removeWaitForBotMessage(){
        mMessagesListAdapter.deleteById("WAIT_MESSAGE");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "logout");
                finish();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        intent.putExtra("USER", mUid);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
