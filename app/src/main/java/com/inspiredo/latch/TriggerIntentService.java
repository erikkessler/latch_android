package com.inspiredo.latch;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

/**
 * Called to display a notification
 */
public class TriggerIntentService extends IntentService {

    public static final String SEQUENCE_ID = "seq_id";

    public static final String ACTION_CREATE = "create";
    public static final String ACTION_COMPLETE = "complete";

    public TriggerIntentService() {
        super("TriggerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(ACTION_CREATE)) {
            actionCreate(intent);
        } else if (intent.getAction().equals(ACTION_COMPLETE)) {
            actionComplete(intent);
        }

    }

    // Marks all of the sequence actions as complete
    private void actionComplete(Intent intent) {
        Long seqId = intent.getLongExtra(SEQUENCE_ID, -1);

        // Get the Sequence
        MySQLDataSource dataSource = new MySQLDataSource(this);
        dataSource.open();

        Sequence s = dataSource.getSequence(seqId);

        if (s == null) {
            dataSource.close();
            return;
        }

        for (Step step : s.getSteps()) {
            dataSource.completeStep(step, true);
        }

        dataSource.close();

        NotificationManager mgr = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.cancel((int) s.getId());
    }

    // Creates a notification
    private void actionCreate(Intent intent) {
        Intent resultIntent = new Intent(this, TodayActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Long seqId = intent.getLongExtra(SEQUENCE_ID, -1);

        // Get the Sequence
        MySQLDataSource dataSource = new MySQLDataSource(this);
        dataSource.open();

        Sequence s = dataSource.getSequence(seqId);

        dataSource.close();

        // Check for null
        if (s == null){ return; }

        // Setup the Complete Action
        Intent completeIntent = new Intent(this, TriggerIntentService.class);
        completeIntent.setAction(ACTION_COMPLETE);
        completeIntent.putExtra(SEQUENCE_ID, seqId);
        PendingIntent piComplete = PendingIntent.getService(this, 0, completeIntent, 0);

        // Set the message and big message
        String msg = "1st Step: " + s.getSteps().get(0);

        NotificationManager mgr = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(s.getTitle())
                .setTicker("Time to Start: " + s.getTitle())
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new Notification.BigTextStyle()
                        .bigText(createBigMessage(s)))
                .addAction(R.drawable.ic_action_accept, "Completed", piComplete)
                .setContentIntent(resultPendingIntent).build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mgr.notify((int) s.getId(), notification);
    }

    /**
     * Create the big message for a Sequence
     * @param s The Sequence
     * @return The big message
     */
    private String createBigMessage(Sequence s) {
        String msg = "";

        for (Step step : s.getSteps()) {
            msg += "\t- " + step.getTitle() + "\n";
        }

        msg += "\nReward: " + s.getReward();

        return msg;
    }
}
