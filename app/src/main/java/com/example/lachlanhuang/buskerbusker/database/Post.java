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
    public String buskerLocation;
    public String textDesc;


    public Post() {
        // Default constructor required
    }

    public Post(String uid, String name, String buskerName, String buskerLocation, String textDesc) {
        this.uid = uid;
        this.name = name;
        this.buskerName = buskerName;
        this.buskerLocation = buskerLocation;
        this.textDesc = textDesc;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("busker-name", buskerName);
        result.put("busker-location", buskerLocation);
        result.put("textDesc", textDesc);

        return result;
    }


}
