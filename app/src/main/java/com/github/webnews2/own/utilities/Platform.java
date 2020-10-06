package com.github.webnews2.own.utilities;

/**
 * Stores basic information about a platform like PlayStation 4, Nintendo Switch or Steam. Currently a platform only has
 * a id and a name. In future updates information like the username on the platform and other relevant information will
 * be available.
 *
 * @author Kevin Kleiber (m26675)
 * @version 1.0
 */
public class Platform {

    // Tag for (logcat) information logging
    private static final String TAG = Title.class.getSimpleName();

    // Declare fields for platform class
    private int id;
    private String name;

    /**
     * Creates a new platform object to store the id and the name of a platform.
     *
     * @param p_id id of the platform, mostly used for database operations
     * @param p_name name of the platform, e.g. Xbox One X, Xbox Series X, etc.
     */
    public Platform(int p_id, String p_name) {
        id = p_id;
        name = p_name;
    }

    /**
     * Simple get method which returns the id of the current platform object.
     *
     * @return id of current platform
     */
    public int getID() {
        return id;
    }

    /**
     * Simple get method which returns the name of the current platform object.
     *
     * @return name of current platform
     */
    public String getName() {
        return name;
    }

    /**
     * Simple method to set the id of the current platform object.
     *
     * @param p_id id to use for overriding current one
     */
    public void setID(int p_id) {
        id = p_id;
    }

    /**
     * Simple method to set the name of the current platform object.
     *
     * @param p_name name to use for overriding current one
     */
    public void setName(String p_name) {
        name = p_name;
    }
}
