package org.gamecontrol.codeclock;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.Button;

import java.util.Timer;
import java.util.UUID;
import java.util.TimerTask;


public class JobActivity extends Activity{
    private String parentProject = "";
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
        parentProject = intent.getStringExtra(ProjectActivity.EXTRA_PROJECT_NAME);

        //TODO
        jobTimer = (Button) findViewById(R.id.jobTimerButton);
        handler = new Handler();
        currentJob = createJob();

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(parentProject + " > " + currentJob.getName());
        if(timeContainer == null)
            timeContainer = TimeService.TimeContainer.getInstance();

        // If TimeContainer holds data for a different job, save that progress, and load the current job
        if(timeContainer.getJobUUID() != currentJob.getUUID()) {
            timeContainer.saveJob();
            timeContainer.loadJob(currentJob);
        }
        startUpdateTimer();
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggleTimer(View view) {
        startService();
        if(timeContainer.getCurrentState() == TimeService.TimeContainer.STATE_RUNNING){
            //change ring to pause
            Button timeButton = (Button) findViewById(R.id.jobTimerButton);
            Drawable img = view.getResources().getDrawable(R.drawable.dial_stopped);
            timeButton.setBackground(img);
            //pause timer
            timeContainer.pause();
            stopService();
            timer.cancel();
            timer = null;
            //timeButton.setText(TimeService.msToHourMinSec(timeContainer.getTotalElapsed()));
        } else {
            //change ring to running
            Button timeButton = (Button) findViewById(R.id.jobTimerButton);
            Drawable img = view.getResources().getDrawable(R.drawable.dial_running);
            timeButton.setBackground(img);
            //start timer
            timer = new Timer();
            timeContainer.start();
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

    private Job createJob(){
        return new Job(UUID.randomUUID(), UUID.randomUUID(), "testJob", 0, null);
    }

    public void markComplete(View view){

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
