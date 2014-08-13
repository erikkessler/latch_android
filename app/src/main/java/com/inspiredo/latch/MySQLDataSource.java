package com.inspiredo.latch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Allows for persistence and retrieval of data using the local SQLite database.
 */
public class MySQLDataSource {

    // Database fields
    private SQLiteDatabase  database;
    private MySQLiteHelper  dbHelper;

    // Context
    private Context mContext;

    // Constructor sets the helpers
    public MySQLDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        mContext = context;
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
        values.put(MySQLiteHelper.COLUMN_POS, seq.getOrder());

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
     * @param order How to sort the data
     */
    public List<Sequence> getAllSequences(String order) {
        List<Sequence> sequences = new ArrayList<Sequence>();

        // Query the database
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SEQUENCES,
                null, null, null, null, null, order);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Sequence sequence = cursorToSeq(cursor);
            sequence.setSteps(getSteps(sequence.getId()));
            sequence.setTrigger(getSeqTrigger(sequence.getId()));
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
            sequence.setSteps(getSteps(sequence.getId()));
            sequence.setTrigger(getSeqTrigger(sequence.getId()));
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
        Trigger.delete(getSeqTrigger(id), mContext);
        database.delete(MySQLiteHelper.TABLE_TRIGGERS,
                MySQLiteHelper.COLUMN_SEQ + " = " + id, null);

        // Decrement the order value of following Sequences
        changeRangeOrder(sequence.getOrder() + 1, -1, -1);
    }

    /**
     * Change the order of a range of Sequences by the delta value.
     * If end is -1 just does all greater than Start
     * @param start The Order value of the first one to change
     * @param end The Order value of the last Sequence to change
     * @param delta The amount to change the order by
     */
    private void changeRangeOrder(int start, int end, int delta) {

        // Query for the entries
        Cursor cursor;
        if (end == -1) {
            cursor = database.query(MySQLiteHelper.TABLE_SEQUENCES, null,
                    MySQLiteHelper.COLUMN_POS + " >= " + start, null, null, null, null);
            cursor.moveToFirst();
        } else {
            cursor = database.query(MySQLiteHelper.TABLE_SEQUENCES, null,
                    MySQLiteHelper.COLUMN_POS + " BETWEEN " + start + " AND " + end,
                    null, null, null, null);
            cursor.moveToFirst();
        }

        // Decrement all the entries
        while (!cursor.isAfterLast()) {
            ContentValues vals = new ContentValues();
            vals.put(MySQLiteHelper.COLUMN_POS,
                    cursor.getInt(3) + delta);
            database.update(MySQLiteHelper.TABLE_SEQUENCES, vals,
                    MySQLiteHelper.COLUMN_ID + " = " + cursor.getLong(0), null);
            cursor.moveToNext();
        }
    }

    /**
     * Move a Sequence to a specified position
     * @param s The sequence to move
     * @param end The ending position
     */
    public void moveSequence(Sequence s, int end) {
        int start = s.getOrder();
        long id = s.getId();

        // Update the affected surrounding sequences
        if (start > end) {
            changeRangeOrder(end, start - 1, 1);
        } else if (start < end) {
            changeRangeOrder(start + 1, end, -1);
        }

        // Update the Sequence itself
        changeIndividualOrder(id, end);


    }

    // Changes the order value in the DB
    private void changeIndividualOrder(long id, int newPos) {
        ContentValues vals = new ContentValues();
        vals.put(MySQLiteHelper.COLUMN_POS, newPos);
        database.update(MySQLiteHelper.TABLE_SEQUENCES, vals,
                MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * Update an existing Sequence (as defined by its ID)
     * with the properties and steps of a new Sequence
     * @param oldSId The id of the Sequence to update
     * @param newS The new Sequence to save
     */
    public void updateSequence(long oldSId, Sequence newS) {
        // Put the content into a ContentValue
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TITLE, newS.getTitle());
        values.put(MySQLiteHelper.COLUMN_REWARD, newS.getReward());

        // Update the database
        database.update(
                MySQLiteHelper.TABLE_SEQUENCES,
                values,
                MySQLiteHelper.COLUMN_ID + " = " + oldSId, null
                );

        // Delete all the old Steps
        database.delete(
                MySQLiteHelper.TABLE_STEPS,
                MySQLiteHelper.COLUMN_SEQ + " = " + oldSId, null
                );

        // Create the new steps
        for (Step step : newS.getSteps()) {
            values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_TITLE, step.getTitle());
            values.put(MySQLiteHelper.COLUMN_COMPLETE, step.isComplete());
            values.put(MySQLiteHelper.COLUMN_SEQ, step.getSequenceId());
            database.insert(MySQLiteHelper.TABLE_STEPS, null,
                    values);
        }

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

        Cursor cursor = database.query(MySQLiteHelper.TABLE_STEPS,
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
    // * TRIGGER CRUD ACTIONS *
    //

    /**
     * Save a Trigger to the DataBase
     * @param t The Trigger to save
     * @return The created Trigger
     */
    public Trigger createTrigger(Trigger t) {
        ContentValues values = new ContentValues();
        Log.d("Tigger", t.getTime().getTime() + "");
        values.put(MySQLiteHelper.COLUMN_TIME, t.getTime().getTime());
        values.put(MySQLiteHelper.COLUMN_TYPE, t.getType());
        values.put(MySQLiteHelper.COLUMN_SEQ, t.getSequenceId());

        long insertId = database.insert(MySQLiteHelper.TABLE_TRIGGERS, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_TRIGGERS,
                null, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Trigger newTrigger = cursorToTrigger(cursor);
        cursor.close();
        return newTrigger;
    }

    /**
     * Get Trigger belonging to a Sequence
     * @param id Id of the Sequence
     * @return The Trigger
     */
    public Trigger getSeqTrigger(long id) {
        Trigger t = new Trigger(null, Trigger.NONE);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_TRIGGERS,
                null, MySQLiteHelper.COLUMN_SEQ +"=?",
                new String[] {id +""}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            t = cursorToTrigger(cursor);
            cursor.moveToNext();
        }

        cursor.close();
        return t;
    }

    /**
     * Get all Triggers in the database
     * @return List of all the Steps
     */
    public List<Trigger> getAllTriggers() {
        List<Trigger> triggers = new ArrayList<Trigger>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_TRIGGERS,
                null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Trigger trigger = cursorToTrigger(cursor);
            triggers.add(trigger);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return triggers;
    }

    /**
     * Delete a Trigger's entry
     * @param trigger Trigger to remove
     */
    public void deleteTrigger(Trigger trigger) {
        long id = trigger.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_TRIGGERS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
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
        sequence.setOrder(cursor.getInt(3));
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

    /**
     * Convert a cursor to a Trigger object
     * @param cursor Cursor from the DB query
     * @return The Trigger created
     */
    private Trigger cursorToTrigger(Cursor cursor) {
        Date d = new Date();
        d.setTime(cursor.getLong(1));

        Trigger trigger = new Trigger(
                d,
                cursor.getInt(2)
        );
        trigger.setId(cursor.getLong(0));
        trigger.setSequenceId(cursor.getLong(3));
        return trigger;
    }

}
