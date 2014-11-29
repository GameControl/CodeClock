package org.gamecontrol.codeclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.ArrayList;


public class HomeActivity extends Activity {

    private final static String TAG = "org.gamecontrol.codeclock.HomeActivity";

    private static ArrayList<String> fileNames;
    private static ArrayList<String> projectNames;

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createProject(View v) {
        Intent intent = new Intent(HomeActivity.this, CreateProjectActivity.class);
        startActivity(intent);

    }

    private void wipeFiles() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Wipe Files")
                .setMessage("Are you sure you want to delete all files?")
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

