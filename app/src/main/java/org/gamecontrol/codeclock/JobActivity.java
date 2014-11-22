package org.gamecontrol.codeclock;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;


public class JobActivity extends Activity {

    private final static String TAG = "org.gamecontrol.codeclock.JobActivity";
    private final Object mSynchronizedObject = new Object();

    private int mID;
    private boolean ready = false;
    private String parentProject;
    private String parentProjectUUID;
    private String jobName;
    private String jobUUID;

    private Button jobTimer;
    private Job currentJob;
    private JobBeatReceiver beatReceiver;
    private CCNotificationManager mCCNotificationManager;

    @Override
    protected void onPause() {
        ready = false;
        unregisterReceiver(beatReceiver);
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

        jobTimer = (Button) findViewById(R.id.jobTimerButton);
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

        if(currentJob.getCurrentState() == CCUtils.STATE_RUNNING){
            Button timeButton = (Button) findViewById(R.id.jobTimerButton);
            Drawable img = this.getResources().getDrawable(R.drawable.dial_running);
            timeButton.setBackground(img);
        }

        // Register the receiver
        IntentFilter filter = new IntentFilter(CCUtils.BEAT);
        beatReceiver = new JobBeatReceiver(this);
        registerReceiver(beatReceiver, filter);

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
                if (currentJob.getCurrentState() == CCUtils.STATE_RUNNING) {
                    //change ring to pause
                    Button timeButton = (Button) findViewById(R.id.jobTimerButton);
                    Drawable img = view.getResources().getDrawable(R.drawable.dial_stopped);
                    timeButton.setBackground(img);
                    //pause timer
//                    mCCNotificationManager.deleteJob(mID);
                    currentJob.pause();
                    stopHeart();
                } else {
                    startHeart();
                    //change ring to running
                    Button timeButton = (Button) findViewById(R.id.jobTimerButton);
                    Drawable img = view.getResources().getDrawable(R.drawable.dial_running);
                    timeButton.setBackground(img);
                    //start timer
                    currentJob.start();
//                    mCCNotificationManager.registerJob(mID, parentProject, parentProjectUUID, jobName, jobUUID);
                }
                try {
                    CCUtils.JSONToFile(this.getApplicationContext(), currentJob.toJSON(), jobUUID + ".json");
                    Log.d(TAG, "Writing Job: " + currentJob.getUUID());
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    }

    private void startHeart() {
        Intent intent = new Intent(JobActivity.this, Kalima.class);
        JobActivity.this.startService(intent);
    }

    private void stopHeart() {
        Intent intent = new Intent(JobActivity.this, Kalima.class);
        JobActivity.this.stopService(intent);
    }

    private void initJob() {
        try {
            // get JSON of job file
            JSONObject jobJSON = CCUtils.fileToJSON(this.getApplicationContext(), jobUUID + ".json");

            // construct a new job object
            currentJob = new Job(jobUUID , parentProjectUUID, jobName, jobJSON);

        } catch (Exception e) {
            Log.d(TAG, "caught exception: " + e.toString());
        }
    }

    public void markComplete(View view){
        Toast toast = Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT);
        toast.show();
//        finish();
    }

    public void update() {
        jobTimer.setText(CCUtils.msToHourMinSec(CCUtils.getTotalElapsed(currentJob)));
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

    private int getId(){
        return UUID.fromString(jobUUID).hashCode();
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
