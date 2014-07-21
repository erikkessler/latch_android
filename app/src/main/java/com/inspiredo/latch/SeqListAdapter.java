package com.inspiredo.latch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Binds an Array of sequences to a view
 */
public class SeqListAdapter extends ArrayAdapter<Sequence>{

    /**
     * Constructor just calls the super constructor
     */
    public SeqListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate the view if needed
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_seq, null);
        }

        // Get the sequence corresponding to the position
        Sequence s = getItem(position);

        if (s != null) {
            // Set the title
            TextView title = (TextView) convertView.findViewById(R.id.seq_title);
            if (title != null) {
                title.setText(s.getTitle());
            }

            // Set the steps
            ListView steps = (ListView) convertView.findViewById(R.id.seq_step_list);
            if (steps != null) {
                // Create adapter and add all steps
                StepListAdapter stepAdapter =
                        new StepListAdapter(getContext(), R.layout.row_seq_step);
                stepAdapter.addAll(s.getSteps());

                // Adjust height of the ListView to show all steps
                final float scale = getContext().getResources().getDisplayMetrics().density;
                steps.getLayoutParams().height = (int) (
                        (56 * scale + 0.5f) * stepAdapter.getCount());

                // Set the adapter and the ClickListener
                steps.setAdapter(stepAdapter);
                steps.setOnItemClickListener(stepAdapter);
            }

            // Set the reward
            TextView reward = (TextView) convertView.findViewById(R.id.seq_reward);
            if (reward != null) {
                reward.setText(s.getReward());
            }


        }

        return convertView;
    }

}
