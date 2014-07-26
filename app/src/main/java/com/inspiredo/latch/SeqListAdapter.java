package com.inspiredo.latch;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Binds an Array of sequences to a view
 */
public class SeqListAdapter extends ArrayAdapter<Sequence>{

    // Fragment Manager for displaying the dialog
    private FragmentManager mManager;

    /**
     * Constructor just calls the super constructor
     */
    public SeqListAdapter(Context context, int resource, FragmentManager manager) {
        super(context, resource);
        mManager = manager;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate the view if needed
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_seq, null);
        }

        // Get the sequence corresponding to the position
        final Sequence s = getItem(position);

        if (s != null) {
            // Set the title
            TextView title = (TextView) convertView.findViewById(R.id.seq_title);
            if (title != null) {
                title.setText(s.getTitle());
            }

            // Set the steps
            ListView steps = (ListView) convertView.findViewById(R.id.seq_step_list);
            steps.setFocusable(false); // Needed to allow parent list to be clickable
            steps.setFocusableInTouchMode(false); // Needed to allow parent list to be clickable
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

            // Trigger button
            ImageView trigger = (ImageView) convertView.findViewById(R.id.trigger_icon);
            trigger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (s.getTrigger().getType() == Trigger.NONE) {
                        DialogFragment triggerDialog = TriggerDialog.newInstance(s.getId());
                        triggerDialog.show(mManager, "trigger");
                    } else {
                        Toast.makeText(getContext(),
                                s.getTrigger().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Set correct icon
            if (s.getTrigger().getType() == Trigger.ALARM) {
                trigger.setImageDrawable(getContext().getResources()
                        .getDrawable(R.drawable.ic_action_alarms));
            } else if (s.getTrigger().getType() == Trigger.NOTIFICATION) {
                trigger.setImageDrawable(getContext().getResources()
                        .getDrawable(R.drawable.ic_action_chat));
            } else if (s.getTrigger().getType() == Trigger.NONE) {
                trigger.setImageDrawable(getContext().getResources()
                        .getDrawable(R.drawable.ic_action_add_alarm));
            }

        }

        return convertView;
    }

}
