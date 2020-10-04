package com.github.webnews2.own.utilities;

import android.widget.BaseAdapter;

import java.util.List;

public class DataHolder {

    // Tag for (logcat) information logging
    private static final String TAG = DataHolder.class.getSimpleName();

    // Used to store object reference once its created
    private static DataHolder instance;

    // Used to store relevant data of the app
    private List<Title> lsGames;            // Stores only owned titles
    private List<Title> lsWishList;         // Stores only wish list titles
    private List<Platform> lsPlatforms;     // Stores platforms, e.g. PlayStation 4, Steam, etc.

    public static DataHolder getInstance() {
        if (instance == null) instance = new DataHolder();

        return instance;
    }

    public List<Title> getGames() {
        if (lsGames == null) lsGames = DBHelper.getInstance().getTitles(false);

        return lsGames;
    }

    public List<Title> getWishList() {
        if (lsWishList == null) lsWishList = DBHelper.getInstance().getTitles(true);

        return lsWishList;
    }

    public List<Platform> getPlatforms() {
        if (lsPlatforms == null) lsPlatforms = DBHelper.getInstance().getPlatforms();

        return lsPlatforms;
    }

    public void updateGames(BaseAdapter p_adapter) {
        lsGames.clear();
        lsGames.addAll(DBHelper.getInstance().getTitles(false));
        p_adapter.notifyDataSetChanged();
    }

    public void updateWishList(BaseAdapter p_adapter) {
        lsWishList.clear();
        lsWishList.addAll(DBHelper.getInstance().getTitles(true));
        p_adapter.notifyDataSetChanged();
    }

    public void updatePlatforms(BaseAdapter p_adapter) {
        lsPlatforms.clear();
        lsPlatforms.addAll(DBHelper.getInstance().getPlatforms());
        p_adapter.notifyDataSetChanged();
    }
}
