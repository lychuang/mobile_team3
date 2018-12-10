package com.example.lachlanhuang.buskerbusker.database;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String uid;
    public String name;
    public String buskerName;
    public String textDesc;


    public Post() {
        // Default constructor required
    }

    public Post(String uid, String name, String buskerName, String textDesc) {
        this.uid = uid;
        this.name = name;
        this.buskerName = buskerName;
        this.textDesc = textDesc;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("title", buskerName);
        result.put("body", textDesc);

        return result;
    }


}
