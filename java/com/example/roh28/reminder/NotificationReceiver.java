package com.example.roh28.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by roh28 on 4/19/2018.
 */

public class NotificationReceiver extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    Reminder workingReminder;
    ReminderList reminderList = ReminderList.getInstance();
    static String CHANNEL_ID = "102";
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int reminderId  = (int) intent.getExtras().get(NOTIFICATION);
        int notificationId = (int) intent.getExtras().get(NOTIFICATION_ID);

        Intent contentIntent = new Intent(context, TaskListActivity.class);
        contentIntent.putExtra(ReminderList.REMINDERID, reminderId);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(contentIntent);

        workingReminder = reminderList.getReminderbyID(reminderId);
        PendingIntent contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_on_black_24dp)
                .setContentTitle("Reminder")
                .setContentText(workingReminder.getTaskTitle())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(contentPendingIntent);

        notificationManager.notify(notificationId,builder.build());

    }
}
