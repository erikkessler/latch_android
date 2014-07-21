package com.inspiredo.latch;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class TodayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        // Setup the list
        ListView seqList = (ListView) findViewById(R.id.today_seq_list);
        SeqListAdapter seqAdapter = new SeqListAdapter(this, R.layout.row_seq);
        if (seqList == null) {
            Log.d("Today", "seqList is null");
        }
        seqAdapter.add(dummySeq("Morning", "Drink Water", "Breakfast"));
        seqAdapter.add(dummySeq("Midday", "Brush Teeth", "Lunch"));
        seqAdapter.add(dummySeq("Night", "2' Meditate", "Sleep"));
        seqList.setAdapter(seqAdapter);

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
                return true;
            default:
                Toast.makeText(this, "Unimplemented action", Toast.LENGTH_SHORT)
                        .show();
                return true;
        }
    }

}
