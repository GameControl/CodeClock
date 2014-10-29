package org.gamecontrol.codeclock;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;


public class ProjectActivity extends Activity {

    private final static String TAG = "org.gamecontrol.codeclock.ProjectActivity";
    public final static String JOB_UUID = "org.gamecontrol.codeclock.JOB_UUID";
    public final static String JOB_NAME = "org.gamecontrol.codeclock.JOB_NAME";
    private final static String JOB_KEY = "jobs";

    private String projectName;
    private String projectUUID;

    private Project project;

    private void initProject(){
        try {
            Log.d(TAG, "Reading a project in initProject()");
            Log.d(TAG, "Opening file:" + projectUUID + ".json");
            InputStream in = this.openFileInput(projectUUID + ".json");
            InputStreamReader streamReader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(streamReader);
            String read = reader.readLine();
            Log.d(TAG, "Finished opening file and creating reader");

            StringBuilder sb = new StringBuilder();
            while (read != null) {
                //System.out.println(read);
                sb.append(read);
                read = reader.readLine();
            }
            Log.d(TAG, "Read: " + sb.toString());

            JSONObject projectJSON = new JSONObject(sb.toString()); //(JSONObject) new JSONTokener(sb.toString()).nextValue();
            //create the project

            Log.d(TAG, "After projectJSON init");

            JSONArray tagsJSON = projectJSON.getJSONArray(Project.TAGS);
            ArrayList<String> tags = new ArrayList<String>();
            for(int i = 0; i < tagsJSON.length(); i++){
                tags.add(tagsJSON.getString(i));
            }

            JSONArray jobUUIDJSON = projectJSON.getJSONArray(Project.JOB_UUIDS);
            ArrayList<String> jobUUIDs = new ArrayList<String>();
            for(int i = 0; i < jobUUIDJSON.length(); i++){
                jobUUIDs.add(jobUUIDJSON.getString(i));
            }

            JSONArray jobNameJSON = projectJSON.getJSONArray(Project.JOB_NAMES);
            ArrayList<String> jobNames = new ArrayList<String>();
            for(int i = 0; i < jobNameJSON.length(); i++){
                jobNames.add(jobNameJSON.getString(i));
            }

            project = new Project(UUID.fromString(projectUUID), tags, projectJSON.getString(Project.NOTES), jobUUIDs, jobNames);
            ActionBar actionBar = getActionBar();
            actionBar.setTitle(projectName);

        } catch (Exception e){
            e.printStackTrace();
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
        projectName = intent.getStringExtra(HomeActivity.PROJECT_NAME);
        projectUUID = intent.getStringExtra(HomeActivity.PROJECT_UUID);
        addGraph();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createJob(View view){
        Intent intent = new Intent(ProjectActivity.this, CreateJobActivity.class);
        intent.putExtra(HomeActivity.PROJECT_UUID, projectUUID);
        intent.putExtra(HomeActivity.PROJECT_NAME, projectName);
        startActivity(intent);
    }

    private void refreshListView() {
        Log.d(TAG, project.getJobNames().toString());
        ListView listView = (ListView) findViewById(R.id.jobListView);
        listView.setAdapter(new JobAdapter(this, project.getJobNames()));
        Log.d(TAG, project.getJobNames().toString());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(ProjectActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProjectActivity.this, JobActivity.class);
                intent.putExtra(HomeActivity.PROJECT_NAME, projectName);
                intent.putExtra(HomeActivity.PROJECT_UUID, projectUUID);
                intent.putExtra(ProjectActivity.JOB_NAME, project.getJobNames().get(position));
                intent.putExtra(ProjectActivity.JOB_UUID, project.getJobUUIDs().get(position));
                startActivity(intent);
            }
        });
    }

    public void addGraph() {
        // draw sin curve
        int num = 150;
        GraphView.GraphViewData[] data = new GraphView.GraphViewData[num];
        double v=0;
        for (int i=0; i<num; i++) {
            v += 0.2;
            data[i] = new GraphView.GraphViewData(i, Math.sin(v));
        }
        GraphView graphView = new BarGraphView(
                this
                , "GraphViewDemo"
        );
        // add data
        graphView.addSeries(new GraphViewSeries(data));
        // set view port, start=2, size=40
        graphView.setViewPort(2, 40);
        graphView.setScrollable(true);
        // optional - activate scaling / zooming
        graphView.setScalable(true);

        FrameLayout layout = (FrameLayout) findViewById(R.id.projectDataView);
        layout.addView(graphView);
    }
}
