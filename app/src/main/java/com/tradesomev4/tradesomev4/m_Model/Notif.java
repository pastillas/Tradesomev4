package com.tradesomev4.tradesomev4.m_Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pastillas-Boy on 7/17/2016.
 */
public class Notif {
    String toUserId;
    String content;
    String date;
    String notifType;

    public Notif(String toUserId, String content, String date, String notifType) {
        this.toUserId = toUserId;
        this.content = content;
        this.date = date;
        this.notifType = notifType;
    }

    public String getNotifType() {
        return notifType;
    }

    public void setNotifType(String notifType) {
        this.notifType = notifType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap <String, Object> result = new HashMap<>();

        result.put("toUserId", toUserId);
        result.put("content", content);
        result.put("date", date);
        result.put("notifType", notifType);

        return  result;
    }

    public Notif() {
    }
    public Notif(String toUserId, String content) {
        this.toUserId = toUserId;
        this.content = content;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
