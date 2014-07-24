package com.inspiredo.latch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows for persistence and retrieval of data using the local SQLite database.
 */
public class MySQLDataSource {

    // Database fields
    private SQLiteDatabase  database;
    private MySQLiteHelper  dbHelper;

    // Constructor sets the helpers
    public MySQLDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //
    // * SEQUENCE CRUD ACTIONS *
    //

    /**
     * Creates a database entry for a passed sequence
     * @param seq Sequence to create for
     * @return The sequence
     */
    public Sequence createSequence(Sequence seq) {
        // Put the content into a ContentValue
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TITLE, seq.getTitle());
        values.put(MySQLiteHelper.COLUMN_REWARD, seq.getReward());

        // Insert into the database
        long insertId = database.insert(MySQLiteHelper.TABLE_SEQUENCES, null,
                values);

        // Get the Sequence from the database
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SEQUENCES,
                null, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
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
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SEQUENCES,
                null, null, null, null, null, null);

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
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SEQUENCES,
                null, MySQLiteHelper.COLUMN_ID +"=?",
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
     * Remove a sequence from the database
     * @param sequence Sequence to remove
     */
    public void deleteSequence(Sequence sequence) {
        long id = sequence.getId();
        database.delete(MySQLiteHelper.TABLE_SEQUENCES,
                MySQLiteHelper.COLUMN_ID + " = " + id, null);
        database.delete(MySQLiteHelper.TABLE_STEPS,
                MySQLiteHelper.COLUMN_SEQ + " = " + id, null);
    }

    //
    // * STEP CRUD ACTIONS *
    //

    /**
     * Saves a Step to the database
     * @param step Step to save
     * @return The Step saved
     */
    public Step createStep(Step step) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TITLE, step.getTitle());
        values.put(MySQLiteHelper.COLUMN_COMPLETE, step.isComplete());
        values.put(MySQLiteHelper.COLUMN_SEQ, step.getSequenceId());

        long insertId = database.insert(MySQLiteHelper.TABLE_STEPS, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_STEPS,
                null, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Step newStep = cursorToStep(cursor);
        cursor.close();
        return newStep;
    }

    /**
     * Get all the steps belonging to a Sequence
     * @param id The Id of the sequence to get steps for
     * @return List of the Sequence's steps
     */
    public List<Step> getSteps(long id) {
        List<Step> steps = new ArrayList<Step>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_STEPS,
                null, MySQLiteHelper.COLUMN_SEQ +"=?",
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
     * Get all steps in the database
     * @return List of all the Steps
     */
    public List<Step> getAllSteps() {
        List<Step> steps = new ArrayList<Step>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_SEQUENCES,
                null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Step step = cursorToStep(cursor);
            steps.add(step);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return steps;
    }

    /**
     * Change the completeness of a Step
     * @param step Step to change
     * @param complete State to set it to
     */
    public void completeStep(Step step, boolean complete) {
        long id = step.getId();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_COMPLETE, complete);
        database.update(MySQLiteHelper.TABLE_STEPS, values,
                MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    //
    // * CURSOR CONVERTER METHODS *
    //

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
