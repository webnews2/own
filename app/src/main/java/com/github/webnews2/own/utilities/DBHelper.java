package com.github.webnews2.own.utilities;

import android.content.ContentValues;
import android.content.Context;
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


    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Returns the DBHelper instance and if this one is not available it gets created once.
     *
     * @param p_context context to use for locating paths to the database
     * @return an instance of the DBHelper class for interacting with the database
     */
    public static DBHelper getInstance(Context p_context) {
        if (instance == null) instance = new DBHelper(p_context);

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
                + TBL_TITLES_COL_NAME + " VARCHAR(255) NOT NULL, "
                + TBL_TITLES_COL_THUMBNAIL + " BLOB, "
                + TBL_TITLES_COL_ON_WISH_LIST + " BOOLEAN DEFAULT FALSE NOT NULL, "
                + TBL_TITLES_COL_LOCATION + " VARCHAR(255)"
                //+ "creationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "
                //+ "updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL"
            + ");"
        );

        // Add query for creating the platforms table which contains all possible platforms
        lsQueries.add(
            "CREATE TABLE IF NOT EXISTS " + TBL_PLATFORMS + " ("
                + TBL_PLATFORMS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + TBL_PLATFORMS_COL_NAME + " VARCHAR(255) NOT NULL"
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


    public boolean addGame(Title p_title) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TBL_TITLES_COL_NAME, p_title.getName());
        //cv.put(TBL_TITLES_COL_THUMBNAIL, null);
        cv.put(TBL_TITLES_COL_ON_WISH_LIST, p_title.isOnWishList());
        //cv.put(TBL_TITLES_COL_LOCATION, p_game.getLocation());

        return db.insert(TBL_TITLES, null, cv) != -1;
    }

    public boolean addPlatform(Platform p_platform) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TBL_PLATFORMS_COL_NAME, p_platform.getName());

        return db.insert(TBL_PLATFORMS, null, cv) != -1;
    }
}
