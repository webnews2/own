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
     * Initializes the DBHelper instance if it's not available.
     *
     * @param p_context context to use for locating paths to the database
     */
    public static void init(Context p_context) {
        if (instance == null) instance = new DBHelper(p_context);
    }

    /**
     * Returns the DBHelper instance.
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
            Log.i(TAG, "{ojo} Something in onCreate went wrong.", e);
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
     * @return true - if the game title was successfully added to the db otherwise false
     */
    public long addTitle(Title p_title) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TBL_TITLES_COL_NAME, p_title.getName());
        cv.put(TBL_TITLES_COL_THUMBNAIL, p_title.getThumbnail());
        cv.put(TBL_TITLES_COL_ON_WISH_LIST, p_title.isOnWishList());
        cv.put(TBL_TITLES_COL_LOCATION, p_title.getLocation());

        long result = db.insert(TBL_TITLES, null, cv);
        db.close();

        return result;
    }

    /**
     * Inserts a platform into the database. The name attribute must be present.
     *
     * @param p_platform platform object containing information about the platform
     * @return true - if the platform was successfully added to the db otherwise false
     */
    public long addPlatform(Platform p_platform) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TBL_PLATFORMS_COL_NAME, p_platform.getName());

        long result = db.insert(TBL_PLATFORMS, null, cv);
        db.close();

        return result;
    }

    public long connectTitleAndPlatforms(long p_titleID, List<Platform> p_lsPlatforms) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        long result = -1;

        // TODO: Only add if title and platform are not already connected
        //
        for (Platform p : p_lsPlatforms) {
            cv.put(TBL_T_TBL_P_COL_TITLE_ID, p_titleID);
            cv.put(TBL_T_TBL_P_COL_PLATFORM_ID, p.getId());

            // Insert connection, but stop when there's an error
            if ((result = db.insert(TBL_T_TBL_P, null, cv)) == -1) break;
        }

        db.close();

        return result;
    }

    public List<Title> getTitles(boolean p_onlyWishList) {
        List<Title> lsTitles = new ArrayList<>();

        String query = "SELECT * FROM " + TBL_TITLES + " WHERE " + TBL_TITLES_COL_ON_WISH_LIST + "= ?";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{p_onlyWishList ? "1" : "0"});

        // If cursor is not null
        if (cursor.moveToFirst()) {
            do {
                boolean onWishList = cursor.getInt(3) == 1;

                lsTitles.add(new Title(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    onWishList,
                    cursor.getString(4)
                ));
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
        List<Platform> lsPlatforms = new ArrayList<>();

        String query = "SELECT * FROM " + TBL_PLATFORMS;

        SQLiteDatabase db = getReadableDatabase();
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
     * @param p_titleID ID of title to delete
     * @param p_onWishList set to true if title is on wish list, false otherwise
     * @return true - if title was successfully deleted (should only affect one row), false otherwise
     */
    public boolean deleteTitle(int p_titleID, boolean p_onWishList) {
        // Get database with writing capabilities
        SQLiteDatabase db = getWritableDatabase();

        // If title is not on wish list > delete all associations of the title with any platform
        if (!p_onWishList) db.delete(TBL_T_TBL_P, TBL_T_TBL_P_COL_TITLE_ID + "= ?",
                new String[]{String.valueOf(p_titleID)});

        // Delete the title itself
        int count = db.delete(TBL_TITLES, TBL_TITLES_COL_ID + "= ?",
                new String[]{String.valueOf(p_titleID)});

        db.close();

        // Returns true if operation was successful (means one row affected)
        return count == 1;
    }

    /**
     * Deletes a single platform from the database by first deleting all associations and second deleting the platform
     * itself.
     *
     * @param p_platformID ID of platform to delete
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

    public boolean updateTitle(Title p_title) {
        // Get database with writing capabilities
        SQLiteDatabase db = getWritableDatabase();

        // Define set of values to update
        ContentValues cv = new ContentValues();
        cv.put(TBL_TITLES_COL_NAME, p_title.getName());
        cv.put(TBL_TITLES_COL_THUMBNAIL, p_title.getThumbnail());
        cv.put(TBL_TITLES_COL_ON_WISH_LIST, p_title.isOnWishList());
        cv.put(TBL_TITLES_COL_LOCATION, p_title.getLocation());

        // TODO: Implement platforms update

        // Update title
        int count = db.update(TBL_TITLES, cv, TBL_TITLES_COL_ID + "= ?",
                new String[]{String.valueOf(p_title.getId())});

        db.close();

        // Returns true if operation was successful
        return count == 1;
    }

    /**
     * Updates the name of a single platform.
     *
     * @param p_platformID ID of platform to update
     * @param p_platformName new platform name
     * @return true - if platform was successfully updated (should only affect one row), false otherwise
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
