package com.inspiredo.latch;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Activity that gets launched at the start. Displays the day's sequences, allows user
 * to mark steps as complete, and add new sequences/steps
 */
public class TodayActivity extends Activity
        implements TriggerDialog.TriggerDialogListener{

    // Request code for creating a sequence
    static final int        CREATE_SEQ_REQUEST = 1;

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
        final ListView seqList = (ListView) findViewById(R.id.today_seq_list);

        // Long click listener
        seqList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Sequence s = (Sequence) seqList.getItemAtPosition(position);
                mDataSource.deleteSequence(s);
                mSequenceAdapter.remove(s);
                mSequenceAdapter.notifyDataSetChanged();
                return true;
            }
        });

        // Adapter for sequences
        mSequenceAdapter = new SeqListAdapter(this, R.layout.row_seq, getFragmentManager());

        // Get all the sequences and add them to the adapter
        Runnable getSequences = new Runnable() {
            @Override
            public void run() {
                mSequenceAdapter.addAll(mDataSource.getAllSequences());
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
            case R.id.action_add:
                // Launch the sequence creation Activity
                Intent createSeqIntent = new Intent(this, CreateSeqActivity.class);
                startActivityForResult(createSeqIntent, CREATE_SEQ_REQUEST);
                return true;
            default:
                Toast.makeText(this, "Unimplemented action", Toast.LENGTH_SHORT)
                        .show();
                return true;
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
                    Sequence newS = mDataSource.getSequence(newId);
                    List<Step> newSteps = mDataSource.getSteps(newId);
                    Trigger t = mDataSource.getSeqTrigger(newId);

                    // Add the new new Sequence
                    if (newS != null) {
                        newS.setSteps(newSteps);
                        newS.setTrigger(t);
                        mSequenceAdapter.add(newS);
                        mSequenceAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    // Reopen the data sources
    @Override
    protected void onResume() {
        mDataSource.open();
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
        // TODO: Save the Trigger to the Sequence
        if (t.getType() == Trigger.ALARM) {
            // Create an Alarm
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(t.getTime());
            Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
            i.putExtra(AlarmClock.EXTRA_HOUR, cal.get(Calendar.HOUR_OF_DAY));
            i.putExtra(AlarmClock.EXTRA_MINUTES, cal.get(Calendar.MINUTE));
            i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            startActivity(i);
        } else if (t.getType() == Trigger.NOTIFICATION) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(t.getTime());
            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(this, TriggerIntentService.class);
            // TODO: Get the actual sequence name
            i.putExtra(TriggerIntentService.SEQUENCE_TITLE, "Morning");
            PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
            mgr.setExact(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), pi);
        }
    }
}
