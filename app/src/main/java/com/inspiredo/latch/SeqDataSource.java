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
    private SQLiteDatabase      database;
    private SeqSQLiteHelper     dbHelper;
    private StepSQLiteHelper    stepDbHelper;
    private SQLiteDatabase      stepDatabase;
    private String[]            allColumns = { SeqSQLiteHelper.COLUMN_ID,
            SeqSQLiteHelper.COLUMN_TITLE, SeqSQLiteHelper.COLUMN_REWARD };

    // Constructor sets the helpers
    // Need both Sequences and Steps as steps have sequences
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

    /**
     * Creates a database entry for a passed sequence
     * @param seq Sequence to create for
     * @return The sequence
     */
    public Sequence createSequence(Sequence seq) {
        // Put the content into a ContentValue
        ContentValues values = new ContentValues();
        values.put(SeqSQLiteHelper.COLUMN_TITLE, seq.getTitle());
        values.put(SeqSQLiteHelper.COLUMN_REWARD, seq.getReward());

        // Insert into the database
        long insertId = database.insert(SeqSQLiteHelper.TABLE_SEQUENCES, null,
                values);

        // Get the Sequence from the database
        Cursor cursor = database.query(SeqSQLiteHelper.TABLE_SEQUENCES,
                allColumns, SeqSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Sequence newSequence = cursorToSeq(cursor);
        cursor.close();
        return newSequence;
    }

    /**
     * Get all the sequences in the database
     * @return List of the Sequences
     */
    public List<Sequence> getAllSequences() {
        List<Sequence> sequences = new ArrayList<Sequence>();

        // Query the database
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

    /**
     * Get a sequence by id
     * @param id Id to query
     * @return The sequence
     */
    public Sequence getSequence(long id) {
        // Query the database
        Cursor cursor = database.query(SeqSQLiteHelper.TABLE_SEQUENCES,
                null, StepSQLiteHelper.COLUMN_ID +"=?",
                new String[] {id +""}, null, null, null);

        // Create the Sequence from the cursor
        Sequence sequence = null;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            sequence = cursorToSeq(cursor);
            cursor.moveToNext();
        }
        return sequence;
    }

    /**
     * Get all the steps belonging to a Sequence
     * @param id The Id of the sequence to get steps for
     * @return List of the Sequence's steps
     */
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

    /**
     * Remove a sequence from the database
     * @param sequence Sequence to remove
     */
    public void deleteSequence(Sequence sequence) {
        long id = sequence.getId();
        database.delete(SeqSQLiteHelper.TABLE_SEQUENCES,
                SeqSQLiteHelper.COLUMN_ID + " = " + id, null);
        stepDatabase.delete(StepSQLiteHelper.TABLE_STEPS,
                StepSQLiteHelper.COLUMN_SEQ + " = " + id, null);
    }

    /**
     * Convert a cursor to a Sequence object
     * @param cursor Cursor from the DB query
     * @return The Sequence created
     */
    private Sequence cursorToSeq(Cursor cursor) {
        Sequence sequence = new Sequence(cursor.getString(1));
        sequence.setId(cursor.getLong(0));
        sequence.setReward(cursor.getString(2));
        return sequence;
    }

    /**
     * Convert a cursor to a Step object
     * @param cursor Cursor from the DB query
     * @return The Step created
     */
    private Step cursorToStep(Cursor cursor) {
        Step step = new Step(cursor.getString(1));
        step.setId(cursor.getLong(0));
        step.setComplete(cursor.getInt(2) == 1);
        step.setSequenceId(cursor.getLong(3));
        return step;
    }
}
