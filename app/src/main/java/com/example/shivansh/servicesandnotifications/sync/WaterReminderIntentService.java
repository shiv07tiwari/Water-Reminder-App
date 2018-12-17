package com.example.shivansh.servicesandnotifications.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


public class WaterReminderIntentService extends IntentService {

    public WaterReminderIntentService() {
        super("WaterReminderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

      //  Log.e("log","Service Started");
        String action = intent.getAction();
        ReminderTasks.executeTask(this, action);
    }
}