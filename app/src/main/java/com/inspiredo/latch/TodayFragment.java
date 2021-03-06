package com.inspiredo.latch;

import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.List;

/**
 * Activity that gets launched at the start. Displays the day's sequences, allows user
 * to mark steps as complete, and add new sequences/steps
 */
public class TodayFragment extends Fragment
        implements TriggerDialog.TriggerDialogListener{

    // Request code for creating a sequence/editing a sequence
    static final int        CREATE_SEQ_REQUEST = 1;
    static final int        EDIT_SEQ_REQUEST = 2;

    // SequenceAdapter
    private SeqListAdapter  mSequenceAdapter;

    // Data sources for Sequences and Steps
    private DataSource mDataSource;

    // Context of activity attached to
    private MyLaunchActivity mActivity;

    // Root view
    private View mRootView;

    public static TodayFragment newInstance() {
        return new TodayFragment();
    }

    public TodayFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MyLaunchActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_today, container, false);


        // Instantiate and open the data sources
        mDataSource = new MySQLDataSource(mActivity);
        mDataSource.open();

        // FAB Click Listener
        FloatingActionButton fab = (FloatingActionButton) mRootView.findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSequence();
            }
        });

        // Setup the list
        DynamicListView seqList = (DynamicListView) mRootView.findViewById(R.id.today_seq_list);

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
        mSequenceAdapter = new SeqListAdapter(mActivity, R.layout.row_seq, getFragmentManager(),
                seqList);

        // Get all the sequences and add them to the adapter
        Runnable getSequences = new Runnable() {
            @Override
            public void run() {
                List<Sequence> seqs = mDataSource.listAllSequences(MySQLiteHelper.COLUMN_POS);
                mSequenceAdapter.addAll(seqs);
                mSequenceAdapter.notifyDataSetChanged();
            }
        };
        new Thread(getSequences).run();

        seqList.setAdapter(mSequenceAdapter);

        return mRootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.today, menu);

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
            default:
                Toast.makeText(mActivity, "Unimplemented action", Toast.LENGTH_SHORT)
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

        DynamicListView seqList = (DynamicListView) mRootView.findViewById(R.id.today_seq_list);

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

    // Launch the sequence creation Activity
    private void createSequence() {
        Intent createSeqIntent = new Intent(mActivity, CreateSeqActivity.class);
        createSeqIntent.putExtra(
                CreateSeqActivity.ORDER_KEY, mSequenceAdapter.getCount()
        );
        startActivityForResult(createSeqIntent, CREATE_SEQ_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Getting response from sequence creation
        if (requestCode == CREATE_SEQ_REQUEST) {
            // Check for a response - get the id of the new seq
            if (resultCode == Activity.RESULT_OK) {
                long newId = data.getLongExtra(CreateSeqActivity.ID_KEY, -1);

                // An ID was passed
                if (newId != -1) {
                    mDataSource.open();
                    mSequenceAdapter.clear();
                    mSequenceAdapter.addAll(mDataSource.listAllSequences(MySQLiteHelper.COLUMN_POS));
                    mSequenceAdapter.notifyDataSetChanged();
                }
            }
        } else if(requestCode == EDIT_SEQ_REQUEST) {
            // Check for a response - get the id of the edited seq
            if (resultCode == Activity.RESULT_OK) {
                long seqId = data.getLongExtra(CreateSeqActivity.ID_KEY, -1);
                int pos = data.getIntExtra(CreateSeqActivity.EDIT_POS, -1); // Position to add to

                // An ID was passed
                if (seqId != -1) {
                    mDataSource.open();

                    // Remove and re-add to update the list
                    Sequence s = mDataSource.getSequenceById(seqId);
                    mSequenceAdapter.remove(s);
                    mSequenceAdapter.insert(s, pos);
                    mSequenceAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    // Reopen the data sources
    @Override
    public void onResume() {
        mDataSource.open();

        // Clear any notifications upon entering app
        NotificationManager mgr = (NotificationManager)
                mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.cancelAll();
        super.onResume();
    }

    // Close the data sources
    @Override
    public void onPause() {
        mDataSource.close();

        super.onPause();
    }

    @Override
    public void onDialogPositiveClick(Trigger t) {
        // Save the Trigger
        t = mDataSource.createTrigger(t);

        // Sequence that the Trigger belongs to
        Sequence seq = mDataSource.getSequenceById(t.getSequenceId());

        // Create the alarm/notification
       Trigger.createTrigger(t, mActivity, seq);

        // Update the UI
        mSequenceAdapter.clear();
        mSequenceAdapter.addAll(mDataSource.listAllSequences(MySQLiteHelper.COLUMN_POS));
        mSequenceAdapter.notifyDataSetChanged();
    }
}
