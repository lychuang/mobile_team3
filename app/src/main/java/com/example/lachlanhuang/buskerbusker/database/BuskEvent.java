package com.example.lachlanhuang.buskerbusker.database;

import com.example.lachlanhuang.buskerbusker.MyLatLng;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BuskEvent {


    public String userId;
    public String username;
    public MyLatLng latLng = new MyLatLng(0.0,0.0);

    private String mDescription;

    /**
    private int mYear;
    private int mMonth;
    private int mDay;

    private int mHour;

    private int mMinute;
**/

    private TimeDate startTime;
    private TimeDate endTime;
    private boolean live;

    private MarkerOptions mo = new MarkerOptions();



    //Constructor - will probs take more params later
    public BuskEvent(String userId, String username, MyLatLng latLng, TimeDate startTime,
                     TimeDate endTime, String description) {

        this.userId = userId;
        this.username = username;
        this.latLng = latLng;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mDescription = description;
        this.live = false;


        LatLng mapsLatLng = this.latLng.convertToMapsLatLng();

        mo.position(mapsLatLng);
        mo.title(username);
        if (!description.equals("")) {

            mo.snippet(description);
        } else {

            mo.snippet("");
        }


        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("buskEvent").child(userId).setValue(this);
    }

    public BuskEvent() {

    }

    public String getUserId() {

        return this.userId;
    }

    public String getUsername() {

        return this.username;
    }

    public MyLatLng getLatLng() {

        return this.latLng;
    }

    public void setUserId(String id) {

        this.userId = id;
    }

    public void setUsername(String name) {

        this.username = name;
    }

    public void setLatLng(MyLatLng latLng) {

        this.latLng = latLng;
    }


    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public TimeDate getStartTime() {
        return startTime;
    }

    public void setStartTime(TimeDate startTime) {
        this.startTime = startTime;
    }

    public TimeDate getEndTime() {
        return endTime;
    }

    public void setEndTime(TimeDate endTime) {
        this.endTime = endTime;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
}
