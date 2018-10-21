package com.example.carla.xhosabot;


import android.support.annotation.Nullable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@IgnoreExtraProperties
public class Message implements IMessage, Serializable, MessageContentType.Image {
    public Date messageSendTime;
    public String messageText;
    public boolean messageFromBot;
    @Exclude String userId;
    @Exclude Author author;

    String typingImage;


    public Message(){
        typingImage = null;
    }


    Message(String messageText, boolean messageFromBot) {
        this.messageText = messageText;
        this.messageFromBot = messageFromBot;


        Long timeLong = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH);
        sdf.setTimeZone(tz);
        String localTime = sdf.format(new Date(timeLong));
        Date sendTime = new Date();
        try {
            sendTime = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.messageSendTime = sendTime;
    }

    String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    boolean isMessageFromBot() {
        return messageFromBot;
    }

    public void setMessageFromBot(boolean messageFromBot) {
        this.messageFromBot = messageFromBot;
    }

    private Date getMessageSendTime() {
        return messageSendTime;
    }

    public void setMessageSendTime(Date messageSendTime) {
        this.messageSendTime = messageSendTime;
    }

    @Override
    public String toString() {
        return "Message: " + "{\n" + messageText + "\n"
                + "fromBot: " + messageFromBot + "\n"
                + "sendTime: " + messageSendTime + "\n}";
    }

    @Exclude
    @Override
    public String getId() {
        if(this.userId.equals("WAIT_MESSAGE")) {
            return "WAIT_MESSAGE";
        }else if (messageFromBot){
            return "Bot";
        }else{
            return "User";
        }
    }

    @Exclude
    @Override
    public String getText() {
        return messageText;
    }

    @Exclude
    @Override
    public Author getUser() {
        return author;
    }

    @Exclude
    public void setUser(){
        String name;
        String avatar;
        if(messageFromBot){
            name = "Bot";
            avatar = "Bot";
        }else{
            name = "User";
            avatar = null;
        }
        this.author = new Author(userId, name, avatar);
    }

    @Exclude
    public void setId(String userId){
        if(!messageFromBot) {
            this.userId = userId;
        } else if (null != userId){
            this.userId = "Bot";
        }
    }

    @Exclude
    @Override
    public Date getCreatedAt() {
        return messageSendTime;
    }

    public void setTypingImage(){
        typingImage = "gif";
    }

    @Nullable
    @Override
    public String getImageUrl() {
        return typingImage;
    }
}
