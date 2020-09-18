package com.github.webnews2.own.utilities;

import android.media.Image;

public class Title {

    // Tag for (logcat) information logging
    private static final String TAG = Title.class.getSimpleName();

    private int id;
    private String name;
    private Image thumbnail;
    private boolean onWishList;
    private String location;

    public Title(String p_name, Image p_thumbnail, boolean p_onWishList, String p_location) {
        name = p_name;
        thumbnail = p_thumbnail;
        onWishList = p_onWishList;
        location = p_location;
    }

    // TODO: Check getters/setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isOnWishList() {
        return onWishList;
    }

    public void setOnWishList(boolean onWishList) {
        this.onWishList = onWishList;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
