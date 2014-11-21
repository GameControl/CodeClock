package org.gamecontrol.codeclock;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.UUID;
import java.util.TimerTask;


public class JobActivity extends Activity{

    private final static String TAG = "org.gamecontrol.codeclock.JobActivity";
    private final Object mSynchronizedObject = new Object();
    private static int classCount = 0;
    private static NotificationCompat.Builder mBuilder;
    private static NotificationManager mNotificationManager;

    private int mID;
    private boolean ready = false;
    private String parentProject;
    private String parentProjectUUID;
    private String jobName;
    private String jobUUID;

    private TimeService.TimeContainer timeContainer;
    private Button jobTimer;
    private Job currentJob;
    private Handler handler;
    private Timer timer;
    private final Runnable updateTextRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimeText();
        }
    };

    @Override
    protected void onPause() {
        ready = false;
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_job);

        // Get the Intent from ProjectActivity
        Intent intent = getIntent();
        parentProject = intent.getStringExtra(CCUtils.PROJECT_NAME);
        parentProjectUUID = intent.getStringExtra(CCUtils.PROJECT_UUID);
        jobName = intent.getStringExtra(CCUtils.JOB_NAME);
        jobUUID = intent.getStringExtra(CCUtils.JOB_UUID);
        mID = getId();

        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_white_icon_36x36)
                .setContentTitle(CCUtils.APP_NAME)
                .setContentText(jobName);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        jobTimer = (Button) findViewById(R.id.jobTimerButton);
        handler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        initJob();

        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
//            actionBar.setTitle(parentProject + " > " + currentJob.getName());
            actionBar.setTitle(currentJob.getName());

        }

        if(timeContainer == null)
            timeContainer = TimeService.TimeContainer.getInstance();

        // If TimeContainer holds data for a different job, save that progress, and load the current job
        if(timeContainer.getJobUUID() != currentJob.getUUID()) {
            timeContainer.saveJob();
            timeContainer.loadJob(currentJob);
        }

        if(currentJob.getCurrentState() == CCUtils.STATE_RUNNING){
            Button timeButton = (Button) findViewById(R.id.jobTimerButton);
            Drawable img = this.getResources().getDrawable(R.drawable.dial_running);
            timeButton.setBackground(img);
        }
        startUpdateTimer();

        ready = true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.job, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                HomeActivity.openSettings(JobActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void toggleTimer(View view) {
        if(ready){
            synchronized (mSynchronizedObject) {
                startService();
                if (timeContainer.getCurrentState() == CCUtils.STATE_RUNNING) {
                    //change ring to pause
                    Button timeButton = (Button) findViewById(R.id.jobTimerButton);
                    Drawable img = view.getResources().getDrawable(R.drawable.dial_stopped);
                    timeButton.setBackground(img);
                    //pause timer
                    timeContainer.pause();
                    stopService();
                    clearNotification();
                } else {
                    //change ring to running
                    Button timeButton = (Button) findViewById(R.id.jobTimerButton);
                    Drawable img = view.getResources().getDrawable(R.drawable.dial_running);
                    timeButton.setBackground(img);
                    //start timer
                    timer = new Timer();
                    timeContainer.start();
                    createNotification();
                }
                try {
                    OutputStream out = this.openFileOutput(jobUUID + ".json", Context.MODE_PRIVATE);
                    OutputStreamWriter writer = new OutputStreamWriter(out);
                    writer.write(currentJob.toJSON().toString());
                    Log.d(TAG, "Writing Job: " + currentJob.getUUID());
                    //timeButton.setText(TimeService.msToHourMinSec(timeContainer.getTotalElapsed()));
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startService() {
        Intent intent = new Intent(JobActivity.this, TimeService.class);
        JobActivity.this.startService(intent);
    }

    private void stopService() {
        Intent intent = new Intent(JobActivity.this, TimeService.class);
        JobActivity.this.stopService(intent);
    }

    private void initJob() {
        try {
            // get JSON of job file
            JSONObject jobJSON = CCUtils.fileToJSON(this.getApplicationContext(), jobUUID + ".json");

            // construct a new job object
            currentJob = new Job(UUID.fromString(jobUUID), parentProjectUUID, jobName, 0, null);

            // set job state
            currentJob.setCurrentState(jobJSON.getInt(CCUtils.STATE));

            // set job start times
            JSONArray startTimesJSON = jobJSON.getJSONArray(CCUtils.START_TIMES);
            ArrayList<Long> startTimesArray = CCUtils.JSONArrayToArrayListLong(startTimesJSON);
            currentJob.setStartTimes(startTimesArray);

            // set job running times
            JSONArray runningTimesJSON = jobJSON.getJSONArray(CCUtils.RUNNING_TIMES);
            ArrayList<Long> runningTimesArray = CCUtils.JSONArrayToArrayListLong(runningTimesJSON);
            currentJob.setRunningTimes(runningTimesArray);

            // set job total elapsed time
            currentJob.setElapsed(Long.valueOf(jobJSON.get(CCUtils.ELAPSED).toString()));
        } catch (Exception e) {
            Log.d(TAG, "caught exception: " + e.toString());
        }
    }

    public void markComplete(View view){
        Toast toast = Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT);
        toast.show();
//        finish();
    }

    private void updateTimeText() {
        jobTimer.setText(TimeService.msToHourMinSec(timeContainer.getTotalElapsed()));
        createNotification();
    }

    public void startUpdateTimer() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(updateTextRunnable);
            }
        }, 0, 16);
    }

    public void tagButton(View v) {
        Intent intent = new Intent(JobActivity.this, TagActivity.class);
        intent.putExtra(CCUtils.FILENAME, jobUUID);
        intent.putExtra(CCUtils.TYPE, CCUtils.JOB);
        startActivity(intent);
    }

    public void descriptionButton(View v) {
        Intent intent = new Intent(JobActivity.this, DescriptionActivity.class);
        intent.putExtra(CCUtils.FILENAME, jobUUID);
        intent.putExtra(CCUtils.TYPE, CCUtils.JOB);
        startActivity(intent);
    }

    public void settingsButton(View v) {
        Intent intent = new Intent(JobActivity.this, JobSettingsActivity.class);
        intent.putExtra(CCUtils.FILENAME, jobUUID);
        startActivity(intent);
    }

    public void createNotification(){

        RemoteViews view = new RemoteViews("org.gamecontrol.codeclock", R.layout.job_notification);
        view.setTextViewText(R.id.job_name_notification, jobName);
        view.setTextViewText(R.id.time_notification,TimeService.msToHourMinSec(timeContainer.getTotalElapsed()));
        mBuilder.setContent(view);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, JobActivity.class);
        resultIntent.putExtra(CCUtils.PROJECT_NAME, parentProject);
        resultIntent.putExtra(CCUtils.PROJECT_UUID, parentProjectUUID);
        resultIntent.putExtra(CCUtils.JOB_NAME, jobName);
        resultIntent.putExtra(CCUtils.JOB_UUID, jobUUID);
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
        mNotificationManager.notify(mID, mBuilder.build());
    }

    public void clearNotification(){
        mNotificationManager.cancel(mID);
    }

    private int getId(){
        int output = 0;
        for(char c: jobName.toCharArray()){
            output += (int) c;
        }
        return output;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_job, container, false);
            return rootView;
        }
    }
}
