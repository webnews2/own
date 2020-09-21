package com.github.webnews2.own.utilities;

import android.media.Image;

import java.util.ArrayList;

public class Title {

    // Tag for (logcat) information logging
    private static final String TAG = Title.class.getSimpleName();

    private int id;
    private String name;
    private String thumbnail;
    private boolean onWishList;
    private String location;
    private ArrayList<Platform> lsPlatforms;

    public Title(String p_name, String p_thumbnail, boolean p_onWishList, String p_location) {
        name = p_name;
        thumbnail = p_thumbnail;
        onWishList = p_onWishList;
        location = p_location;
    }

    // TODO: Check getters/setters
    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public boolean isOnWishList() {
        return onWishList;
    }

    public String getLocation() {
        return location;
    }

    public ArrayList<Platform> getPlatforms() {
        return lsPlatforms;
    }

    public void setName(String p_name) {
        name = p_name;
    }

    public void setThumbnail(String p_thumbnail) {
        thumbnail = p_thumbnail;
    }

    public void setOnWishList(boolean p_onWishList) {
        onWishList = p_onWishList;
    }

    public void setLocation(String p_location) {
        location = p_location;
    }

    public void setPlatforms(ArrayList<Platform> p_lsPlatforms) {
        lsPlatforms = p_lsPlatforms;
    }

    // TODO: Implement method to get path of image for db ops
}
