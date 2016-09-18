package com.tradesomev4.tradesomev4.m_Model;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Pastillas-Boy on 7/19/2016.
 */
public class UserMessage {
    String message;
    String sendDate;
    String senderId;
    String key;
    boolean read;

    public UserMessage() {
    }

    public UserMessage(String message, String sendDate, String senderId, String key, boolean read) {
        this.message = message;
        this.sendDate = sendDate;
        this.senderId = senderId;
        this.key = key;
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();

        Calendar now = Calendar.getInstance();

        map.put("message", message);
        map.put("sendDate", sendDate);
        map.put("senderId", senderId);
        map.put("key", key);
        map.put("read", read);

        return map;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
