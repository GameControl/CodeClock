package org.gamecontrol.codeclock;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

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
    private long estimate;

    private Button jobTimer;
    private TextView estimateText;
    private TextView actualText;

    private Job currentJob;
    private JobBeatReceiver beatReceiver;
    private CCNotificationManager mCCNotificationManager;
    private boolean beatRegistered;

    @Override
    protected void onPause() {
        ready = false;
        unregisterReciever();
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
        beatRegistered = false;
        jobTimer = (Button) findViewById(R.id.jobTimerButton);
        actualText = (TextView) findViewById(R.id.actualText);
        estimateText = (TextView) findViewById(R.id.estimateText);

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        initJob();
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setTitle(parentProject + " > " + currentJob.getName());
        }

        if(currentJob.getCurrentState() == CCUtils.STATE_RUNNING){
            startHeart();
            Button timeButton = (Button) findViewById(R.id.jobTimerButton);
            Drawable img = this.getResources().getDrawable(R.drawable.dial_running);
            timeButton.setBackground(img);
        }
        estimate = currentJob.getEstimate();
        Log.d(TAG, "Estimate: " + estimate);
        estimateText.setText(CCUtils.msToHourMinSec(estimate));
        update();
        ready = true;
    }

    private void registerReciever(){
        // Register the receiver
        if(!beatRegistered) {
            IntentFilter filter = new IntentFilter(CCUtils.BEAT);
            beatReceiver = new JobBeatReceiver(this);
            registerReceiver(beatReceiver, filter);
            beatRegistered = true;
        }
    }

    private void unregisterReciever(){
        if(beatRegistered) {
            unregisterReceiver(beatReceiver);
            beatRegistered = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.job, menu);
        return true;
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
        registerReciever();
        Intent intent = new Intent(JobActivity.this, Kalima.class);
        JobActivity.this.startService(intent);
    }

    private void stopHeart() {
        unregisterReciever();
        Intent intent = new Intent(JobActivity.this, Kalima.class);
        JobActivity.this.stopService(intent);
    }

    private void initJob() {
        try {
            // get JSON of job file
            JSONObject jobJSON = CCUtils.fileToJSON(this.getApplicationContext(), jobUUID + ".json");

            // construct a new job object
            currentJob = new Job(jobUUID , parentProjectUUID, jobName, jobJSON);
            currentJob.setEstimate(jobJSON.getLong(CCUtils.ESTIMATE));

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
        long elapsed = CCUtils.getTotalElapsed(currentJob);

        jobTimer.setText(CCUtils.msToHourMinSec(elapsed));
        long overUnder = estimate - elapsed;
        if( overUnder<0 ){
            actualText.setText("- " + CCUtils.msToHourMinSec(-1*overUnder));
            actualText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            actualText.setText(CCUtils.msToHourMinSec(overUnder));
            actualText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

        }

    }

    public void tagButton(View v) {
        Intent intent = new Intent(JobActivity.this, TagActivity.class);
        intent.putExtra(CCUtils.NAME, jobName);
        intent.putExtra(CCUtils.FILENAME, jobUUID);
        intent.putExtra(CCUtils.TYPE, CCUtils.JOB);
        startActivity(intent);
    }

    public void notesButton(View v) {
        Intent intent = new Intent(JobActivity.this, NotesActivity.class);
        intent.putExtra(CCUtils.NAME, jobName);
        intent.putExtra(CCUtils.FILENAME, jobUUID);
        startActivity(intent);
    }

    public void detailsButton(View v) {
        Intent intent = new Intent(JobActivity.this, JobDetailsActivity.class);
        intent.putExtra(CCUtils.JOB_UUID, jobUUID);
        intent.putExtra(CCUtils.JOB_NAME, jobName);
        intent.putExtra(CCUtils.PROJECT_UUID, parentProjectUUID);
        startActivityForResult(intent, CCUtils.NEW_NAME_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CCUtils.NEW_NAME_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                jobName = data.getStringExtra(CCUtils.JOB_NAME);
                ActionBar actionBar = getActionBar();
                actionBar.setTitle(jobName);
            }
        }
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
