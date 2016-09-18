package com.tradesomev4.tradesomev4.m_Model;

import java.util.HashMap;

/**
 * Created by Pastillas-Boy on 7/27/2016.
 */
public class ItemComplain {
    String reason;
    String detailedReason;
    String auctionId;
    String complainerId;
    String beingComplainedId;
    String key;
    String dateComplained;

    public ItemComplain() {
    }

    public ItemComplain(String reason, String detailedReason, String auctionId, String complainerId, String beingComplainedId, String key, String dateComplained) {
        this.reason = reason;
        this.detailedReason = detailedReason;
        this.auctionId = auctionId;
        this.complainerId = complainerId;
        this.beingComplainedId = beingComplainedId;
        this.key = key;
        this.dateComplained = dateComplained;
    }

    public HashMap<String, Object>toMap(){
        HashMap<String, Object> map = new HashMap<>();

        map.put("reason", reason);
        map.put("detailedReason", detailedReason);
        map.put("auctionId", auctionId);
        map.put("complainerId", complainerId);
        map.put("beingComplainedId", beingComplainedId);
        map.put("key", key);
        map.put("dateComplained", dateComplained);

        return map;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDetailedReason() {
        return detailedReason;
    }

    public void setDetailedReason(String detailedReason) {
        this.detailedReason = detailedReason;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getComplainerId() {
        return complainerId;
    }

    public void setComplainerId(String complainerId) {
        this.complainerId = complainerId;
    }

    public String getBeingComplainedId() {
        return beingComplainedId;
    }

    public void setBeingComplainedId(String beingComplainedId) {
        this.beingComplainedId = beingComplainedId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDateComplained() {
        return dateComplained;
    }

    public void setDateComplained(String dateComplained) {
        this.dateComplained = dateComplained;
    }
}
