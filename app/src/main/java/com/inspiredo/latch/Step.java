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
     * Construct step with the given title
     * @param title Title of the step
     */
    public Step(String title) {
        mTitle = title;
    }

    @Override
    public String toString() {
        return mTitle;
    }
}
