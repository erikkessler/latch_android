package com.inspiredo.latch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains connection to the database. Allows for CRUD operations
 */
public class SeqDataSource {

    // Database fields
    private SQLiteDatabase database;
    private SeqSQLiteHelper dbHelper;
    private String[] allColumns = { SeqSQLiteHelper.COLUMN_ID,
            SeqSQLiteHelper.COLUMN_TITLE, SeqSQLiteHelper.COLUMN_REWARD };

    public SeqDataSource(Context context) {
        dbHelper = new SeqSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Sequence createSequence(Sequence seq) {
        ContentValues values = new ContentValues();
        values.put(SeqSQLiteHelper.COLUMN_TITLE, seq.getTitle());
        values.put(SeqSQLiteHelper.COLUMN_REWARD, seq.getReward());

        long insertId = database.insert(SeqSQLiteHelper.TABLE_SEQUENCES, null,
                values);

        Cursor cursor = database.query(SeqSQLiteHelper.TABLE_SEQUENCES,
                allColumns, SeqSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Sequence newComment = cursorToSeq(cursor);
        cursor.close();
        return newComment;
    }

    public List<Sequence> getAllSequences() {
        List<Sequence> sequences = new ArrayList<Sequence>();

        Cursor cursor = database.query(SeqSQLiteHelper.TABLE_SEQUENCES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Sequence comment = cursorToSeq(cursor);
            sequences.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return sequences;
    }

    private Sequence cursorToSeq(Cursor cursor) {
        Sequence sequence = new Sequence(cursor.getString(1));
        sequence.setId(cursor.getLong(0));
        sequence.setReward(cursor.getString(2));
        return sequence;
    }
}
