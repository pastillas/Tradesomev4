package com.tradesomev4.tradesomev4.m_Model;

/**
 * Created by Pastillas-Boy on 7/17/2016.
 */
public class Notif {
    String key;
    String auctionId;
    String posterId;
    String content;
    String bidderId;
    String type;
    boolean read;
    String date;
    boolean received;

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    //bid, auctioner, finishAuctioner, finishBidder, finishWinner, itemComplain, userComplain, message

    public Notif() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Notif(String key, String auctionId, String posterId, String content, String bidderId, String type, boolean read, String date) {
        this.key = key;
        this.auctionId = auctionId;
        this.posterId = posterId;
        this.content = content;
        this.bidderId = bidderId;
        this.type = type;
        this.read = read;
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getBidderId() {
        return bidderId;
    }

    public void setBidderId(String bidderId) {
        this.bidderId = bidderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
