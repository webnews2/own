package com.github.webnews2.own.utilities;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Data wrapper class which houses 4 types of lists and some methods to retrieve or update them. It uses the singleton
 * pattern and doesn't require any initial call to a specific method except for {@link #getInstance()}.
 *
 * @author Kevin Kleiber (m26675)
 * @version 1.0
 */
public class DataHolder {

    // Tag for (logcat) information logging
    private static final String TAG = DataHolder.class.getSimpleName();

    // Used to store object reference once its created
    private static DataHolder instance;

    // Used to store relevant data of the app
    private List<Title> lsTitles = new ArrayList<>();   // Stores owned titles and wish list titles (just for containment queries)
    private List<Title> lsGames;                        // Stores only owned titles
    private List<Title> lsWishList;                     // Stores only wish list titles
    private List<Platform> lsPlatforms;                 // Stores platforms, e.g. PlayStation 4, Steam, etc.

    /**
     * Returns the DataHolder instance which will be initialized if it's not available and can then be used to call
     * different methods to retrieve or update the data lists provided by this class.
     *
     * @return instance of DataHolder class
     */
    public static DataHolder getInstance() {
        if (instance == null) instance = new DataHolder();

        return instance;
    }

    /**
     * Returns a list containing all wish list and games list titles. Gets updated by calling
     * {@link #updateWishList(BaseAdapter)} and {@link #updateGames(BaseAdapter)}. This list should be used for
     * containment checks only.
     *
     * @return list containing all wish list and games list titles
     */
    public List<Title> getTitles() {
        if (lsTitles.size() == 0) {
            lsTitles.addAll(getGames());
            lsTitles.addAll(getWishList());
        }

        return lsTitles;
    }

    /**
     * Returns a list containing all games list titles provided by the corresponding DBHelper method. To update it call
     * {@link #updateGames(BaseAdapter)}.
     *
     * @return list containing all games list titles
     */
    public List<Title> getGames() {
        if (lsGames == null) lsGames = DBHelper.getInstance().getTitles(false);

        return lsGames;
    }

    /**
     * Returns a list containing all wish list titles provided by the corresponding DBHelper method. To update it call
     * {@link #updateWishList(BaseAdapter)}.
     *
     * @return list containing all wish list titles
     */
    public List<Title> getWishList() {
        if (lsWishList == null) lsWishList = DBHelper.getInstance().getTitles(true);

        return lsWishList;
    }

    /**
     * Returns a list containing all platforms provided by the corresponding DBHelper method. To update it call
     * {@link #updatePlatforms(BaseAdapter)}.
     *
     * @return list containing all platforms
     */
    public List<Platform> getPlatforms() {
        if (lsPlatforms == null) lsPlatforms = DBHelper.getInstance().getPlatforms();

        return lsPlatforms;
    }

    /**
     * Updates the games as well as the titles list. The adapter is used to update the corresponding views content.
     *
     * @param p_adapter adapter used to fill the corresponding view
     */
    public void updateGames(BaseAdapter p_adapter) {
        lsGames.clear();
        lsGames.addAll(DBHelper.getInstance().getTitles(false));

        lsTitles.clear();
        lsTitles.addAll(getTitles());

        p_adapter.notifyDataSetChanged();
    }

    /**
     * Updates the wish as well as the titles list. The adapter is used to update the corresponding views content.
     *
     * @param p_adapter adapter used to fill the corresponding view
     */
    public void updateWishList(BaseAdapter p_adapter) {
        lsWishList.clear();
        lsWishList.addAll(DBHelper.getInstance().getTitles(true));

        lsTitles.clear();
        lsTitles.addAll(getTitles());

        p_adapter.notifyDataSetChanged();
    }

    /**
     * Updates the platforms list. The adapter is used to update the corresponding views content.
     *
     * @param p_adapter adapter used to fill the corresponding view
     */
    public void updatePlatforms(BaseAdapter p_adapter) {
        lsPlatforms.clear();
        lsPlatforms.addAll(DBHelper.getInstance().getPlatforms());
        p_adapter.notifyDataSetChanged();
    }
}
