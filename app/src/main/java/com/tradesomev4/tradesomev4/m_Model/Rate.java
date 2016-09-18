package com.tradesomev4.tradesomev4.m_Model;

/**
 * Created by Pastillas-Boy on 9/15/2016.
 */
public class Rate {
    String id;
    float rate;

    public Rate() {
    }

    public Rate(String id, float rate) {
        this.id = id;
        this.rate = rate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
