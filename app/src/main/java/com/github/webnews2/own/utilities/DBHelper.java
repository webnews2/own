package com.github.webnews2.own.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class which contains all necessary methods for CRUD operations on the database of this application. It uses
 * the singleton pattern and requires a single call of the init method to function properly.
 *
 * @author Kevin Kleiber (m26675)
 * @version 1.0
 */
public class DBHelper extends SQLiteOpenHelper {

    // Tag for (logcat) information logging
    private static final String TAG = DBHelper.class.getSimpleName();

    // Represents the current version of the db schema, onUpgrade/onDowngrade will be executed if necessary
    private static final int DATABASE_VERSION = 1;

    // TODO: Make future-proof by backing up on version jump
    // Represents the db file of the app
    private static final String DATABASE_NAME = "own-" + DATABASE_VERSION + ".db";

    // Used to store the singleton once its created
    private static DBHelper instance;

    // Constants for query building
    private static final String TBL_TITLES = "tblTitles";
    private static final String TBL_TITLES_COL_ID = "id";
    private static final String TBL_TITLES_COL_NAME = "name";
    private static final String TBL_TITLES_COL_THUMBNAIL = "thumbnail";
    private static final String TBL_TITLES_COL_ON_WISH_LIST = "onWishList";
    private static final String TBL_TITLES_COL_LOCATION = "location";

    private static final String TBL_PLATFORMS = "tblPlatforms";
    private static final String TBL_PLATFORMS_COL_ID = "id";
    private static final String TBL_PLATFORMS_COL_NAME = "name";

    private static final String TBL_T_TBL_P = "tblTitles_tblPlatforms";
    private static final String TBL_T_TBL_P_COL_TITLE_ID = "titleID";
    private static final String TBL_T_TBL_P_COL_PLATFORM_ID = "platformID";

    // Private constructor as this class uses the singleton pattern
    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Call this method ones to initialize the DBHelper instance if it's not available.
     *
     * @param p_context context to use for locating paths to the database
     */
    public static void init(Context p_context) {
        if (instance == null) instance = new DBHelper(p_context);
    }

    /**
     * Returns the DBHelper instance which can then be used for calling methods for different database operations.
     *
     * @return instance of the DBHelper class for interacting with the database
     */
    public static DBHelper getInstance() {
        return instance;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // List for storing all necessary creation statements
        List<String> lsQueries = new ArrayList<>();

        // TODO: Further investigation for creation and update time necessary! Important for production system.
        // IDEA: Implement location management similar to Google Home
        // Add query for creating the game titles table which stores core information
        lsQueries.add(
            "CREATE TABLE IF NOT EXISTS " + TBL_TITLES + " ("
                + TBL_TITLES_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + TBL_TITLES_COL_NAME + " TEXT UNIQUE NOT NULL, "
                + TBL_TITLES_COL_THUMBNAIL + " TEXT, " // only contains the path
                + TBL_TITLES_COL_ON_WISH_LIST + " BOOLEAN DEFAULT FALSE NOT NULL, "
                + TBL_TITLES_COL_LOCATION + " TEXT"
                //+ "creationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "
                //+ "updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL"
            + ");"
        );

        // Add query for creating the platforms table which contains all possible platforms
        lsQueries.add(
            "CREATE TABLE IF NOT EXISTS " + TBL_PLATFORMS + " ("
                + TBL_PLATFORMS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + TBL_PLATFORMS_COL_NAME + " TEXT UNIQUE NOT NULL"
                //+ "creationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "
                //+ "updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL"
            + ");"
        );

        // Add query for creating the association table for game titles and platforms
        lsQueries.add(
            "CREATE TABLE IF NOT EXISTS " + TBL_T_TBL_P + " ("
                + TBL_T_TBL_P_COL_TITLE_ID + " INTEGER NOT NULL, "
                + TBL_T_TBL_P_COL_PLATFORM_ID + " INTEGER NOT NULL, "
                //+ "creationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "
                //+ "updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "
                + "FOREIGN KEY(" + TBL_T_TBL_P_COL_TITLE_ID + ") REFERENCES " + TBL_TITLES + "(" + TBL_TITLES_COL_ID + "), "
                + "FOREIGN KEY(" + TBL_T_TBL_P_COL_PLATFORM_ID + ") REFERENCES " + TBL_PLATFORMS + "(" + TBL_PLATFORMS_COL_ID + ")"
            + ");"
        );

        // Try to create the tables of the app
        try {
            for (int i = 0; i < lsQueries.size(); i++) {
                db.execSQL(lsQueries.get(i));
                Log.i(TAG, "{ojo} Query " + i + " was executed successfully.");
            }
        }
        catch (SQLException e) {
            Log.e(TAG, "{ojo} Something in onCreate went wrong.", e);
        }
        finally {
            Log.i(TAG, "{ojo} Database and table creation was successful.");
        }
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: Implement me!
    }

