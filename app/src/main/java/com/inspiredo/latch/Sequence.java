package com.inspiredo.latch;

import java.util.List;
import java.util.Vector;

/**
 * Object that represents a sequence
 */
public class Sequence {

    /**
     * Title of the sequence
     */
    private String mTitle;

    /**
     * Array of steps
     */
    private List<Step> mSteps;

    /**
     * Reward
     */
    private String mReward;

    /**
     * ID for DB Query
     */
    private long mId;

    /**
     * Construct a sequence with only a name
     * @param title The seq title
     */
    public Sequence(String title) { this(title, new Vector<Step>(), ""); }

    /**
     * Construct a sequence with a name and its steps
     * @param title The seq title
     * @param steps The sequence's steps
     */
    public  Sequence(String title, List<Step> steps, String reward) {
        mTitle = title;
        mSteps = steps;
        mReward = reward;

    }

    /**
     * Change the title
     * @param title New title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * @return Title of the sequence
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return All steps of the sequence
     */
    public List<Step> getSteps() {
        return mSteps;
    }

    /**
     * Add a single step
     * @param step The step to add
     */
    public void addStep(Step step) {
        mSteps.add(step);
    }

    /**
     * @return Reward of completing the sequence
     */
    public String getReward() {
        return mReward;
    }

    /**
     * Set the reward
     * @param reward New text of the reward
     */
    public void setReward(String reward) {
        mReward = reward;
    }

    /**
     * Set the ID
     */
    public void setId(long id) {
        mId = id;
    }

    /**
     * @return id of the db entry
     */
    public long getId() {
        return mId;
    }

    @Override
    public String toString() {
        return mTitle;
    }
}
