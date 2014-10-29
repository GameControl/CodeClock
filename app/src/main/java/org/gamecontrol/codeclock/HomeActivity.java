package org.gamecontrol.codeclock;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;


public class HomeActivity extends Activity {

    private final static String TAG = "org.gamecontrol.codeclock.HomeActivity";
    public final static String PROJECT_UUID = "org.gamecontrol.codeclock.PROJECT_UUID";
    public final static String PROJECT_NAME = "org.gamecontrol.codeclock.PROJECT_NAME";

    private static ArrayList<String> fileNames;
    private static ArrayList<String> projectNames;

    private void getProjects(){
        fileNames = new ArrayList<String>();
        projectNames = new ArrayList<String>();
        try {
            Log.d(HomeActivity.TAG, "Reading in projects");
            InputStream in = this.openFileInput("home.json");
            InputStreamReader streamReader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(streamReader);
            String read = reader.readLine();

            StringBuilder sb = new StringBuilder();
            while (read != null) {
                //System.out.println(read);
                sb.append(read);
                read = reader.readLine();
            }
            JSONObject homeJSON = new JSONObject(sb.toString());
            JSONArray names = homeJSON.names();
            for(int i = 0; i < names.length(); i++){
                projectNames.add(names.getString(i));
                fileNames.add(homeJSON.getString(names.getString(i)));
            }
        } catch (Exception e){
            e.printStackTrace();
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
                intent.putExtra(PROJECT_UUID, fileNames.get(position));
                intent.putExtra(PROJECT_NAME, projectNames.get(position));
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
        Writer writer = null;
        try {
            File file = this.getFileStreamPath("home.json");
            if (!file.exists()) {
                JSONObject empty = new JSONObject();
                Log.d(HomeActivity.TAG, "INIT home.json :" + empty.toString());
                OutputStream out = this.openFileOutput("home.json", Context.MODE_PRIVATE);
                writer = new OutputStreamWriter(out);
                writer.write(empty.toString());
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
            case R.id.action_settings:
                //openSettings();
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
        String[] files = this.fileList();
        for(String file : files) {
            this.deleteFile(file);
        }
        initHome();
        refreshGridView();
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

