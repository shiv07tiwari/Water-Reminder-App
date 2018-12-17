package com.example.shivansh.servicesandnotifications.sync;

import android.content.Context;
import android.util.Log;

import com.example.shivansh.servicesandnotifications.utilities.NotificationUtlis;
import com.example.shivansh.servicesandnotifications.utilities.PreferenceUtilities;


public class ReminderTasks {


    public static final String ACTION_INCREMENT_WATER_COUNT = "increment-water-count";
    public static final String DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_INCREMENT_WATER_COUNT_WITH_DISMISS = "increment-water-count-and-dismiss";
    public static final String ACTION_WATER_REMINDER = "water-reminder";

    public static void executeTask(Context context, String action) {
        if (ACTION_INCREMENT_WATER_COUNT.equals(action)) {
            incrementWaterCount(context);
        }
        if (DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtlis.dismissallNotification(context);
        }
        if(ACTION_INCREMENT_WATER_COUNT_WITH_DISMISS.equals(action)) {
            incrementWaterCount(context);
            NotificationUtlis.dismissallNotification(context);
        }
        if(ACTION_WATER_REMINDER.equals(action)) {
            issueChargingReminder(context);
        }
    }

    private static void incrementWaterCount(Context context) {
        PreferenceUtilities.incrementWaterCount(context);
    }
    private static void issueChargingReminder(Context context) {
        Log.e("send rem","Reminder Sent");
        PreferenceUtilities.incrementChargingReminderCount(context);
        NotificationUtlis.remindUserBecauseCharging(context);
    }
}