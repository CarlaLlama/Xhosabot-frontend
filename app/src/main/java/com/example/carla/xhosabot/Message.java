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

    public Message(String messageText, boolean messageFromBot) {
        this.messageText = messageText;
        // false if message is from user, true if from bot
        this.messageFromBot = messageFromBot;


        Long timeLong = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH);
        sdf.setTimeZone(tz);//set time zone.
        String localTime = sdf.format(new Date(timeLong));
        Date date = new Date();

        try {
            messageSendTime = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.messageSendTime = messageSendTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isMessageFromBot() {
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
                + "fromZelda: " + messageFromBot + "\n"
                + "sendTime: " + messageSendTime + "\n}";
    }

}
