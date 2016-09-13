package com.marceme.hpifitness.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Marcel on 9/13/2016.
 */
public class NotificationBroadcaster extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "walk_reminder";
    public static String NOTIFICATION_KEY = "reminder_ notification";

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION_KEY);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);
    }

}
