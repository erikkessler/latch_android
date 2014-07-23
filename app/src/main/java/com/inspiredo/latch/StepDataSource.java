package com.inspiredo.latch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains connection to the Step database. Allows for CRUD operations
 */
public class StepDataSource {

    // Database fields
    private SQLiteDatabase database;
    private StepSQLiteHelper dbHelper;
    private String[] allColumns = { StepSQLiteHelper.COLUMN_ID,
            StepSQLiteHelper.COLUMN_TITLE, StepSQLiteHelper.COLUMN_COMPLETE,
            StepSQLiteHelper.COLUMN_SEQ};

    public StepDataSource(Context context) {
        dbHelper = new StepSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Step createStep(Step step) {
        ContentValues values = new ContentValues();
        values.put(StepSQLiteHelper.COLUMN_TITLE, step.getTitle());
        values.put(StepSQLiteHelper.COLUMN_COMPLETE, step.isComplete());
        values.put(StepSQLiteHelper.COLUMN_SEQ, step.getSequenceId());

        long insertId = database.insert(StepSQLiteHelper.TABLE_STEPS, null,
                values);

        Cursor cursor = database.query(StepSQLiteHelper.TABLE_STEPS,
                allColumns, SeqSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Step newStep = cursorToStep(cursor);
        cursor.close();
        return newStep;
    }

    public List<Step> getAllStepss() {
        List<Step> steps = new ArrayList<Step>();

        Cursor cursor = database.query(SeqSQLiteHelper.TABLE_SEQUENCES,
                allColumns, null, null, null, null, null);

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

    public void completeStep(Step step, boolean complete) {
        long id = step.getId();

        ContentValues values = new ContentValues();
        values.put(StepSQLiteHelper.COLUMN_COMPLETE, complete);
        database.update(StepSQLiteHelper.TABLE_STEPS, values,
                StepSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    private Step cursorToStep(Cursor cursor) {
        Step step = new Step(cursor.getString(1));
        step.setId(cursor.getLong(0));
        step.setComplete(cursor.getInt(2) == 1);
        step.setSequenceId(cursor.getLong(3));
        return step;
    }
}