    /**
     * Inserts a game title into the database. The name and the onWishList attribute must be present.
     *
     * @param p_title title object containing information about the game title
     * @return id of the recently inserted game title or -1 if failed
     */
    public long addTitle(Title p_title) {
        // Get database with writing capabilities
        SQLiteDatabase db = getWritableDatabase();

        // Define set of values to insert
        ContentValues cv = new ContentValues();
        cv.put(TBL_TITLES_COL_NAME, p_title.getName());
        cv.put(TBL_TITLES_COL_THUMBNAIL, p_title.getThumbnail());
        cv.put(TBL_TITLES_COL_ON_WISH_LIST, p_title.isOnWishList());
        cv.put(TBL_TITLES_COL_LOCATION, p_title.getLocation());

        // Insert title and close db afterwards
        long result = db.insert(TBL_TITLES, null, cv);
        db.close();

        return result;
    }

    /**
     * Inserts a platform into the database. The name attribute must be present.
     *
     * @param p_platform platform object containing information about the platform
     * @return id of the recently inserted platform or -1 if failed
     */
    public long addPlatform(Platform p_platform) {
        // Get database with writing capabilities
        SQLiteDatabase db = getWritableDatabase();

        // Define set of values to insert
        ContentValues cv = new ContentValues();
        cv.put(TBL_PLATFORMS_COL_NAME, p_platform.getName());

        // Insert platform and close db afterwards
        long result = db.insert(TBL_PLATFORMS, null, cv);
        db.close();

        return result;
    }

    /**
     * Inserts title <> platform connections into the database.
     *
     * @param p_titleID id of title to connect with platforms of list
     * @param p_lsNewPlatforms list of new platforms to connect to title behind id
     * @return id of the last inserted connection or -1 if failed
     */
    public long connectTitleAndPlatforms(long p_titleID, List<Platform> p_lsNewPlatforms) {
        // Get database with writing capabilities
        SQLiteDatabase db = getWritableDatabase();
        // Init set for connection insertion
        ContentValues cv = new ContentValues();

        long result = -1;

        // TODO: Only add if title and platform are not already connected > requires platforms list of titles to be filled
        // Loop through each platform of provided list
        for (Platform p : p_lsNewPlatforms) {
            // Set values for title <> platform connection
            cv.put(TBL_T_TBL_P_COL_TITLE_ID, p_titleID);
            cv.put(TBL_T_TBL_P_COL_PLATFORM_ID, p.getID());

            // Insert connection, but stop when there's an error
            if ((result = db.insert(TBL_T_TBL_P, null, cv)) == -1) break;
        }

        db.close();

        return result;
    }

