package com.github.webnews2.own.utilities;

import java.util.List;

/**
 * Stores basic information about a game title, e.g. the name, the platforms and a thumbnail. It's used for displaying
 * the wish list and the games list.
 *
 * @author Kevin Kleiber (m26675)
 * @version 1.0
 */
public class Title {

    // Tag for (logcat) information logging
    private static final String TAG = Title.class.getSimpleName();

    // Declare fields for title class
    private int id;
    private String name;
    private String thumbnail;
    private boolean onWishList;
    private String location;
    private List<Platform> lsPlatforms;

    /**
     * Creates a new title object to store basic information like the location where the physical copy was seen last
     * time or if it should only be displayed on the wish list.
     *
     * @param p_id id of the title, mostly used for database operations
     * @param p_name name of the game title, e.g. Fallout 3
     * @param p_thumbnail uri of the chosen thumbnail as string object
     * @param p_onWishList whether the title should be displayed on the wish list or the games list
     * @param p_location used to store the most current location of the physical copy of a title
     */
    public Title(int p_id, String p_name, String p_thumbnail, boolean p_onWishList, String p_location) {
        id = p_id;
        name = p_name;
        thumbnail = p_thumbnail;
        onWishList = p_onWishList;
        location = p_location;
    }

    /**
     * Simple get method which returns the id of the current title object.
     *
     * @return id of current title
     */
    public int getID() {
        return id;
    }

    /**
     * Simple get method which returns the name of the current title object.
     *
     * @return name of current title
     */
    public String getName() {
        return name;
    }

    /**
     * Simple get method which returns the path of the current title object's thumbnail.
     *
     * @return thumbnail path of current title
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * Simple get method which returns a boolean to determine if the current title object should be shown on the
     * wish list or not.
     *
     * @return true - if title is on wish list, false otherwise
     */
    public boolean isOnWishList() {
        return onWishList;
    }

    /**
     * Simple get method which returns the location where the physical copy of the current title was last seen. Users
     * must provide information as there is no tracking system which comes with this app.
     *
     * @return most current location of the physical copy
     */
    public String getLocation() {
        return location;
    }

    /**
     * Simple get method which returns a list of platform objects the current title is associated with.
     *
     * @return list of platforms associated with current title
     */
    public List<Platform> getPlatforms() {
        return lsPlatforms;
    }

    /**
     * Simple method to set the id of the current title object.
     *
     * @param p_id id to use for overriding current one
     */
    public void setID(int p_id) {
        id = p_id;
    }

    /**
     * Simple method to set the name of the current title object.
     *
     * @param p_name name to use for overriding current one
     */
    public void setName(String p_name) {
        name = p_name;
    }

    /**
     * Simple method to set the path of the current title object's thumbnail.
     *
     * @param p_thumbnail thumbnail path to use for overriding current one
     */
    public void setThumbnail(String p_thumbnail) {
        thumbnail = p_thumbnail;
    }

    /**
     * Simple method to set boolean which determines whether the current title object should be shown on the wish list
     * or not.
     *
     * @param p_onWishList true - if title should be shown on wish list, false otherwise
     */
    public void setOnWishList(boolean p_onWishList) {
        onWishList = p_onWishList;
    }

    /**
     * Simple method to set the location of the current title object's physical copy. Users must provide information as
     * there is no tracking system which comes with this app.
     *
     * @param p_location most current location of physical copy for overriding current one
     */
    public void setLocation(String p_location) {
        location = p_location;
    }

    /**
     * Simple method to set the list of platform objects the current title object is associated with.
     *
     * @param p_lsPlatforms list of platform objects for overriding current one
     */
    public void setPlatforms(List<Platform> p_lsPlatforms) {
        lsPlatforms = p_lsPlatforms;
    }
}
