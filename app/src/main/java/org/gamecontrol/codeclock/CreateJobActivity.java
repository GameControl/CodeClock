package org.gamecontrol.codeclock;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

            Writer writer = null;
            try {
                Log.d(TAG, "Beginning file IO");
                InputStream in = this.openFileInput(parentProjectUUID + ".json");
                InputStreamReader streamReader = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(streamReader);
                String read = reader.readLine();

                StringBuilder sb = new StringBuilder();
                while (read != null) {
                    //System.out.println(read);
                    sb.append(read);
                    read = reader.readLine();
                }
                Log.d(TAG, "Read: " + sb.toString());

                JSONObject projectJSON = new JSONObject(sb.toString());
                JSONArray jobNames = projectJSON.getJSONArray(CCUtils.JOB_NAMES);
                for(int i = 0; i < jobNames.length(); ++i) {
                    if (jobNames.get(i).equals(jobName)) {
                        Toast toast = Toast.makeText(this, "A job with that name already exists.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 250);
                        toast.show();
                        clicked = false;
                        return;
                    }
                }

                projectJSON.accumulate(CCUtils.JOB_NAMES, jobName);
                projectJSON.accumulate(CCUtils.JOB_UUIDS, jobUUID);
                Log.d(TAG, "Writing :" + projectJSON.toString());
                OutputStream out = this.openFileOutput(parentProjectUUID + ".json", Context.MODE_PRIVATE);
                writer = new OutputStreamWriter(out);
                writer.write(projectJSON.toString());

                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Job newJob = new Job(jobUUID, parentProjectUUID, jobName, estimate, tags);

                out = this.openFileOutput(jobUUID.toString() + ".json", Context.MODE_PRIVATE);
                writer = new OutputStreamWriter(out);
                writer.write(newJob.toJSON().toString());
                Log.d(TAG, "Writing Job: " + newJob.getUUID());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            finish();
        }
    }
}
