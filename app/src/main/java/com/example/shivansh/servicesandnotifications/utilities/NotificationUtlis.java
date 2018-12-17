package com.example.shivansh.servicesandnotifications.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.shivansh.servicesandnotifications.MainActivity;
import com.example.shivansh.servicesandnotifications.R;
import com.example.shivansh.servicesandnotifications.sync.ReminderTasks;
import com.example.shivansh.servicesandnotifications.sync.WaterReminderFirebaseJobService;
import com.example.shivansh.servicesandnotifications.sync.WaterReminderIntentService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class NotificationUtlis {
    private static String CHANNEL_ID = String.valueOf(1);
    public static int NOTIFICATION_ID = 6882;

    private static final int REMINDER_INTERVAL_MINUTES = 30;
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS;
    private static Boolean isnotificationSend=false;

    synchronized public static void scheduleNotifications(Context context) {
        if(isnotificationSend) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispach = new FirebaseJobDispatcher(driver);
        Job reminderjob = dispach.newJobBuilder()
                .setService(WaterReminderFirebaseJobService.class)
                .setTag("NOTIFICATION_SERVICE")
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispach.schedule(reminderjob);
        Log.e("REM JOB", String.valueOf(reminderjob)+" "+REMINDER_INTERVAL_SECONDS);
        isnotificationSend=true;

    }

    public static void remindUserBecauseCharging(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_drink_grey);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_drink_grey)
                .setLargeIcon(icon)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setContentTitle(context.getResources().getString(R.string.charging_reminder_notification_title))
                .setContentText(context.getResources().getString(R.string.charging_reminder_notification_body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent(context))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getResources().getString(R.string.charging_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_SOUND)
                .addAction(R.drawable.ic_drink_grey,"I DID IT!",incrementWaterCountIntent(context))
                .addAction(R.drawable.ic_drink_grey,"NO THANKS",dismissNotificationIntent(context))
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private static int INTENT_ID = 8765;

    public static PendingIntent contentIntent (Context context) {
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context,INTENT_ID,mainActivityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public static PendingIntent incrementWaterCountIntent (Context context) {

        Intent incrementWaterCountIntent = new Intent(context, WaterReminderIntentService.class);
        incrementWaterCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_WATER_COUNT_WITH_DISMISS);
        return PendingIntent.getService(context,INTENT_ID,incrementWaterCountIntent,PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public static PendingIntent dismissNotificationIntent (Context context) {

        Intent dismissnotiintent = new Intent(context, WaterReminderIntentService.class);
        dismissnotiintent.setAction(ReminderTasks.DISMISS_NOTIFICATION);
        return PendingIntent.getService(context,INTENT_ID,dismissnotiintent,PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public static void dismissallNotification(Context context) {
        Log.e("log","DISMISS");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancelAll();
    }
}