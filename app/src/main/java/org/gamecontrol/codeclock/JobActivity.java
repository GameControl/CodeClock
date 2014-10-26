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
import android.os.Build;
import android.widget.Button;

import java.util.UUID;


public class JobActivity extends Activity {
    private String parentProject = "";
    private TimeService.TimeContainer timeContainer;
    private Job currentJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        // Get the Intent from ProjectActivity
        Intent intent = getIntent();
        parentProject = intent.getStringExtra(ProjectActivity.EXTRA_PROJECT_NAME);

        //TODO
        currentJob = createJob();
/*        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(parentProject + " > " + currentJob.getName());
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
        if(timeContainer == null)
            timeContainer = TimeService.TimeContainer.getInstance();

        // TimeContainer holds data for a different job
        if(timeContainer.getJobUUID() != currentJob.getUUID()) {
            timeContainer.saveJob();
            timeContainer.loadJob(currentJob);
        }
        if(timeContainer.getCurrentState() == TimeService.TimeContainer.STATE_RUNNING){
            //change ring to pause
            Button timer = (Button) findViewById(R.id.jobTimerButton);
            Drawable img = view.getResources().getDrawable(R.drawable.dial_stopped);
            timer.setBackground(img);
            //pause timer
            timeContainer.pause();
            stopService();
            timer.setText(TimeService.msToHourMinSec(timeContainer.getTotalElapsed()));
        } else {
            //change ring to running
            Button timer = (Button) findViewById(R.id.jobTimerButton);
            Drawable img = view.getResources().getDrawable(R.drawable.dial_running);
            timer.setBackground(img);
            //start timer
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