    /**
     * Returns a list which contains all titles for the wish list or the games list. To get the combined list of both
     * check the {@link DataHolder} class.
     *
     * @param p_onlyWishList true - if only wish list titles should be selected, false otherwise
     * @return a List of type Title containing only wish list or games list titles
     */
    public List<Title> getTitles(boolean p_onlyWishList) {
        // Init list which will contain return values
        List<Title> lsTitles = new ArrayList<>();
        // Define query for selecting titles of both the wish and games list
        String query = "SELECT * FROM " + TBL_TITLES + " WHERE " + TBL_TITLES_COL_ON_WISH_LIST + "= ?";
        // Get database with reading capabilities
        SQLiteDatabase db = getReadableDatabase();
        // Execute query selecting only wish list or games list results and storing result set in cursor
        Cursor cursor = db.rawQuery(query, new String[]{p_onlyWishList ? "1" : "0"});

        // If cursor is not null
        if (cursor.moveToFirst()) {
            do {
                boolean onWishList = cursor.getInt(3) == 1;

                // Add each title of result set to result list
                lsTitles.add(new Title(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    onWishList,
                    cursor.getString(4)
                ));

                // TODO: Get platforms associated with current title and set current title's platforms list
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lsTitles;
    }

    /**
     * Returns a List with all platforms that were added to the db.
     *
     * @return a List of type Platform containing all available platforms
     */
    public List<Platform> getPlatforms() {
        // Init list which will contain return values
        List<Platform> lsPlatforms = new ArrayList<>();
        // Define query for selecting all available platforms
        String query = "SELECT * FROM " + TBL_PLATFORMS;
        // Get database with reading capabilities
        SQLiteDatabase db = getReadableDatabase();
        // Execute query and store result set in cursor
        Cursor cursor = db.rawQuery(query, null);

        // If cursor is not null
        if (cursor.moveToFirst()) {
            do {
                // Add each platform to platforms list
                lsPlatforms.add(new Platform(
                    cursor.getInt(0),
                    cursor.getString(1)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lsPlatforms;
    }

    /**
     * Deletes a single title from the database by first deleting all associations (if it's not a wish list title) and
     * second deleting the title itself.
     *
     * @param p_titleID id of title to delete
     * @param p_onWishList true - if title is on wish list, false otherwise
     * @return true - if title was successfully deleted, false otherwise
     */
    public boolean deleteTitle(int p_titleID, boolean p_onWishList) {
        // Get database with writing capabilities
        SQLiteDatabase db = getWritableDatabase();

        // If title is not on wish list > delete all associations of title with any platform
        if (!p_onWishList) db.delete(TBL_T_TBL_P, TBL_T_TBL_P_COL_TITLE_ID + "= ?",
                new String[]{String.valueOf(p_titleID)});

        // Delete title itself
        int count = db.delete(TBL_TITLES, TBL_TITLES_COL_ID + "= ?",
                new String[]{String.valueOf(p_titleID)});

        db.close();

        // Returns true if operation was successful (means one row affected)
        return count == 1;
    }

    /**
     * Deletes a single platform from the database by first deleting all associations with titles and second deleting
     * the platform itself.
     *
     * @param p_platformID id of platform to delete
     * @return true - if platform was successfully deleted, false otherwise
     */
    public boolean deletePlatform(int p_platformID) {
        // Get database with writing capabilities
        SQLiteDatabase db = getWritableDatabase();

        // Delete all associations of the platform with any title
        db.delete(TBL_T_TBL_P, TBL_T_TBL_P_COL_PLATFORM_ID + "= ?",
                new String[]{String.valueOf(p_platformID)});

        // Delete the platform itself
        int count = db.delete(TBL_PLATFORMS, TBL_PLATFORMS_COL_ID + "= ?",
                new String[]{String.valueOf(p_platformID)});

        db.close();

        // Returns true if operation was successful (means one row affected)
        return count == 1;
    }

    /**
     * Updates a single title using the provided title object to access the values for updating it.
     *
     * @param p_title title object with title id to update and updated values
     * @return true - if title was successfully updated, false otherwise
     */
    public boolean updateTitle(Title p_title) {
        // Get database with writing capabilities
        SQLiteDatabase db = getWritableDatabase();

        // Define set of values to update
        ContentValues cv = new ContentValues();
        cv.put(TBL_TITLES_COL_NAME, p_title.getName());
        cv.put(TBL_TITLES_COL_THUMBNAIL, p_title.getThumbnail());
        cv.put(TBL_TITLES_COL_ON_WISH_LIST, p_title.isOnWishList());
        cv.put(TBL_TITLES_COL_LOCATION, p_title.getLocation());

        // TODO: Implement update for associated platforms

        // Update title
        int count = db.update(TBL_TITLES, cv, TBL_TITLES_COL_ID + "= ?",
                new String[]{String.valueOf(p_title.getID())});

        db.close();

        // Returns true if operation was successful (means one row affected)
        return count == 1;
    }

    /**
     * Updates the name of a single platform.
     *
     * @param p_platformID ID of platform to update
     * @param p_platformName new platform name
     * @return true - if platform was successfully updated, false otherwise
     */
    public boolean updatePlatform(int p_platformID, String p_platformName) {
        // Get database with writing capabilities
        SQLiteDatabase db = getWritableDatabase();

        // Define set of values to update
        ContentValues cv = new ContentValues();
        cv.put(TBL_PLATFORMS_COL_NAME, p_platformName);

        // Update platform
        int count = db.update(TBL_PLATFORMS, cv, TBL_PLATFORMS_COL_ID + "= ?",
                new String[]{String.valueOf(p_platformID)});

        db.close();

        // Returns true if operation was successful (means one row affected)
        return count == 1;
    }
}
