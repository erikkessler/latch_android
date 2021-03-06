package com.inspiredo.latch;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

/**
 * Binds an Array of sequences to a view
 */
public class SeqListAdapter extends ArrayAdapter<Sequence>{

    // Fragment Manager for displaying the dialog
    private FragmentManager mManager;

    // Context
    private MyLaunchActivity mContext;

    // Dynamic ListView
    private DynamicListView mListView;

    final int INVALID_ID = -1;

    /**
     * Constructor just calls the super constructor
     */
    public SeqListAdapter(MyLaunchActivity context, int resource, FragmentManager manager,
                          DynamicListView listView) {
        super(context, resource);
        mManager = manager;
        mContext = context;
        mListView = listView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

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

            // Create adapter and add all steps
            final StepListAdapter stepAdapter =
                    new StepListAdapter(getContext(), R.layout.row_seq_step);
            stepAdapter.addAll(s.getSteps());

            // Adjust height of the ListView to show all steps
            final float scale = getContext().getResources().getDisplayMetrics().density;
            steps.getLayoutParams().height = (int) (
                    (56 * scale + 0.5f) * stepAdapter.getCount());

            // Set the adapter and the ClickListener
            steps.setAdapter(stepAdapter);
            steps.setOnItemClickListener(stepAdapter);


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

            // Up and down movement
            ImageView upIcon = (ImageView) convertView.findViewById(R.id.move_up);
            ImageView downIcon = (ImageView) convertView.findViewById(R.id.move_down);
            upIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveUp(position);
                }
            });
            downIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveDown(position);
                }
            });
            upIcon.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListView.startMobile();
                    return true;
                }
            });

            // More actions
            ImageView moreIcon = (ImageView) convertView.findViewById(R.id.more_actions);
            moreIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Sequence s = getItem(position);

                    // Build the confirmation dialog
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {

                                case DialogInterface.BUTTON_NEGATIVE:
                                    // Cancel button
                                    break;
                                case 0:
                                    // Edit button
                                    Intent createSeqIntent = new Intent(mContext, CreateSeqActivity.class);
                                    createSeqIntent.putExtra(CreateSeqActivity.EDIT_ID_KEY, s.getId());
                                    createSeqIntent.putExtra(CreateSeqActivity.EDIT_POS, position);
                                    mContext.startActivityForResult(createSeqIntent,
                                            TodayFragment.EDIT_SEQ_REQUEST);
                                    break;

                                case 1:
                                    // Delete Button
                                    DataSource dataSource = new MySQLDataSource(mContext);
                                    dataSource.open();
                                    dataSource.deleteSequence(s);
                                    remove(s);
                                    notifyDataSetChanged();
                                    dataSource.close();
                                    break;
                                case 2:
                                    // Check all Steps
                                    dataSource = new MySQLDataSource(mContext);
                                    dataSource.open();
                                    List<Step> editedSteps = new Vector<Step>();
                                    for (Step step : s.getSteps()) {
                                        step.setComplete(true);
                                        dataSource.completeStep(step, true);
                                        editedSteps.add(step);
                                    }
                                    stepAdapter.clear();
                                    stepAdapter.addAll(editedSteps);
                                    stepAdapter.notifyDataSetChanged();
                                    dataSource.close();
                                    break;
                                case 3:
                                    // Uncheck all Steps
                                    dataSource = new MySQLDataSource(mContext);
                                    dataSource.open();
                                    editedSteps = new Vector<Step>();
                                    for (Step step : s.getSteps()) {
                                        step.setComplete(false);
                                        dataSource.completeStep(step, false);
                                        editedSteps.add(step);
                                    }
                                    stepAdapter.clear();
                                    stepAdapter.addAll(editedSteps);
                                    stepAdapter.notifyDataSetChanged();
                                    dataSource.close();
                                    break;
                            }
                        }
                    };

                    // Show the confirmation dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mContext.getString(R.string.more_actions))
                            .setNegativeButton("Cancel", dialogClickListener)
                            .setItems(new CharSequence[]
                                    {"Edit", "Delete", "Check All", "Uncheck All"},
                                    dialogClickListener).show();

                }
            });


            // Collapse if needed
            if (s.getCollapsed()) {
                steps.setVisibility(View.GONE);
                reward.setVisibility(View.GONE);
            } else {
                steps.setVisibility(View.VISIBLE);
                reward.setVisibility(View.VISIBLE);
            }
                    
        }



        return convertView;
    }

    public void moveUp(int pos) {
        if (pos != 0) {
            moveSequence(pos, -1);
        }
    }

    public void moveDown(int pos) {
        if (pos < getCount() - 1) {
            moveSequence(pos, 1);
        }
    }

    // Method to move a Sequence
    private void moveSequence(int position, int delta) {
        DataSource dataSource = new MySQLDataSource(mContext);
        dataSource.open();

        // Save change to the DB
        Sequence s = getItem(position);
        dataSource.changeSequenceOrder(s, s.getOrder() + delta);

        s.setOrder(s.getOrder() + delta);

        // Update the ListView
        remove(s);
        insert(s, s.getOrder());
        notifyDataSetChanged();


        dataSource.close();
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= getCount()) {
            return INVALID_ID;
        }
        Sequence item = getItem(position);
        return item.getId();
    }
}
