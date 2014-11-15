package org.gamecontrol.codeclock;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ProjectSettingsActivity extends Activity {

    private String TAG = "org.gamecontrol.codeclock.ProjectSettingsActivity.java";
    private String filename;
    private String projectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_settings);

        // Get the Intent from ProjectActivity
        Intent intent = getIntent();
        projectName = intent.getStringExtra(CCUtils.PROJECT_NAME);
        filename = intent.getStringExtra(CCUtils.FILENAME);

        // Set the Action Bar Title
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(projectName + " - Settings");
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        EditText editProjectName = (EditText) findViewById(R.id.editProjectName);
        editProjectName.setText(projectName);
    }

    public void updateSettings(View v) {
        try {
            InputStream in = this.openFileInput("home.json");
            OutputStream out = this.openFileOutput("home.json", Context.MODE_PRIVATE);
            EditText newProjectName = (EditText) findViewById(R.id.editProjectName);
            Log.d(TAG, "Trying to change project name to: " + newProjectName.getText().toString());
            CCUtils.changeProjectName(in, out, projectName, newProjectName.getText().toString());
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project_settings, menu);
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
