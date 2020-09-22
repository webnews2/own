package com.github.webnews2.own.utilities;

import android.media.Image;

public class Platform {

    // Tag for (logcat) information logging
    private static final String TAG = Title.class.getSimpleName();

    private int id;
    private String name;

    public Platform(int p_id, String p_name) {
        id = p_id;
        name = p_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
