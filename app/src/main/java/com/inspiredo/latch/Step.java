package com.inspiredo.latch;

/**
 * Object that represents each instance of a step
 */
public class Step {

    /**
     * Title of the step
     */
    private String mTitle;

    /**
     * True if the step is complete
     */
    private Boolean mComplete;

    /**
     * Id of the sequence that this step belongs to
     */
    private long mSequenceId;

    /**
     * Id of the step in the DB
     */
    private long mId;


    /**
     * Construct step with the given title
     * @param title Title of the step
     */
    public Step(String title) {
        mTitle = title;
        mComplete = false;
    }

    /**
     * @return The title of the step
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Set the title to a new value
     * @param title Name of the new title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * @return The state of the step
     */
    public Boolean isComplete() {
        return mComplete;
    }

    /**
     * Toggles the complete state
     * @return The new state
     */
    public Boolean toggleComplete() {
        mComplete = !mComplete;
        return mComplete;
    }

    /**
     * Set the completeness to a certain state
     * @param complete The state to set it to
     */
    public void setComplete(Boolean complete) {
        mComplete = complete;
    }

    /**
     * @return The id of the sequence
     */
    public long getSequenceId() {
        return mSequenceId;
    }

    /**
     * Set the id of the step's sequence
     * @param id Id of the step's sequence
     */
    public void setSequenceId(long id) {
        mSequenceId = id;
    }

    /**
     * @return The id of the step
     */
    public long getId() {
        return mId;
    }

    /**
     * Set the id of the step in the DB
     * @param id Id of the step
     */
    public void setId(long id) {
        mId = id;
    }

    @Override
    public String toString() {
        return mTitle;
    }
}
