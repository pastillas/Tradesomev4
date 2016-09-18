package com.tradesomev4.tradesomev4.m_Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pastillas-Boy on 7/16/2016.
 */
public class Bid {
    String auctionId;
    String userId;
    String title;
    long bidDate;
    String id;

    public Bid() {
    }

    public long getBidDate() {
        return bidDate;
    }

    public void setBidDate(long bidDate) {
        this.bidDate = bidDate;
    }

    public Bid(String auctionId, String userId, String title, long bidDate, String id) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.title = title;
        this.bidDate = bidDate;
        this.id = id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("auctionId", auctionId);
        result.put("userId", userId);
        result.put("title", title);
        result.put("bidDate", bidDate);
        result.put("id", id);

        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
