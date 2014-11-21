package org.gamecontrol.codeclock;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;


public class CreateJobActivity extends Activity {
    private static boolean clicked = false;
    public static final String TAG = "org.gamecontrol.codeclock.CreateJobActivity";

    private String parentProjectName;
    private String parentProjectUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);
        NumberPicker hours = (NumberPicker) findViewById(R.id.pickHours);
        hours.setMinValue(0);
        hours.setMaxValue(99);
        NumberPicker minutes = (NumberPicker) findViewById(R.id.pickMinutes);
        minutes.setMinValue(0);
        minutes.setMaxValue(59);

        // Get the Intent from ProjectActivity
        Intent intent = getIntent();
        parentProjectName = intent.getStringExtra(CCUtils.PROJECT_NAME);
        parentProjectUUID = intent.getStringExtra(CCUtils.PROJECT_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("New Job");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        clicked = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_job, menu);
        return true;
    }

    public void initNewJob(View view) {
        if(!clicked) {
            clicked = true;
            UUID jobUUID = UUID.randomUUID();
            String jobName = ((EditText) findViewById(R.id.jobName)).getText().toString();
            //TODO get this from view once added
            ArrayList<String> tags =  new ArrayList<String>();
            NumberPicker hours = (NumberPicker) findViewById(R.id.pickHours);
            NumberPicker minutes = (NumberPicker) findViewById(R.id.pickMinutes);

            long estimate = (minutes.getValue() + ( hours.getValue() * 60) ) * 60000;

            if (jobName.equals("")) {
                Toast toast = Toast.makeText(this, "Please enter a job name.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 250);
                toast.show();
                clicked = false;
                return;
            }

            try {
                // open parent project's file to prevent duplicate job names and save new name/UUID
                JSONObject projectJSON = CCUtils.fileToJSON(this.getApplicationContext(), parentProjectUUID + ".json");

                if (projectJSON.has(jobName)) {
                        Toast toast = Toast.makeText(this, "A job with that name already exists.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 250);
                        toast.show();
                        clicked = false;
                        return;
                }

                // add name and UUID to json file and save it out
                projectJSON.accumulate(CCUtils.JOB_NAMES, jobName);
                projectJSON.accumulate(CCUtils.JOB_UUIDS, jobUUID);
                CCUtils.JSONToFile(this.getApplicationContext(), projectJSON, parentProjectUUID + ".json");

                // construct a job and save in a new file
                Job newJob = new Job(jobUUID, parentProjectUUID, jobName, estimate, tags);
                CCUtils.JSONToFile(this.getApplicationContext(), newJob.toJSON(), jobUUID.toString() + ".json");

            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }

            finish();
        }
    }
}
