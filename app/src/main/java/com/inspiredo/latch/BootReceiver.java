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
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_BOOT_COMPLETED) ||
                action.equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            scheduleAlarms(context);
        }
    }

    /**
     * Retrieves Triggers from DB and schedules futures notifications
     */
    private static void scheduleAlarms(Context c) {
        MySQLDataSource dataSource = new MySQLDataSource(c);
        dataSource.open();

        List<Trigger> triggers = dataSource.getAllTriggers();

        for (Trigger trigger : triggers) {
            if (trigger.getType() == Trigger.NOTIFICATION &&
                    trigger.getTime().compareTo(new Date()) > 0) {
                Sequence s = dataSource.getSequence(trigger.getSequenceId());
                Trigger.createTrigger(trigger, c, s);
            }
        }

        dataSource.close();
    }
}
