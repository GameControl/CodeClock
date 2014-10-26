package org.gamecontrol.codeclock;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


public class HomeActivity extends Activity {

    public final static String EXTRA_MESSAGE = "org.gamecontrol.codeclock.MESSAGE";
    private ArrayList<String> getProjects(){
        ArrayList<String> output = new ArrayList<String>();
        for(int i=0; i < 20; i++){
            output.add("Project " + i);
        }
        return output;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        testProjectSave();

        GridView gridview = (GridView) findViewById(R.id.projectGridView);

        gridview.setAdapter(new ProjectAdapter(this, getProjects()));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(HomeActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, ProjectActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "" + position);
                startActivity(intent);
            }
        });

//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

    public void createProject(View v) {
        Intent intent = new Intent(HomeActivity.this, CreateProjectActivity.class);
        startActivity(intent);

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;
        }
    }

    public void testProjectSave() {
        // Make dummy projects and jobs
        Project testProject = makeDummyProject();

        CodeClockJSONSerializer testSerializer = new CodeClockJSONSerializer(this);
        try {
            testSerializer.saveProject(testProject, testProject.getUuid().toString() + ".json");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Project makeDummyProject() {
        Project dummyProject = new Project();

        dummyProject.setUuid(UUID.randomUUID());

        ArrayList<Job> jobs = new ArrayList<Job>();
        jobs.add(makeDummyJob(dummyProject.getUuid()));
        jobs.add(makeDummyJob(dummyProject.getUuid()));
        jobs.add(makeDummyJob(dummyProject.getUuid()));
        dummyProject.setJobs(jobs);

        ArrayList<String> tags = new ArrayList<String>(Arrays.asList("Peace", "has", "sapped", "your", "strength."));
        dummyProject.setTags(tags);

        dummyProject.setNotes("Victory has defeated you.");



        return dummyProject;
    }

    public Job makeDummyJob(UUID projectUUID) {
        Job dummyJob = new Job(UUID.randomUUID(), projectUUID, "", 1, null);

        dummyJob.setName("JobName" + Math.round(Math.random()*10));
        dummyJob.setEstimate(Math.round(Math.random() * 100));
        dummyJob.setNotes("Enter notes here...");
        ArrayList<String> tags = new ArrayList<String>(Arrays.asList("your", "spirit", "or", "your body!"));
        dummyJob.setTags(tags);

        return dummyJob;
    }
}

