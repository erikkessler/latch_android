package com.inspiredo.latch;

import java.util.Date;

/**
 * Object that represents each instance of a Trigger.
 * A Trigger occurs at a certain time and is either an alarm or notification
 */
public class Trigger {

    // Constants for the types
    public static final int ALARM = 1;
    public static final int NOTIFICATION = 2;

    // Datetime of trigger
    private Date mTime;

    // Trigger type
    private int mType;

    // DB Id
    private long mId;

    // ID of the Sequence it belongs to
    private long mSequenceId;

    /**
     * Construct a new trigger
     * @param time Time to trigger notification
     * @param type Type of notification
     */
    public Trigger(Date time, int type) {

    }

    /**
     * Change the time to trigger at
     * @param time New time
     */
    public void setTime(Date time) {
        mTime = time;
    }

    /**
     * Gets the DateTime that it should occur
     * @return The DateTime
     */
    public Date getTime() {
        return mTime;
    }

    /**
     * Set the notification type
     * @param type The new type to set it to
     */
    public void setType(int type) {
        mType = type;
    }

    /**
     * Get the type
     * @return The trigger's type
     */
    public int getType() {
        return mType;
    }

    /**
     * Set the Trigger's id
     * @param id The id to set to
     */
    public void setId(long id) {
        mId = id;
    }

    /**
     * Get the Trigger's Id
     * @return The id
     */
    public long getId() {
        return mId;
    }

    /**
     * Set the sequence id
     * @param seqId The id of the owning sequence
     */
    public void setSequenceId(long seqId) {
        mSequenceId = seqId;
    }

    /**
     * Get the id of the owner Sequence
     * @return The owning Sequence's id
     */
    public long getSequenceId() {
        return mSequenceId;
    }

    @Override
    public String toString() {
        String string;

        if (mType == ALARM) {
            string = "An alarm at ";
        } else if(mType == NOTIFICATION) {
            string = "A notification at ";
        } else {
            return "Invalid Trigger type";
        }

        string += mTime;

        return string;
    }
}
