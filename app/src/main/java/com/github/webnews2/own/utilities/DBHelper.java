package com.github.webnews2.own.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {

    // Tag for (logcat) information logging
    private static final String TAG = DBHelper.class.getSimpleName();

    // Represents the current version of the db schema, onUpgrade/onDowngrade will be executed if necessary
    private static final int DATABASE_VERSION = 1;

    // TODO: Make future-proof by backing up on version jump
    // Represents the db file for the app
    private static final String DATABASE_NAME = "own-" + DATABASE_VERSION + ".db";

    private static DBHelper instance;

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

        // TODO: Further investigation for creation and update time necessary!
        // IDEA: Implement location management similar to Google Home
        // Add query for creating the games table which stores core information
        lsQueries.add(
            "CREATE TABLE IF NOT EXISTS tblGames ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "title VARCHAR(255) NOT NULL, "
                + "thumbnail BLOB, "
                + "onWishlist BOOLEAN DEFAULT FALSE NOT NULL, "
                + "location VARCHAR(255), "
                + "creationTime TIMESTAMP DEFAULT ... NOT NULL, "
                + "updateTime TIMESTAMP DEFAULT ... NOT NULL"
            + ");"
        );

        // Add query for creating the platforms table which contains all possible platforms
        lsQueries.add(
            "CREATE TABLE IF NOT EXISTS tblPlatforms ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "name VARCHAR(255) NOT NULL, "
                + "creationTime TIMESTAMP DEFAULT ... NOT NULL, "
                + "updateTime TIMESTAMP DEFAULT ... NOT NULL"
            + ");"
        );

        // Add query for creating the association table for games and platforms
        lsQueries.add(
            "CREATE TABLE IF NOT EXISTS tblGames_tblPlatforms ("
                + "titleID INTEGER NOT NULL, "
                + "platformID INTEGER NOT NULL, "
                + "creationTime TIMESTAMP DEFAULT ... NOT NULL, "
                + "updateTime TIMESTAMP DEFAULT ... NOT NULL, "
                + "FOREIGN KEY(titleID) REFERENCES tblGames(id), "
                + "FOREIGN KEY(platformID) REFERENCES tblPlatforms(id)"
            + ");"
        );

        // TODO: Continue here!
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
}
