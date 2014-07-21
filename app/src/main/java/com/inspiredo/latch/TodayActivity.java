package com.inspiredo.latch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activity that gets launched at the start. Displays the day's sequences, allows user
 * to mark steps as complete, and add new sequences/steps
 */
public class TodayActivity extends Activity {

    // SequenceAdapter
    SeqListAdapter mSequenceAdapter;

    // Request code for creating a sequence
    static final int CREATE_SEQ_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        // Setup the list
        ListView seqList = (ListView) findViewById(R.id.today_seq_list);
        mSequenceAdapter = new SeqListAdapter(this, R.layout.row_seq);
        if (seqList == null) {
            Log.d("Today", "seqList is null");
        }

        final Context thisContext = this;
        Runnable getSequences = new Runnable() {
            @Override
            public void run() {
                SeqDataSource dataSource = new SeqDataSource(thisContext);
                dataSource.open();
                mSequenceAdapter.addAll(dataSource.getAllSequences());
                mSequenceAdapter.notifyDataSetChanged();
                dataSource.close();
            }
        };
        new Thread(getSequences).run();

        seqList.setAdapter(mSequenceAdapter);

    }

    private Sequence dummySeq(String seq, String step, String reward) {
        Sequence s = new Sequence(seq);
        s.addStep(new Step(step));
        s.addStep(new Step("20' Read"));
        s.addStep(new Step("10 Pushups"));
        s.setReward(reward);
        return s;
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
                // TODO: Add Habit Activity
                SeqDataSource dataSource = new SeqDataSource(this);
                dataSource.open();
                mSequenceAdapter.add(dataSource.createSequence(dummySeq("Though", "I swim","I am")));
                mSequenceAdapter.notifyDataSetChanged();
                Intent createSeqIntent = new Intent(this, CreateSeqActivity.class);
                startActivityForResult(createSeqIntent, CREATE_SEQ_REQUEST);
                dataSource.close();
                return true;
            default:
                Toast.makeText(this, "Unimplemented action", Toast.LENGTH_SHORT)
                        .show();
                return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREATE_SEQ_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.d("REsult", "OKAY!");
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("Result","Canceled");
            }
        }
    }

}
