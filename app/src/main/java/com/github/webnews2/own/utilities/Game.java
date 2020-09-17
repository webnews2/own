package com.github.webnews2.own.utilities;

import android.media.Image;

public class Game {

    // Tag for (logcat) information logging
    private static final String TAG = Game.class.getSimpleName();

    private int id;
    private String title;
    private Image thumbnail;
    private boolean onWishlist;
    private String location;

    public Game(String p_title) {
        title = p_title;
    }
}
