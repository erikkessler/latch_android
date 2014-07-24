package com.inspiredo.latch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper for storing Sequences, Steps, and Triggers in a SQLiteDB
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "latch.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_SEQUENCES = "sequences";
    public static final String TABLE_STEPS = "steps";
    public static final String TABLE_TRIGGERS = "triggers";

    // Shared column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";

    // Sequence column names
    public static final String COLUMN_REWARD = "reward";

    // Step column names
    public static final String COLUMN_COMPLETE = "complete";
    public static final String COLUMN_SEQ = "seq_id";

    // Trigger column names
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TYPE = "type";

    // Table creation strings
    private static final String CREATE_TABLE_SEQUENCES = "create table "
            + TABLE_SEQUENCES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TITLE
            + " text not null, " + COLUMN_REWARD
            + " text);";

    private static final String CREATE_TABLE_STEPS = "create table "
            + TABLE_STEPS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TITLE
            + " text not null, " + COLUMN_COMPLETE
            + " integer, " + COLUMN_SEQ + " integer);";

    private static final String CREATE_TABLE_TRIGGERS = "create table "
            + TABLE_TRIGGERS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TIME
            + " datetime, " + COLUMN_TYPE
            + " integer);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create the tables
        db.execSQL(CREATE_TABLE_SEQUENCES);
        db.execSQL(CREATE_TABLE_STEPS);
        db.execSQL(CREATE_TABLE_TRIGGERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // On upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEQUENCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIGGERS);

        // Create new tables
        onCreate(db);
    }
}
