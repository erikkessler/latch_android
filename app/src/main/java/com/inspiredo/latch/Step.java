package com.inspiredo.latch;

/**
 * Created by erik on 7/20/14.
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
     * Construct step with the given title
     * @param title Title of the step
     */
    public Step(String title) {
        mTitle = title;
        mComplete = false;
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

    @Override
    public String toString() {
        return mTitle;
    }
}
