package com.tradesomev4.tradesomev4.m_Model;

import java.util.HashMap;

/**
 * Created by Pastillas-Boy on 7/25/2016.
 */
public class AuctionHistory {
    String auctionId;
    String posterId;

    public AuctionHistory() {
    }

    public AuctionHistory(String posterId, String auctionId) {
        this.posterId = posterId;
        this.auctionId = auctionId;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();

        map.put("auctionId", auctionId);
        map.put("posterId", posterId);

        return map;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }
}
