package com.tradesomev4.tradesomev4.m_Model;

import java.util.HashMap;


public class Follower {
    String key;
    String id;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Follower() {
    }

    public Follower(String key, String id, String name) {
        this.key = key;
        this.id = id;
        this.name = name;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();

        map.put("key", key);
        map.put("id", id);
        map.put("name", name);

        return map;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
