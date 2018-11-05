package com.example.lachlanhuang.buskerbusker;

import com.google.android.gms.maps.model.LatLng;

public class MyLatLng {
    private double latitude;
    private double longitude;

    public MyLatLng(Double latitude, Double longitude) {

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MyLatLng() {}

    public Double getLatitude() {

        return this.latitude;
    }

    public Double getLongitude() {

        return this.longitude;
    }


    public LatLng convertToMapsLatLng() {

        com.google.android.gms.maps.model.LatLng mapsLatLng =
                new com.google.android.gms.maps.model.LatLng(this.getLatitude(),
                        this.getLongitude());
        return mapsLatLng;
    }
}
