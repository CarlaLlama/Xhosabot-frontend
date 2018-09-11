package com.example.carla.xhosabot;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Message {
    private Date messageSendTime;
    private String messageText;
    private boolean messageFromBot;

    public Message(){
    }

    Message(String messageText, boolean messageFromBot) {
        this.messageText = messageText;
        // false if message is from user, true if from bot
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

    public Date getMessageSendTime() {
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

}
