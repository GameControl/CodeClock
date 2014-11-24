package org.gamecontrol.codeclock;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class JobSettingsActivity extends Activity {

    private String TAG = "org.gamecontrol.codeclock.JobSettingsActivity.java";
    private String jobUUID;
    private String jobName;
    private String parentProjectUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_settings);

        // Get the Intent from ProjectActivity
        Intent intent = getIntent();

        jobUUID = intent.getStringExtra(CCUtils.JOB_UUID);
        jobName = intent.getStringExtra(CCUtils.JOB_NAME);
        parentProjectUUID = intent.getStringExtra(CCUtils.PROJECT_UUID);

        // Set the Action Bar Title
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(jobName + " - Settings");
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        EditText editProjectName = (EditText) findViewById(R.id.editJobName);
        editProjectName.setText(jobName);
    }

    public void updateSettings(View v) {
        //TODO prevent empty and dupe names
        EditText newJobName = (EditText) findViewById(R.id.editJobName);
        String newJobNameString = newJobName.getText().toString();

        if (!jobName.equals(newJobNameString))
            CCUtils.changeJobName(this.getApplicationContext(), jobName, newJobNameString, jobUUID, parentProjectUUID);

        Intent resultIntent = new Intent();
        resultIntent.putExtra(CCUtils.JOB_NAME, newJobNameString);
        setResult(Activity.RESULT_OK, resultIntent);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_job_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
