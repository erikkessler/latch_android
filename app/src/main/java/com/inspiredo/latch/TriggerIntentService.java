package com.inspiredo.latch;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

/**
 * Called to display a notification
 */
public class TriggerIntentService extends IntentService {

    public static final String SEQUENCE_TITLE = "seq_title";

    public TriggerIntentService() {
        super("TriggerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String seqName = intent.getStringExtra(SEQUENCE_TITLE);
        NotificationManager mgr = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(seqName)
                .setTicker("Time to Start: " + seqName)
                .setShowWhen(true)
                .setContentText("Just a reminder to start your " +
                seqName + " sequence")
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();
        mgr.notify(1, notification);
    }
}
