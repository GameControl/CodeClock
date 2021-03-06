package org.gamecontrol.codeclock;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.jjoe64.graphview.GraphView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class ProjectActivity extends Activity {

    private final static String TAG = "org.gamecontrol.codeclock.ProjectActivity";

    private String projectName;
    private String projectUUID;

    private ArrayList<Integer> statuses;
    private ArrayList<Long> startTimes;
    private ArrayList<Long> runningTimes;
    private Project project;
    private int toDelete;

    private void initProject(){
        try {
            // get a JSONObject of the project file
            JSONObject projectJSON = CCUtils.fileToJSON(this.getApplicationContext(), projectUUID + ".json");

            // find the array of 'tags' in the JSON and convert it to ArrayList<String>
            JSONArray tagsJSON = projectJSON.getJSONArray(CCUtils.TAGS);
            ArrayList<String> tags = CCUtils.JSONArrayToArrayListString(tagsJSON);

            // find the array of 'jobUUIDs' in the JSON and convert it to ArrayList<String>
            JSONArray jobUUIDsJSON = projectJSON.getJSONArray(CCUtils.JOB_UUIDS);
            ArrayList<String> jobUUIDs = CCUtils.JSONArrayToArrayListString(jobUUIDsJSON);

            // find the array of 'jobNames' in the JSON and convert it to ArrayList<String>
            JSONArray jobNamesJSON = projectJSON.getJSONArray(CCUtils.JOB_NAMES);
            ArrayList<String> jobNames = CCUtils.JSONArrayToArrayListString(jobNamesJSON);

            // construct a new Project object with the information we just read
            project = new Project(UUID.fromString(projectUUID), tags, projectJSON.getString(CCUtils.NOTES), jobUUIDs, jobNames);

            startTimes = new ArrayList<Long>();
            runningTimes = new ArrayList<Long>();
            statuses = new ArrayList<Integer>();

            CCUtils.harvestTimes(ProjectActivity.this, projectUUID, startTimes, runningTimes, statuses);

            addGraph();
            ActionBar actionBar = getActionBar();
            if(actionBar!=null) {
                actionBar.setTitle(projectName);
            }

        } catch (Exception e){
            Log.d(TAG, "in method initProject():" + e.toString());
        }
        if(project == null) {
            project = new Project(UUID.fromString(projectUUID));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Get the Intent from HomeActivity
        Intent intent = getIntent();
        projectName = intent.getStringExtra(CCUtils.PROJECT_NAME);
        projectUUID = intent.getStringExtra(CCUtils.PROJECT_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        initProject();
        refreshListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project, menu);
        return true;
    }

    public void createJob(View view){
        Intent intent = new Intent(ProjectActivity.this, CreateJobActivity.class);
        intent.putExtra(CCUtils.PROJECT_UUID, projectUUID);
        intent.putExtra(CCUtils.PROJECT_NAME, projectName);
        startActivity(intent);
    }

    private void refreshListView() {
        Log.d(TAG, project.getJobNames().toString());
        ListView listView = (ListView) findViewById(R.id.jobListView);
        listView.setAdapter(new JobAdapter(this, project.getJobNames(), statuses));
        Log.d(TAG, project.getJobNames().toString());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(ProjectActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProjectActivity.this, JobActivity.class);
                intent.putExtra(CCUtils.PROJECT_NAME, projectName);
                intent.putExtra(CCUtils.PROJECT_UUID, projectUUID);
                intent.putExtra(CCUtils.JOB_NAME, project.getJobNames().get(position));
                intent.putExtra(CCUtils.JOB_UUID, project.getJobUUIDs().get(position));
                startActivity(intent);
            }
        });
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                toDelete = position;
                new AlertDialog.Builder(ProjectActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete Job")
                        .setMessage("This will delete the selected job. Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CCUtils.deleteJob(ProjectActivity.this, project.getJobUUIDs().get(toDelete));
                                project.getJobNames().remove(toDelete);
                                project.getJobUUIDs().remove(toDelete);
                                try {
                                    CCUtils.JSONToFile(ProjectActivity.this, project.toJSON(), projectUUID + ".json");
                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                                //initProject();
                                refreshListView();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
    }

    public void tagButton(View v) {
        Intent intent = new Intent(ProjectActivity.this, TagActivity.class);
        intent.putExtra(CCUtils.NAME, projectName);
        intent.putExtra(CCUtils.FILENAME, projectUUID);
        intent.putExtra(CCUtils.TYPE, CCUtils.PROJECT);
        startActivity(intent);
    }

    public void notesButton(View v) {
        Intent intent = new Intent(ProjectActivity.this, NotesActivity.class);
        intent.putExtra(CCUtils.NAME, projectName);
        intent.putExtra(CCUtils.FILENAME, projectUUID);
        startActivity(intent);
    }

    public void detailsButton(View v) {
        Intent intent = new Intent(ProjectActivity.this, ProjectDetailsActivity.class);
        intent.putExtra(CCUtils.PROJECT_NAME, projectName);
        intent.putExtra(CCUtils.FILENAME, projectUUID);
        startActivityForResult(intent, CCUtils.NEW_NAME_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CCUtils.NEW_NAME_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                projectName = data.getStringExtra(CCUtils.PROJECT_NAME);
                ActionBar actionBar = getActionBar();
                if(actionBar != null)
                    actionBar.setTitle(projectName);
            }
        }
    }

    public void addGraph() {
        GraphView graphView = GraphUtils.getWeeklyFrequencyGraph(this.getApplicationContext(), startTimes, runningTimes);
        graphView.redrawAll();
        FrameLayout layout = (FrameLayout) findViewById(R.id.projectDataView);
        layout.addView(graphView);
    }
}
