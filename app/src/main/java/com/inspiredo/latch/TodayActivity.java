package com.inspiredo.latch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

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

    // Key to bundle the collapsed items
    static final String        COLLAPSED_KEY = "collapsed_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        // Instantiate and open the data sources
        mDataSource = new MySQLDataSource(this);
        mDataSource.open();

        // Setup the list
        final ListView seqList = (ListView) findViewById(R.id.today_seq_list);

        // Click listener
        seqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View steps = view.findViewById(R.id.seq_step_list);
                View reward = view.findViewById(R.id.seq_reward);

                if (steps.getVisibility() != View.GONE) {
                    // Expand
                    steps.setVisibility(View.GONE);
                    reward.setVisibility(View.GONE);
                    mSequenceAdapter.addCollapsed(position);
                } else {
                    // Collapse
                    steps.setVisibility(View.VISIBLE);
                    reward.setVisibility(View.VISIBLE);
                    mSequenceAdapter.removeCollapsed(position);
                }

            }
        });

        // Long click listener - dialog to edit or delete
        final Context self = this;
        seqList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Sequence s = (Sequence) seqList.getItemAtPosition(position);

                // Build the confirmation dialog
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // Cancel Button
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                // Edit button
                                Intent createSeqIntent = new Intent(self, CreateSeqActivity.class);
                                createSeqIntent.putExtra(CreateSeqActivity.EDIT_ID_KEY, s.getId());
                                createSeqIntent.putExtra(CreateSeqActivity.EDIT_POS, position);
                                startActivityForResult(createSeqIntent, EDIT_SEQ_REQUEST);
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                // Delete Button
                                mSequenceAdapter.deleteCollapsed(position);
                                mDataSource.deleteSequence(s);
                                mSequenceAdapter.remove(s);
                                mSequenceAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                };

                // Show the confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(self);
                builder.setMessage("Edit or Delete this Sequence?")
                        .setPositiveButton("Cancel", dialogClickListener)
                        .setNeutralButton("Delete", dialogClickListener)
                        .setNegativeButton("Edit", dialogClickListener).show();


                return true;
            }
        });


        // Adapter for sequences
        mSequenceAdapter = new SeqListAdapter(this, R.layout.row_seq, getFragmentManager());

        // Get collapsed
        if (savedInstanceState != null) {
            int[] array = savedInstanceState.getIntArray(COLLAPSED_KEY);
            if (array != null) {
                Set<Integer> set = new TreeSet<Integer>();
                for (int anArray : array) {
                    set.add(anArray);
                }
                mSequenceAdapter.setCollapsed(set);
            }
        }

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
                    mSequenceAdapter.clear();
                    mSequenceAdapter.addAll(mDataSource.getAllSequences());
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
        // Save the Trigger
        t = mDataSource.createTrigger(t);

        // Sequence that the Trigger belongs to
        Sequence seq = mDataSource.getSequence(t.getSequenceId());

        // Create the alarm/notification
       Trigger.createTrigger(t, this, seq.getTitle());

        // Update the UI
        mSequenceAdapter.clear();
        mSequenceAdapter.addAll(mDataSource.getAllSequences());
        mSequenceAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Make collapsed an int[]
        Set<Integer> collapsed = mSequenceAdapter.getCollapsed();
        int[] array = new int[collapsed.size()];
        Iterator<Integer> iterator = collapsed.iterator();
        for (int i = 0; i < collapsed.size(); i++) {
            array[i] = iterator.next();
        }

        outState.putIntArray(COLLAPSED_KEY, array);
    }
}
