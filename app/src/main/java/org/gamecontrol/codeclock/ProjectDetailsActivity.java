package org.gamecontrol.codeclock;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class ProjectDetailsActivity extends Activity {
    private String TAG = "org.gamecontrol.codeclock.ProjectDetailsActivity.java";
    private String projectName;
    private static boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        // Get the Intent from ProjectActivity
        Intent intent = getIntent();
        projectName = intent.getStringExtra(CCUtils.PROJECT_NAME);

        // Set the Action Bar Title
        ActionBar actionBar = getActionBar();
        if(actionBar!= null)
            actionBar.setTitle(projectName + " - Details");
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        EditText editProjectName = (EditText) findViewById(R.id.editProjectName);
        editProjectName.setText(projectName);
    }

    public void updateSettings(View v) {
        if(!clicked) {
            clicked = true;
            //TODO prevent dupe names
            EditText newProjectName = (EditText) findViewById(R.id.editProjectName);
            String newProjectNameString = newProjectName.getText().toString();

            // Warn and prevent user from creating a project with an empty name
            if (newProjectNameString.equals("")) {
                Toast toast = Toast.makeText(this, "Please enter a project name.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 250);
                toast.show();
                clicked = false;
                return;
            }

            if (!projectName.equals(newProjectNameString))
                CCUtils.changeProjectName(this.getApplicationContext(), projectName, newProjectNameString);

            Intent resultIntent = new Intent();
            resultIntent.putExtra(CCUtils.PROJECT_NAME, newProjectNameString);
            setResult(Activity.RESULT_OK, resultIntent);

            clicked = false;
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project_settings, menu);
        return true;
    }
}
