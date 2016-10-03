package com.tradesomev4.tradesomev4.m_Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pastillas-Boy on 7/11/2016.
 */
public class Auction {
    public String uid;
    public String auctionId;
    public String itemTitle;
    public int staringBid;
    public int currentBid;
    public String category;
    public String description;
    public String image1Uri;
    public String image2Uri;
    public String image3Uri;
    public String image4Uri;
    public String image1Name;
    public String image2Name;
    public String image3Name;
    public String image4Name;
    public String directoryName;
    public boolean status;
    public long postDate;
    private double latitude;
    private double longitude;
    private boolean hidden;

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public Auction() {
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Auction(String uid, String auctionId, String itemTitle, int staringBid, int currentBid, String category, String description,
                   String image1Uri, String image2Uri, String image3Uri, String image4Uri, String image1Name, String image2Name,
                   String image3Name, String image4Name, String directoryName, boolean status, long postDate, double latitude, double longitude, boolean hidden) {
        this.uid = uid;
        this.auctionId = auctionId;
        this.itemTitle = itemTitle;
        this.staringBid = staringBid;
        this.currentBid = currentBid;
        this.category = category;
        this.description = description;
        this.image1Uri = image1Uri;
        this.image2Uri = image2Uri;
        this.image3Uri = image3Uri;
        this.image4Uri = image4Uri;
        this.image1Name = image1Name;
        this.image2Name = image2Name;
        this.image3Name = image3Name;
        this.image4Name = image4Name;
        this.directoryName = directoryName;
        this.status = status;
        this.postDate = postDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hidden = hidden;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("uid", uid);
        result.put("auctionId", auctionId);
        result.put("itemTitle", itemTitle);
        result.put("staringBid", staringBid);
        result.put("currentBid", staringBid);
        result.put("category", category);
        result.put("description", description);
        result.put("image1Uri", image1Uri);
        result.put("image2Uri", image2Uri);
        result.put("image3Uri", image3Uri);
        result.put("image4Uri", image4Uri);
        result.put("image1Name", image1Name);
        result.put("image2Name", image2Name);
        result.put("image3Name", image3Name);
        result.put("image4Name", image4Name);
        result.put("directoryName", directoryName);
        result.put("status", status);
        result.put("postDate", postDate);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("hidden", hidden);

        return result;
    }

    public int getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(int currentBid) {
        this.currentBid = currentBid;
    }

    public long getPostDate() {
        return postDate;
    }

    public void setPostDate(long postDate) {
        this.postDate = postDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public int getStaringBid() {
        return staringBid;
    }

    public void setStaringBid(int staringBid) {
        this.staringBid = staringBid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage1Uri() {
        return image1Uri;
    }

    public void setImage1Uri(String image1Uri) {
        this.image1Uri = image1Uri;
    }

    public String getImage2Uri() {
        return image2Uri;
    }

    public void setImage2Uri(String image2Uri) {
        this.image2Uri = image2Uri;
    }

    public String getImage3Uri() {
        return image3Uri;
    }

    public void setImage3Uri(String image3Uri) {
        this.image3Uri = image3Uri;
    }

    public String getImage4Uri() {
        return image4Uri;
    }

    public void setImage4Uri(String image4Uri) {
        this.image4Uri = image4Uri;
    }

    public String getImage1Name() {
        return image1Name;
    }

    public void setImage1Name(String image1Name) {
        this.image1Name = image1Name;
    }

    public String getImage2Name() {
        return image2Name;
    }

    public void setImage2Name(String image2Name) {
        this.image2Name = image2Name;
    }

    public String getImage3Name() {
        return image3Name;
    }

    public void setImage3Name(String image3Name) {
        this.image3Name = image3Name;
    }

    public String getImage4Name() {
        return image4Name;
    }

    public void setImage4Name(String image4Name) {
        this.image4Name = image4Name;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
}
