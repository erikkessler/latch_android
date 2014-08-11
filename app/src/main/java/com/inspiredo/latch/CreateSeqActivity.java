package com.inspiredo.latch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Activity that is launched that allows for the creation of a new sequence.
 * Allows setting of title, steps, and reward.
 * Also allows for editing by passing in the ID of a sequence.
 * TODO: Triggers
 * TODO: Orientation Change
 */
public class  CreateSeqActivity extends Activity {

    // Views for the title
    private ViewSwitcher    mTitleSwitcher;
    private TextView        mTitleView;
    private EditText        mTitleEdit;

    // Views for the reward
    private ViewSwitcher    mRewardSwitcher;
    private TextView        mRewardView;
    private EditText        mRewardEdit;

    // Container for the steps
    private LinearLayout    mStepContainer;

    // Holds the current values of the title, steps, and reward
    private String          mTitle;
    private String          mReward;
    private ArrayList<String>   mSteps;

    // Key for sending back the ID of the created sequence
    public static final String  ID_KEY = "sequence_id";

    // Variables for editing
    public static final String EDIT_ID_KEY = "edit_sequence_id";
    private  boolean        mEditing;
    private long            mEditId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_seq);

        // Fade in/out animations
        Animation fadeIn = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);

        // Setup the Title Switcher to fade in/out
        mTitleSwitcher = (ViewSwitcher) findViewById(R.id.create_title_switcher);
        mTitleSwitcher.setInAnimation(fadeIn);
        mTitleSwitcher.setOutAnimation(fadeOut);

        // Setup the Reward Switcher to fade in/out
        mRewardSwitcher = (ViewSwitcher) findViewById(R.id.create_reward_switcher);
        mRewardSwitcher.setInAnimation(fadeIn);
        mRewardSwitcher.setOutAnimation(fadeOut);

        // Have the EditText show the TextView on focus change
        mTitleEdit = (EditText) findViewById(R.id.create_title_edit);
        mTitleEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mTitleEdit.getText().length() != 0) {
                    mTitleView.setText(mTitle);
                    mTitleSwitcher.showNext();
                }
            }
        });
        mTitleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTitle = mTitleEdit.getText().toString();
            }
        });

        // Have the TextView show the EditText on change
        mTitleView = (TextView) findViewById(R.id.create_title_view);
        mTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleSwitcher.showNext();
                mTitleEdit.requestFocus();
            }
        });

        // Have the EditText show the TextView on focus change
        mRewardEdit = (EditText) findViewById(R.id.create_reward_edit);
        mRewardEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mRewardEdit.getText().length() != 0) {
                    mReward = mRewardEdit.getText().toString();
                    mRewardView.setText(mReward);
                    mRewardSwitcher.showNext();
                }
            }
        });
        mRewardEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mReward = mRewardEdit.getText().toString();
            }
        });

        // Have the TextView show the EditText on change
        mRewardView = (TextView) findViewById(R.id.create_reward_view);
        mRewardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRewardSwitcher.showNext();
                mRewardEdit.requestFocus();
            }
        });

        // Make the button handle clicks - adds new view for a step
        mStepContainer = (LinearLayout) findViewById(R.id.create_steps_container);
        ImageButton addStep = (ImageButton) findViewById(R.id.create_add_step);
        addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStepContainer.addView(createStepView(null));
            }
        });

        // Check and handle for editing case
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mEditId = extras.getLong(EDIT_ID_KEY, -1);
            mEditing = (mEditId != -1);

            // If editing fill in the data
            if (mEditing) {
                // Instantiate and open the data sources
                MySQLDataSource dataSource = new MySQLDataSource(this);
                dataSource.open();

                Sequence s = dataSource.getSequence(mEditId);
                fillFields(s);

                dataSource.close();

                // Change the title
                setTitle(getString(R.string.title_activity_edit_seq));
            }
        }
    }

    /**
     * In editing mode this method fills the fields with the data
     * @param s The sequence to edit
     */
    private void fillFields(Sequence s) {
        // Title
        mTitle = s.getTitle();
        mTitleView.setText(mTitle);
        mTitleEdit.setText(mTitle);

        // Reward
        mReward = s.getReward();
        mRewardView.setText(mReward);
        mRewardEdit.setText(mReward);

        // Steps
        for(Step step : s.getSteps()) {
            mStepContainer.addView(createStepView(step));
        }
    }

    // Creates a new StepViewSwitcher
    private StepViewSwitcher createStepView(Step step) {
        final StepViewSwitcher newSwitcher = (StepViewSwitcher) getLayoutInflater()
                .inflate(R.layout.step_switcher,null);

        // Create and setup the EditText
        final EditText edit = (EditText) newSwitcher.findViewById(R.id.step_title_edit);

        // Create and setup the TextView
        final TextView view = (TextView) newSwitcher.findViewById(R.id.step_title_view);

        // Listen for when the EditText loses focus
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && edit.getText().length() != 0) {
                    view.setText(edit.getText().toString());
                    newSwitcher.showNext();
                }
            }
        });


        // Listen for when the text is changed
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                newSwitcher.setString(edit.getText().toString());
            }
        });


        // Listen for when the view is clicked
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSwitcher.showNext();
                edit.requestFocus();
            }
        });

        if (step == null) {
            edit.requestFocus(); // Focus on new EditText
        } else {
            view.setText(step.getTitle());
            edit.setText(step.getTitle());
            newSwitcher.setString(step.getTitle());
        }

        return newSwitcher;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_seq, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                // TODO: Settings
                return true;
            case R.id.action_save:
                // Before saving check if valid
                if (validate()) {
                    // Save and return the id of the new Seq
                    Intent i = new Intent();

                    // Either save or update
                    if (mEditing) {
                        update();
                    } else {
                        i.putExtra(ID_KEY, save());
                    }
                    setResult(RESULT_OK, i);
                    finish();
                }
                return true;
            case R.id.action_cancel:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                Toast.makeText(this, "Unimplemented action", Toast.LENGTH_SHORT)
                        .show();
                return true;
        }
    }

    /**
     * Updates the sequence and its steps
     */
    private void update() {
        // Instantiate and open the data sources
        MySQLDataSource dataSource = new MySQLDataSource(this);
        dataSource.open();

        // Create the Sequence object and add the steps to it
        Sequence s = new Sequence(mTitle, new Vector<Step>(), mReward);
        for (String stepString : mSteps) {
            Step step = new Step(stepString);
            step.setSequenceId(mEditId);
            s.addStep(step);
        }

        // Edit the sequence and get the id
        dataSource.updateSequence(mEditId, s);

        dataSource.close();
    }

    /**
     * Saves the sequence and its steps
     * @return Returns the index of the sequence
     */
    private long save() {
        long id;


        // Instantiate and open the data sources
        MySQLDataSource dataSource = new MySQLDataSource(this);
        dataSource.open();

        // Create the sequence and get the id
        Sequence s = dataSource.createSequence(
                new Sequence(mTitle, null, mReward)
        );
        id = s.getId();

        // Create and save the steps
        for (String stepString : mSteps) {
            Step step = new Step(stepString);
            step.setSequenceId(id);
            dataSource.createStep(step);
        }

        dataSource.close();

        return id;
    }

    /**
     * Validates that the sequence is good: Has a title and at least one step.
     * Displays an error Toast if not.
     * Note: Call validate before save as validate sets mSteps
     * @return If the sequence is valid
     */
    private boolean validate() {
        mSteps = getSteps();

        String errors = "To save you need:";
        boolean valid = true;

        if (mTitle == null || mTitle.isEmpty()) {
            errors += "\n     A title";
            valid = false;
        }

        if (mSteps.isEmpty()) {
            errors += "\n     At least one step";
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this, errors, Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    /**
     * Get all the steps by checking the container
     * @return Array of the step titles
     */
    private ArrayList<String> getSteps() {
        ArrayList<String> steps = new ArrayList<String>();

        int count = mStepContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            StepViewSwitcher s = (StepViewSwitcher) mStepContainer.getChildAt(i);
            if (s.getString()!= null && s.getString().length() != 0) {
                steps.add(s.getString());
            }
        }

        return steps;
    }
}
