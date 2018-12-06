package com.example.lachlanhuang.buskerbusker;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BuskerLocation {

    public String userId;
    public String username;
    public MyLatLng latLng = new MyLatLng(0.0,0.0);

    private String mDescription;
    private int mYear;
    private int mMonth;
    private int mDay;

    private int mHour;

    private int mMinute;

    private MarkerOptions mo = new MarkerOptions();

    //Constructor - will probs take more params later
    public BuskerLocation(String userId, String username, MyLatLng latLng, int year,
                          int month, int day, int hour, int minute, String description) {

        this.userId = userId;
        this.username = username;
        this.latLng = latLng;
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
        this.mHour = hour;
        this.mMinute = minute;
        this.mDescription = description;

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
        myRef.child("busker").child(userId).setValue(this);
    }

    public BuskerLocation() {

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


    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }

    public int getmMonth() {
        return mMonth;
    }

    public void setmMonth(int mMonth) {
        this.mMonth = mMonth;
    }

    public int getmDay() {
        return mDay;
    }

    public void setmDay(int mDay) {
        this.mDay = mDay;
    }

    public int getmHour() {
        return mHour;
    }

    public void setmHour(int mHour) {
        this.mHour = mHour;
    }

    public int getmMinute() {
        return mMinute;
    }

    public void setmMinute(int mMinute) {
        this.mMinute = mMinute;
    }

}


