package com.example.carla.xhosabot;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

class MessageViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = MessageViewHolder.class.getSimpleName();

    private TextView messageText;

    MessageViewHolder(View view) {
        super(view);
        messageText = view.findViewById(R.id.message_text);
    }

    void bindData(final Message message) {
        try {
            if (message.isMessageFromBot()){
                messageText.setGravity(Gravity.START);
                messageText.setPadding(0, 0, 16, 0);
            }
            else {
                messageText.setGravity(Gravity.END);
                messageText.setPadding(16, 0, 0, 0);
            }
            messageText.setText(message.getMessageText());
        }
        catch (NullPointerException e){
            Log.e(TAG, "Message bind error: " + e.getMessage());
        }
    }
}
