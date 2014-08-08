package com.inspiredo.latch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Object that represents each instance of a Trigger.
 * A Trigger occurs at a certain time and is either an alarm or notification
 */
public class Trigger {

    // Constants for the types
    public static final int NONE = 0;
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
        mTime = time;
        mType = type;
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

    public static void delete(Trigger t, Context context) {
        Intent i = new Intent(context, TriggerIntentService.class);
        PendingIntent pi = PendingIntent.getService(context, (int) t.getId(), i, 0);
        pi.cancel();
    }

    /**
     * Static Trigger method for setting the alarm/notification
     * @param t Trigger to set alarm for
     * @param c Context
     * @param title Title of the sequence to display in the notification
     */
    public static void createTrigger(Trigger t, Context c, String title) {
        // Time that the notification/alarm should trigger
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(t.getTime());

        if (t.getType() == Trigger.ALARM) {

            // Create the alarm
            Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
            i.putExtra(AlarmClock.EXTRA_HOUR, cal.get(Calendar.HOUR_OF_DAY));
            i.putExtra(AlarmClock.EXTRA_MINUTES, cal.get(Calendar.MINUTE));
            i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            c.startActivity(i);
        } else if (t.getType() == Trigger.NOTIFICATION) {

            // Use AlarmManager to create notification at correct time
            AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(c, TriggerIntentService.class);
            i.putExtra(TriggerIntentService.SEQUENCE_TITLE, title);
            PendingIntent pi = PendingIntent.getService(c, (int) t.getId(), i, 0);

            // Check version to use correct method
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mgr.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            } else {
                mgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            }
        }
    }

    @Override
    public String toString() {
        String string;

        if (mType == NONE) {
            return "No Trigger set";
        } else if (mType == ALARM) {
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
