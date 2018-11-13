package com.example.lachlanhuang.buskerbusker.database;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String id;
    private String name;

    private boolean isBusker;
    private ArrayList<String> usualLocations;
    private float rating;

    private String about;
    private String email;

    public User(String id, String name, boolean isBusker, ArrayList<String> usualLocations, float rating, String about, String email) {
        this.id = id;
        this.name = name;
        this.isBusker = isBusker;
        this.usualLocations = usualLocations;
        this.rating = rating;
        this.about = about;
        this.email = email;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBusker() {
        return isBusker;
    }

    public void setBusker(boolean busker) {
        isBusker = busker;
    }

    public ArrayList<String> getUsualLocations() {
        return usualLocations;
    }

    public void setUsualLocations(ArrayList<String> usualLocations) {
        this.usualLocations = usualLocations;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isBusker=" + isBusker +
                ", usualLocations=" + usualLocations +
                ", rating=" + rating +
                ", about='" + about + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
