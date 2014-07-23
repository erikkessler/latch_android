package com.inspiredo.latch;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

public class  CreateSeqActivity extends Activity {

    private ViewSwitcher    mTitleSwitcher;
    private TextView        mTitleView;
    private EditText        mTitleEdit;

    private ViewSwitcher    mRewardSwitcher;
    private TextView        mRewardView;
    private EditText        mRewardEdit;

    private LinearLayout    mStepContainer;

    private String          mTitle;
    private String          mReward;
    private ArrayList<String> mSteps;


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

        // Make the button handle clicks
        mStepContainer = (LinearLayout) findViewById(R.id.create_steps_container);
        ImageButton addStep = (ImageButton) findViewById(R.id.create_add_step);
        final Context thisContext = this;
        addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStepContainer.addView(createStepView());
            }
        });
    }

    // Creates a new StepViewSwitcher
    private StepViewSwitcher createStepView() {
        final StepViewSwitcher newSwitcher = (StepViewSwitcher) getLayoutInflater()
                .inflate(R.layout.step_switcher,null);

        // Create and setup the EditText
        final EditText edit = (EditText) newSwitcher.findViewById(R.id.step_title_edit);

        // Create and setup the TextView
        final TextView view = (TextView) newSwitcher.findViewById(R.id.step_title_view);

        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && edit.getText().length() != 0) {
                    view.setText(edit.getText().toString());
                    newSwitcher.showNext();
                }
            }
        });
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


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSwitcher.showNext();
                edit.requestFocus();
            }
        });

        edit.requestFocus();
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
                // TODO: Save
                if (validate()) {
                    Intent i = new Intent();
                    i.putExtra("sequence_id", save());
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
     * Saves the sequence and its steps
     * @return Returns the index of the sequence
     */
    private long save() {
        long id;

        SeqDataSource dataSource = new SeqDataSource(this);
        StepDataSource stepDataSource = new StepDataSource(this);
        dataSource.open();
        stepDataSource.open();

        Sequence s = dataSource.createSequence(
                new Sequence(mTitle, null, mReward)
        );
        id = s.getId();

        for (String stepString : mSteps) {
            Step step = new Step(stepString);
            step.setSequenceId(id);
            stepDataSource.createStep(step);
        }

        dataSource.close();
        stepDataSource.close();

        return id;
    }

    /**
     * Validates that the sequence is good: Has a title and at least one step.
     * Displays an error Toast if not.
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
        Log.d("Count",count + "");
        for (int i = 0; i < count; i++) {
            StepViewSwitcher s = (StepViewSwitcher) mStepContainer.getChildAt(i);
            if (s.getString()!= null && s.getString().length() != 0) {
                steps.add(s.getString());
            }
        }

        Log.d("Steps", steps.toString());

        return steps;
    }
}
