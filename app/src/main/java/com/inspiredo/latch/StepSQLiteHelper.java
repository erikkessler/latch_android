package com.inspiredo.latch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper for storing Sequences in a SQLiteDB
 */
public class StepSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_STEPS = "steps";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_COMPLETE = "complete";
    public static final String COLUMN_SEQ = "seq_id";

    private static final String DATABASE_NAME = "steps.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_STEPS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TITLE
            + " text not null, " + COLUMN_COMPLETE
            + " integer, " + COLUMN_SEQ + " integer);";

    public StepSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(StepSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + " which will destroy all old data"
        );

        db.execSQL(DATABASE_CREATE);
        onCreate(db);
    }
}
