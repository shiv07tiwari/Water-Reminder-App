package com.example.shivansh.servicesandnotifications.sync;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.firebase.jobdispatcher.JobService;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class WaterReminderFirebaseJobService extends JobService {
    private  AsyncTask mBackgroundTask;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final com.firebase.jobdispatcher.JobParameters job) {
        Log.e("Job Started","JOb STARTED");
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                ReminderTasks.executeTask(WaterReminderFirebaseJobService.this,ReminderTasks.ACTION_WATER_REMINDER);
                return null;
            }
            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job,false);
            }
        };
        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        if(mBackgroundTask!=null) {
            mBackgroundTask.cancel(true);
        }
        return true;
    }
}
