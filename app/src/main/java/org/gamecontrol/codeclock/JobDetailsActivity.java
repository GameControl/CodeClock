package org.gamecontrol.codeclock;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class JobDetailsActivity extends Activity {
    private String TAG = "org.gamecontrol.codeclock.JobDetailsActivity.java";
    private String jobUUID;
    private String jobName;
    private String parentProjectUUID;

    private static boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        // Get the Intent from ProjectActivity
        Intent intent = getIntent();

        jobUUID = intent.getStringExtra(CCUtils.JOB_UUID);
        jobName = intent.getStringExtra(CCUtils.JOB_NAME);
        parentProjectUUID = intent.getStringExtra(CCUtils.PROJECT_UUID);

        // Set the Action Bar Title
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(jobName + " - Details");
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        EditText editProjectName = (EditText) findViewById(R.id.editJobName);
        editProjectName.setText(jobName);
    }

    public void updateSettings(View v) {
        if(!clicked) {
            clicked = true;
            //TODO prevent dupe names
            EditText newJobName = (EditText) findViewById(R.id.editJobName);
            String newJobNameString = newJobName.getText().toString();

            // Warn and prevent user from creating a project with an empty name
            if (newJobNameString.equals("")) {
                Toast toast = Toast.makeText(this, "Please enter a job name.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 250);
                toast.show();
                clicked = false;
                return;
            }

            if (!jobName.equals(newJobNameString))
                CCUtils.changeJobName(this.getApplicationContext(), jobName, newJobNameString, jobUUID, parentProjectUUID);

            Intent resultIntent = new Intent();
            resultIntent.putExtra(CCUtils.JOB_NAME, newJobNameString);
            setResult(Activity.RESULT_OK, resultIntent);

            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_job_settings, menu);
        return true;
    }

}
