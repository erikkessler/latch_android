package com.inspiredo.latch;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Activity that gets launched at the start. Displays the day's sequences, allows user
 * to mark steps as complete, and add new sequences/steps
 */
public class TodayActivity extends Activity
        implements TriggerDialog.TriggerDialogListener{

    // Request code for creating a sequence/editing a sequence
    static final int        CREATE_SEQ_REQUEST = 1;
    static final int        EDIT_SEQ_REQUEST = 2;

    // SequenceAdapter
    private SeqListAdapter  mSequenceAdapter;

    // Data sources for Sequences and Steps
    private MySQLDataSource mDataSource;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        // Instantiate and open the data sources
        mDataSource = new MySQLDataSource(this);
        mDataSource.open();

        // Setup the list
        DynamicListView seqList = (DynamicListView) findViewById(R.id.today_seq_list);

        // Click listener
        seqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View steps = view.findViewById(R.id.seq_step_list);
                View reward = view.findViewById(R.id.seq_reward);

                boolean collapse = false;
                if (steps.getVisibility() != View.GONE) {
                    // Collapse
                    collapse = true;
                    steps.setVisibility(View.GONE);
                    reward.setVisibility(View.GONE);
                } else {
                    // Expand
                    steps.setVisibility(View.VISIBLE);
                    reward.setVisibility(View.VISIBLE);
                }

                mDataSource.setCollapsed(
                        mSequenceAdapter.getItem(position).toggleCollapsed(),
                        collapse
                );

            }
        });

        // Adapter for sequences
        mSequenceAdapter = new SeqListAdapter(this, R.layout.row_seq, getFragmentManager(),
                seqList);

        // Get all the sequences and add them to the adapter
        Runnable getSequences = new Runnable() {
            @Override
            public void run() {
                List<Sequence> seqs = mDataSource.getAllSequences(MySQLiteHelper.COLUMN_POS);
                mSequenceAdapter.addAll(seqs);
                mSequenceAdapter.notifyDataSetChanged();
            }
        };
        new Thread(getSequences).run();

        seqList.setAdapter(mSequenceAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.today, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                // TODO: Settings Activity
                return true;
            case R.id.action_collapse:
                collapseAll(true);
                return true;
            case R.id.action_expand:
                collapseAll(false);
                return true;

            case R.id.action_add:
                // Launch the sequence creation Activity
                Intent createSeqIntent = new Intent(this, CreateSeqActivity.class);
                createSeqIntent.putExtra(
                        CreateSeqActivity.ORDER_KEY, mSequenceAdapter.getCount()
                );
                startActivityForResult(createSeqIntent, CREATE_SEQ_REQUEST);
                return true;
            default:
                Toast.makeText(this, "Unimplemented action", Toast.LENGTH_SHORT)
                        .show();
                return true;
        }
    }

    /**
     * Collapse or expand all the Sequences in the list view
     * @param collapse Collapse - true, expand - false
     */
    private void collapseAll(boolean collapse) {
        // Get the correct visibility value
        int visibility = collapse ? View.GONE : View.VISIBLE;

        DynamicListView seqList = (DynamicListView) findViewById(R.id.today_seq_list);

        // Expand/collapse all
        for (int i = 0; i < seqList.getCount(); i++) {
            View view = seqList.getChildAt(i);

            // Collapse visible ones
            if (view != null) {
                View steps = view.findViewById(R.id.seq_step_list);
                View reward = view.findViewById(R.id.seq_reward);
                steps.setVisibility(visibility);
                reward.setVisibility(visibility);
            }


            mDataSource.setCollapsed(
                    mSequenceAdapter.getItem(i).setCollapsed(collapse),
                    collapse
            );

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Getting response from sequence creation
        if (requestCode == CREATE_SEQ_REQUEST) {
            // Check for a response - get the id of the new seq
            if (resultCode == RESULT_OK) {
                long newId = data.getLongExtra(CreateSeqActivity.ID_KEY, -1);

                // An ID was passed
                if (newId != -1) {
                    mDataSource.open();
                    mSequenceAdapter.clear();
                    mSequenceAdapter.addAll(mDataSource.getAllSequences(MySQLiteHelper.COLUMN_POS));
                    mSequenceAdapter.notifyDataSetChanged();
                }
            }
        } else if(requestCode == EDIT_SEQ_REQUEST) {
            // Check for a response - get the id of the edited seq
            if (resultCode == RESULT_OK) {
                long seqId = data.getLongExtra(CreateSeqActivity.ID_KEY, -1);
                int pos = data.getIntExtra(CreateSeqActivity.EDIT_POS, -1); // Position to add to

                // An ID was passed
                if (seqId != -1) {
                    mDataSource.open();

                    // Remove and re-add to update the list
                    Sequence s = mDataSource.getSequence(seqId);
                    mSequenceAdapter.remove(s);
                    mSequenceAdapter.insert(s, pos);
                    mSequenceAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    // Reopen the data sources
    @Override
    protected void onResume() {
        mDataSource.open();

        // Clear any notifications upon entering app
        NotificationManager mgr = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.cancelAll();
        super.onResume();
    }

    // Close the data sources
    @Override
    protected void onPause() {
        mDataSource.close();

        super.onPause();
    }

    @Override
    public void onDialogPositiveClick(Trigger t) {

        // Sequence that the Trigger belongs to
        Sequence seq = mDataSource.getSequence(t.getSequenceId());

        // Add the Trigger to Google Calendar
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(t.getTime());


        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();

        // Put in the event info
        values.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());
        cal.add(Calendar.MINUTE, 30);
        values.put(CalendarContract.Events.DTEND, cal.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, seq.getTitle());
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        // Insert the event
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // Get the event ID and save it
        long eventID = Long.parseLong(uri.getLastPathSegment());
        Log.d("Calendar", eventID + "");
        t.setEventId(eventID);

        // Save the Trigger
        t = mDataSource.createTrigger(t);

        // Create the alarm/notification
        Trigger.createTrigger(t, this, seq);

        // Update the UI
        mSequenceAdapter.clear();
        mSequenceAdapter.addAll(mDataSource.getAllSequences(MySQLiteHelper.COLUMN_POS));
        mSequenceAdapter.notifyDataSetChanged();
    }

    private void testQuery() {

        // Projection array. Creating indices for this array instead of doing
        // dynamic lookups improves performance.
        final String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
        final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

// Submit the query and get a Cursor object back.
        cur = cr.query(uri, EVENT_PROJECTION, null, null, null);

        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            Log.d("Calendar", "\ncalId " + calID + "\ndisplayName " + displayName);
        }

    }
}
