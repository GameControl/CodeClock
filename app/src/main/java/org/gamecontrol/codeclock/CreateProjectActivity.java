package org.gamecontrol.codeclock;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.UUID;


public class CreateProjectActivity extends Activity {

    private static boolean clicked = false;
    public static final String TAG = "org.gamecontrol.codeclock.CreateProjectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("New Project");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_project, menu);
        return true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        clicked = false;
    }

    public void initNewProject(View view) {
        if(!clicked) {
            clicked = true;
            UUID projectUUID = UUID.randomUUID();
            String projectName = ((EditText) findViewById(R.id.projectName)).getText().toString();

            // Warn and prevent user from creating a project with an empty name
            if (projectName.equals("")) {
                Toast toast = Toast.makeText(this, "Please enter a project name.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 250);
                toast.show();
                clicked = false;
                return;
            }

            // open home so that we can check if user is trying to use a name that already exists
            JSONObject homeJSON = CCUtils.fileToJSON(this.getApplicationContext(), "home.json");
            if (homeJSON.has(projectName)) {
                Toast toast = Toast.makeText(this, "A project with that name already exists.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 250);
                toast.show();
                clicked = false;
                return;
            }

            try {
                // add the project name and UUID to the home.json file
                homeJSON.put(projectName, projectUUID);
                CCUtils.JSONToFile(this.getApplicationContext(), homeJSON, "home.json");

                // construct a project and save it in a new file
                Project newProject = new Project(projectUUID);
                CCUtils.JSONToFile(this.getApplicationContext(), newProject.toJSON(), projectUUID + ".json");
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            finish();
        }
    }
}
