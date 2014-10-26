package org.gamecontrol.codeclock;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


public class HomeActivity extends Activity {

    private static final String TAG_FILE = "Files";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        testProjectSave();
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
        // automatically handle clicks on the HomeActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        Job dummyJob = new Job();
        dummyJob.setUuid(UUID.randomUUID());
        dummyJob.setProjectUUID(projectUUID);
        dummyJob.setName("JobName" + Math.round(Math.random()*10));
        dummyJob.setEstimate(Math.round(Math.random() * 100));
        dummyJob.setNotes("Enter notes here...");
        ArrayList<String> tags = new ArrayList<String>(Arrays.asList("your", "spirit", "or", "your body!"));
        dummyJob.setTags(tags);

        return dummyJob;
    }
}

