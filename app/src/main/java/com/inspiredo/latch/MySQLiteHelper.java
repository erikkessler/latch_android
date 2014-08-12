package com.inspiredo.latch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper for storing Sequences, Steps, and Triggers in a SQLiteDB
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "latch.db";
    private static final int DATABASE_VERSION = 2;

    // Table names
    public static final String TABLE_SEQUENCES = "sequences";
    public static final String TABLE_STEPS = "steps";
    public static final String TABLE_TRIGGERS = "triggers";

    // Shared column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_SEQ = "seq_id";

    // Sequence column names
    public static final String COLUMN_REWARD = "reward";
    public static final String COLUMN_POS = "pos";

    // Step column names
    public static final String COLUMN_COMPLETE = "complete";

    // Trigger column names
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TYPE = "type";

    // Table creation strings
    private static final String CREATE_TABLE_SEQUENCES = "create table "
            + TABLE_SEQUENCES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TITLE
            + " text not null, " + COLUMN_REWARD
            + " text, " + COLUMN_POS
            + " integer);";

    private static final String CREATE_TABLE_STEPS = "create table "
            + TABLE_STEPS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TITLE
            + " text not null, " + COLUMN_COMPLETE
            + " integer, " + COLUMN_SEQ + " integer);";

    private static final String CREATE_TABLE_TRIGGERS = "create table "
            + TABLE_TRIGGERS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TIME
            + " integer, " + COLUMN_TYPE
            + " integer, " + COLUMN_SEQ + " integer);";

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

        /*// On upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEQUENCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIGGERS);

        // Create new tables
        onCreate(db);*/


        switch (oldVersion) {
            case 1:
                Log.d("Database", "Upgrading from version 1");
                db.execSQL("ALTER TABLE " + TABLE_SEQUENCES + " ADD COLUMN "
                        + COLUMN_POS + " INTEGER");
                Cursor cursor = db.query(MySQLiteHelper.TABLE_SEQUENCES,
                        null, null, null, null, null, null);

                cursor.moveToFirst();
                int pos = 0;
                while (!cursor.isAfterLast()) {
                    ContentValues vals = new ContentValues();
                    vals.put(COLUMN_POS, pos);
                    db.update(TABLE_SEQUENCES, vals, COLUMN_ID + " = " + cursor.getLong(0), null);
                    Log.d("Database", "Entry " + cursor.getLong(0)
                            + " given order " + pos);
                    cursor.moveToNext();
                    pos++;

                }

                cursor.close();
                break;
        }

    }

}
