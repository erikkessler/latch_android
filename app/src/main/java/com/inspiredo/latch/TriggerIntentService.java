package com.inspiredo.latch;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
        Intent resultIntent = new Intent(this, TodayActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
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
                .setContentIntent(resultPendingIntent)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mgr.notify(1, notification);
    }
}
