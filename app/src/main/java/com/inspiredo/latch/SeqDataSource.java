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
    private StepSQLiteHelper stepDbHelper;
    private SQLiteDatabase stepDatabase;
    private String[] allColumns = { SeqSQLiteHelper.COLUMN_ID,
            SeqSQLiteHelper.COLUMN_TITLE, SeqSQLiteHelper.COLUMN_REWARD };

    public SeqDataSource(Context context) {
        dbHelper = new SeqSQLiteHelper(context);
        stepDbHelper = new StepSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        stepDatabase = stepDbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        stepDbHelper.close();
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
        Sequence newSequence = cursorToSeq(cursor);
        cursor.close();
        return newSequence;
    }

    public List<Sequence> getAllSequences() {
        List<Sequence> sequences = new ArrayList<Sequence>();

        Cursor cursor = database.query(SeqSQLiteHelper.TABLE_SEQUENCES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Sequence sequence = cursorToSeq(cursor);
            sequence.setSteps(getSteps(sequence.getId()));
            sequences.add(sequence);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return sequences;
    }

    public Sequence getSequence(long id) {
        Cursor cursor = database.query(SeqSQLiteHelper.TABLE_SEQUENCES,
                null, StepSQLiteHelper.COLUMN_ID +"=?",
                new String[] {id +""}, null, null, null);

        Sequence sequence = null;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            sequence = cursorToSeq(cursor);
            cursor.moveToNext();
        }
        return sequence;
    }

    public List<Step> getSteps(long id) {
        List<Step> steps = new ArrayList<Step>();

        Cursor cursor = stepDatabase.query(StepSQLiteHelper.TABLE_STEPS,
                null, StepSQLiteHelper.COLUMN_SEQ +"=?",
                new String[] {id +""}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Step step = cursorToStep(cursor);
            steps.add(step);
            cursor.moveToNext();
        }

        cursor.close();
        return steps;
    }

    public void deleteSequence(Sequence sequence) {
        long id = sequence.getId();
        database.delete(SeqSQLiteHelper.TABLE_SEQUENCES,
                SeqSQLiteHelper.COLUMN_ID + " = " + id, null);
        stepDatabase.delete(StepSQLiteHelper.TABLE_STEPS,
                StepSQLiteHelper.COLUMN_SEQ + " = " + id, null);
    }

    private Sequence cursorToSeq(Cursor cursor) {
        Sequence sequence = new Sequence(cursor.getString(1));
        sequence.setId(cursor.getLong(0));
        sequence.setReward(cursor.getString(2));
        return sequence;
    }

    private Step cursorToStep(Cursor cursor) {
        Step step = new Step(cursor.getString(1));
        step.setId(cursor.getLong(0));
        step.setComplete(cursor.getInt(2) == 1);
        step.setSequenceId(cursor.getLong(3));
        return step;
    }
}
