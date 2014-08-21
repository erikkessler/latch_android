package com.inspiredo.latch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;
import java.util.List;

/**
 * Receives ON_BOOT broadcast. Recreates all the notification alarms
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BOOT", "HEy Boot boy");
        scheduleAlarms(context);
    }

    /**
     * Retrieves Triggers from DB and schedules futures notifications
     */
    private static void scheduleAlarms(Context c) {
        DataSource dataSource = new MySQLDataSource(c);
        dataSource.open();

        List<Trigger> triggers = dataSource.listAllTriggers();

        for (Trigger trigger : triggers) {
            if (trigger.getType() == Trigger.NOTIFICATION &&
                    trigger.getTime().compareTo(new Date()) > 0) {
                Sequence s = dataSource.getSequenceById(trigger.getSequenceId());
                Trigger.createTrigger(trigger, c, s);
            }
        }

        dataSource.close();
    }
}
