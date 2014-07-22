package com.inspiredo.latch;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
                    mTitle = mTitleEdit.getText().toString();
                    mTitleView.setText(mTitle);
                    mTitleSwitcher.showNext();
                }
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
        final StepViewSwitcher newSwitcher = new StepViewSwitcher(this);

        // Create and setup the EditText
        final EditText edit = new EditText(this);
        edit.setHint(getString(R.string.step_title_hint));

        // Create and setup the TextView
        final TextView view = new TextView(this, null, R.style.step_title);

        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && edit.getText().length() != 0) {
                    newSwitcher.setString(edit.getText().toString());
                    view.setText(edit.getText().toString());
                    newSwitcher.showNext();
                }
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSwitcher.showNext();
                edit.requestFocus();
            }
        });

        // Add the views
        newSwitcher.addView(edit);
        newSwitcher.addView(view);

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
                validate();
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
     * Validates that the sequence is good: Has a title and at least one step.
     * Displays an error Toast if not.
     * @return If the sequence is valid
     */
    private boolean validate() {
        mTitle = mTitleEdit.getText().toString();
        mReward = mRewardEdit.getText().toString();

        String errors = "To save you need:\n";
        boolean valid = true;

        if (mTitle == null || mTitle.isEmpty()) {
            errors += "     A title";
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this, errors, Toast.LENGTH_SHORT).show();
        }

        return valid;
    }
}
