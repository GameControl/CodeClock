package org.gamecontrol.codeclock;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.Button;
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
        parentProject = intent.getStringExtra(HomeActivity.PROJECT_NAME);
        parentProjectUUID = intent.getStringExtra(HomeActivity.PROJECT_UUID);
        jobName = intent.getStringExtra(ProjectActivity.JOB_NAME);
        jobUUID = intent.getStringExtra(ProjectActivity.JOB_UUID);

        //TODO
        jobTimer = (Button) findViewById(R.id.jobTimerButton);
        handler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        initJob();

        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setTitle(parentProject + " > " + currentJob.getName());
        }

        if(timeContainer == null)
            timeContainer = TimeService.TimeContainer.getInstance();

        // If TimeContainer holds data for a different job, save that progress, and load the current job
        if(timeContainer.getJobUUID() != currentJob.getUUID()) {
            timeContainer.saveJob();
            timeContainer.loadJob(currentJob);
        }

        if(currentJob.getCurrentState() == Job.STATE_RUNNING){
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
                if (timeContainer.getCurrentState() == Job.STATE_RUNNING) {
                    //change ring to pause
                    Button timeButton = (Button) findViewById(R.id.jobTimerButton);
                    Drawable img = view.getResources().getDrawable(R.drawable.dial_stopped);
                    timeButton.setBackground(img);
                    //pause timer
                    timeContainer.pause();
                    stopService();
                } else {
                    //change ring to running
                    Button timeButton = (Button) findViewById(R.id.jobTimerButton);
                    Drawable img = view.getResources().getDrawable(R.drawable.dial_running);
                    timeButton.setBackground(img);
                    //start timer
                    timer = new Timer();
                    timeContainer.start();
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
            Log.d(TAG, "Reading a job file in initJob()");
            Log.d(TAG, "Opening file:" + jobUUID + ".json");

            InputStream in = this.openFileInput(jobUUID + ".json");
            JSONObject jobJSON = CCUtils.fileToJSON(in);

            currentJob = new Job(UUID.fromString(jobUUID), parentProjectUUID, jobName, 0, null);
            Log.d(TAG, "new Job");

            currentJob.setCurrentState(jobJSON.getInt(Job.STATE));

            JSONArray startTimesJSON = jobJSON.getJSONArray(Job.START_TIMES);
            ArrayList<Long> startTimesArray = CCUtils.JSONArrayToArrayListLong(startTimesJSON);
            currentJob.setStartTimes(startTimesArray);
            Log.d(TAG, "set start times" + currentJob.getStartTimes().toString());

            JSONArray runningTimesJSON = jobJSON.getJSONArray(Job.RUNNING_TIMES);
            ArrayList<Long> runningTimesArray = CCUtils.JSONArrayToArrayListLong(runningTimesJSON);
            currentJob.setRunningTimes(runningTimesArray);
            Log.d(TAG, "set running times" + currentJob.getRunningTimes().toString());

            currentJob.setElapsed(Long.valueOf(jobJSON.get(Job.ELAPSED).toString()));
            Log.d(TAG, "jobJSON.get(Job.ELAPSED) = " + jobJSON.get(Job.ELAPSED));


        } catch (Exception e) {
            Log.d(TAG, "caught exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void markComplete(View view){
        Toast toast = Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT);
        toast.show();
//        finish();
    }

    private void updateTimeText() {
        jobTimer.setText(TimeService.msToHourMinSec(timeContainer.getTotalElapsed()));
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
