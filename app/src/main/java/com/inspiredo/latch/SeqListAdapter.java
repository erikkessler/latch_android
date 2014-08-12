package com.inspiredo.latch;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;
import java.util.TreeSet;

/**
 * Binds an Array of sequences to a view
 */
public class SeqListAdapter extends ArrayAdapter<Sequence>{

    // Fragment Manager for displaying the dialog
    private FragmentManager mManager;

    // Context
    private Context mContext;

    // Set of collapsed
    private Set<Integer> mCollapsed;

    /**
     * Constructor just calls the super constructor
     */
    public SeqListAdapter(Context context, int resource, FragmentManager manager) {
        super(context, resource);
        mManager = manager;
        mContext = context;
        mCollapsed = new TreeSet<Integer>();
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
            final ListView steps = (ListView) convertView.findViewById(R.id.seq_step_list);
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
            final TextView reward = (TextView) convertView.findViewById(R.id.seq_reward);
            if (reward != null) {
                reward.setText(s.getReward());
            }

            // Trigger button
            final ImageView trigger = (ImageView) convertView.findViewById(R.id.trigger_icon);
            // Handle clicks
            trigger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If there is no Trigger allow creation of one, if there is show time
                    if (s.getTrigger().getType() == Trigger.NONE) {
                        DialogFragment triggerDialog = TriggerDialog.newInstance(s.getId());
                        triggerDialog.show(mManager, "trigger");
                    } else {
                        Toast.makeText(getContext(),
                                s.getTrigger().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // Handle Long Clicks
            trigger.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // If there is a Trigger allow user to delete it
                    if (s.getTrigger().getType() != Trigger.NONE) {
                        // Build the confirmation dialog
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Trigger.deleteTrigger(s.getTrigger(), mContext);
                                        trigger.setImageDrawable(mContext.getResources()
                                                .getDrawable(R.drawable.ic_action_add_alarm));
                                        s.setTrigger(new Trigger(null, Trigger.NONE));
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        // Show the confirmation dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Delete Trigger?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }

                    return true;
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

            // Collapse if needed
            if (mCollapsed.contains(position)) {
                steps.setVisibility(View.GONE);
                reward.setVisibility(View.GONE);
            } else {
                steps.setVisibility(View.VISIBLE);
                reward.setVisibility(View.VISIBLE);
            }
                    
        }



        return convertView;
    }

    // Maintainable of collapsed items
    public void addCollapsed(int pos) {
        mCollapsed.add(pos);
    }

    public void removeCollapsed(int pos) {
        mCollapsed.remove(pos);
    }

    public void deleteCollapsed(int pos) {
        removeCollapsed(pos);

        Set<Integer> newSet = new TreeSet<Integer>();

        for (Integer i : mCollapsed) {
            int adj = i;
            if (i > pos) adj--;
            newSet.add(adj);
        }

        mCollapsed = newSet;
    }

    public void setCollapsed(Set<Integer> collapsed) {
        mCollapsed = collapsed;
    }

    public Set<Integer> getCollapsed() {
        return mCollapsed;
    }

}
