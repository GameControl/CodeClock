package org.gamecontrol.codeclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;


public class HomeActivity extends Activity {

    private final static String TAG = "org.gamecontrol.codeclock.HomeActivity";

    private static ArrayList<String> fileNames;
    private static ArrayList<String> projectNames;
    private int toDelete;

    private void getProjects(){
        fileNames = new ArrayList<String>();
        projectNames = new ArrayList<String>();
        try {
            Log.d(HomeActivity.TAG, "Reading in home file");
            JSONObject homeJSON = CCUtils.fileToJSON(this.getApplicationContext(), "home.json");
            JSONArray names = homeJSON.names();
            for(int i = 0; i < names.length(); i++){
                projectNames.add(names.getString(i));
                fileNames.add(homeJSON.getString(names.getString(i)));
            }
        } catch (Exception e){
            e.toString();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        //TODO refresh list
        refreshGridView();
    }

    private void refreshGridView() {
        GridView gridview = (GridView) findViewById(R.id.projectGridView);
        getProjects();
        gridview.setAdapter(new ProjectAdapter(this, projectNames));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(HomeActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, ProjectActivity.class);
                intent.putExtra(CCUtils.PROJECT_UUID, fileNames.get(position));
                intent.putExtra(CCUtils.PROJECT_NAME, projectNames.get(position));
                startActivity(intent);
            }
        });
        gridview.setLongClickable(true);
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                toDelete = position;
                new AlertDialog.Builder(HomeActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete Project")
                        .setMessage("This will delete the selected project. Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    // Get project file to be deleted
                                    JSONObject projectJSON = CCUtils.fileToJSON(HomeActivity.this, fileNames.get(toDelete) + ".json");
                                    JSONArray jobListJSON = projectJSON.getJSONArray(CCUtils.JOB_UUIDS);
                                    ArrayList<String> jobList = CCUtils.JSONArrayToArrayListString(jobListJSON);
                                    // Delete each job listed in the project file
                                    for (String s : jobList) {
                                        CCUtils.deleteJob(HomeActivity.this, s);
                                    }
                                    // Delete the project file
                                    HomeActivity.this.deleteFile(fileNames.get(toDelete) + ".json");

                                    // Get home file, update, then save
                                    JSONObject homeJSON = CCUtils.fileToJSON(HomeActivity.this, "home.json");
                                    homeJSON.remove(projectNames.get(toDelete));
                                    CCUtils.JSONToFile(HomeActivity.this, homeJSON, "home.json");

                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                                // get data and redraw
                                refreshGridView();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initHome();
    }

    private void initHome() {
        try {
            File file = this.getFileStreamPath("home.json");
            if (!file.exists()) {
                JSONObject empty = new JSONObject();
                Log.d(HomeActivity.TAG, "INIT home.json :" + empty.toString());
                CCUtils.JSONToFile(this.getApplicationContext(), empty, "home.json");
            }

            // Create tag manager
            TagManager tagManager = TagManager.getTagManager(this.getApplicationContext());

        } catch (Exception e){
            Log.d(TAG, e.toString());
        }
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
        switch (item.getItemId()) {
            case R.id.wipe_files:
                wipeFiles();
                return true;
            case R.id.load_demo:
                loadDemo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createProject(View v) {
        Intent intent = new Intent(HomeActivity.this, CreateProjectActivity.class);
        startActivity(intent);
    }

    private void createDemoFiles() {
        Log.d(TAG, "Creating demo files");
        try {
            // Write home.json
            OutputStream out = this.openFileOutput("home.json", Context.MODE_PRIVATE);
            Writer writer = new OutputStreamWriter(out);
            writer.write(DemoUtils.HOMEFILE);
            writer.close();

            // Write the project file
            out = this.openFileOutput(DemoUtils.PROJECTUUID + ".json", Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(DemoUtils.PROJECTFILE);
            writer.close();

            // Write the job files
            out = this.openFileOutput(DemoUtils.JOB1UUID + ".json", Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(DemoUtils.JOB1FILE);
            writer.close();

            out = this.openFileOutput(DemoUtils.JOB2UUID + ".json", Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(DemoUtils.JOB2FILE);
            writer.close();

            out = this.openFileOutput(DemoUtils.JOB3UUID + ".json", Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(DemoUtils.JOB3FILE);
            writer.close();

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    private void loadDemo() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Load Demo Files")
                .setMessage("This will delete all current files and load Demo files. Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] files = HomeActivity.this.fileList();
                        for(String file : files) {
                            HomeActivity.this.deleteFile(file);
                        }
                        createDemoFiles();
                        initHome();
                        refreshGridView();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void wipeFiles() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Wipe Files")
                .setMessage("This will delete all current files. Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] files = HomeActivity.this.fileList();
                        for(String file : files) {
                            HomeActivity.this.deleteFile(file);
                        }
                        initHome();
                        refreshGridView();
                    }

                })
                .setNegativeButton("No", null)
                .show();
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
}

