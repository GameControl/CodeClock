package org.gamecontrol.codeclock;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Corbin on 11/21/2014.
 */
public class CCNotificationManager extends Service {

    private static final String TAG = "org.gamecontrol.codeclock.CCNotificationManager";
    private static NotificationCompat.Builder mBuilder;
    private static NotificationManager mNotificationManager;
    private static HashMap<Integer, Job> runningData;
    private static HashMap<Integer, String> dataNames;

    @Override
    public void onCreate() {
        super.onCreate();
        dataNames = new HashMap<Integer, String>();
        runningData = new HashMap<Integer, Job>();
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_white_icon_36x36)
                .setContentTitle(CCUtils.APP_NAME)
                .setContentText(CCUtils.JOB_NAME);
        mNotificationManager = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void registerJob(int id, String parentProject, String parentProjectUUID, String jobName, String jobUUID){
        Job job = null;


        try {
            // get JSON of job file
            JSONObject jobJSON = CCUtils.fileToJSON(this.getApplicationContext(), jobUUID + ".json");

            // construct a new job object
            job = new Job(jobUUID , parentProjectUUID, jobName, jobJSON);

        } catch (Exception e) {
            Log.d(TAG, "caught exception: " + e.toString());
        }

        if(job != null) {
            runningData.put(id, job);
            dataNames.put(id, parentProject);
            notify(id, job);
        }
        else {
            Log.e(TAG, "Job is Null");
        }

    }

    public void deleteJob(int id){
        mNotificationManager.cancel(id);
    }

    public void update(){
        Set<Integer> keySet = runningData.keySet();
        for(Integer integer: keySet){
            Job job = runningData.get(integer);
            notify(integer, job);
        }
    }
    public void notify(int id, Job job) {
        RemoteViews view = new RemoteViews("org.gamecontrol.codeclock", R.layout.job_notification);
        view.setTextViewText(R.id.job_name_notification, job.getName());
        view.setTextViewText(R.id.time_notification, CCUtils.msToHourMinSec(CCUtils.getTotalElapsed(job)));
        mBuilder.setContent(view);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, JobActivity.class);
        resultIntent.putExtra(CCUtils.PROJECT_NAME, dataNames.get(id));
        resultIntent.putExtra(CCUtils.PROJECT_UUID, job.getProjectUUID());
        resultIntent.putExtra(CCUtils.JOB_NAME, job.getName());
        resultIntent.putExtra(CCUtils.JOB_UUID, job.getUUID());
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(JobActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder.build());
    }
}
