package org.gamecontrol.codeclock;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class ProjectActivity extends Activity {
    public final static String EXTRA_PROJECT_NAME = "org.gamecontrol.codeclock.PROJECT_NAME";
    public final static String EXTRA_JOB_NAME = "org.gamecontrol.codeclock.JOB_NAME";
    private String projectName = "";

    private ArrayList<String> getJobs(){
        ArrayList<String> output = new ArrayList<String>();
        for(int i=0; i < 20; i++){
            output.add("Job " + i);
        }
        return output;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Get the Intent from HomeActivity
        Intent intent = getIntent();
        projectName = intent.getStringExtra(HomeActivity.PROJECT_NAME);

        ListView listView = (ListView) findViewById(R.id.jobListView);
        listView.setAdapter(new JobAdapter(this, getJobs()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(ProjectActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProjectActivity.this, JobActivity.class);
                intent.putExtra(EXTRA_PROJECT_NAME, projectName);
                intent.putExtra(EXTRA_JOB_NAME, "" + position);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(projectName);
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
        startActivity(intent);

    }
}
