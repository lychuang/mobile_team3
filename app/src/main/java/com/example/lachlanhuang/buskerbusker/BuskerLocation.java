package com.example.lachlanhuang.buskerbusker;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BuskerLocation {

    public String userId;
    public String username;
    public MyLatLng latLng = new MyLatLng(0.0,0.0);


    private MarkerOptions mo = new MarkerOptions();

    //Constructor - will probs take more params later
    public BuskerLocation(String userId, String username, MyLatLng latLng) {

        this.userId = userId;
        this.username = username;
        this.latLng = latLng;

        LatLng mapsLatLng = this.latLng.convertToMapsLatLng();

        mo.position(mapsLatLng);
        mo.title(username);

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



}


