package com.example.lachlanhuang.buskerbusker;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BuskerLocation {

    private String user_id;
    private String username;
    private LatLng latLng = new LatLng(0, 0);


    private MarkerOptions mo = new MarkerOptions();

    //Constructor - will probs take more params later
    public BuskerLocation(String user_id, String username, LatLng latLng) {

        this.user_id = user_id;
        this.username = username;
        this.latLng = latLng;

        mo.position(latLng);
        mo.title(username);


        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("busker").child(user_id).setValue(this);
    }

    public BuskerLocation () {

    }

    public String getBuskerLocId() {

        return this.user_id;
    }

    public String getBuskerLocName() {

        return this.username;
    }

    public LatLng getBuskerLocLatLng() {

        return this.latLng;
    }

    public void setBuskerLocId(String id) {

        this.user_id = id;
    }

    public void setBuskerLocName(String name) {

        this.username = name;
    }

    public void setBuskerLocLatLng(LatLng latLng) {

        this.latLng = latLng;
    }



}


