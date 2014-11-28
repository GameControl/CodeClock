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
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class TagActivity extends Activity {
    private String name;
    private String filename;
    private TagManager tagManager;
    private ArrayList<String> currentTags;
    private ArrayList<String> commonTags;
    private String type;

    private final static String TAG = "org.gamecontrol.codeclock.JobActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        // Get the Intent from ProjectActivity
        Intent intent = getIntent();
        name = intent.getStringExtra(CCUtils.NAME);
        filename = intent.getStringExtra(CCUtils.FILENAME) + ".json";
        type = intent.getStringExtra(CCUtils.TYPE);

        // Set the Action Bar Title
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(name + " - Tags");
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        tagManager = TagManager.getTagManager(this.getApplicationContext());

        refreshGridViews();
    }

    private void refreshGridViews() {
        refreshCommonTagsGridView();
        refreshCurrentTagsGridView();
    }

    private void refreshCommonTagsGridView() {
        GridView gridview = (GridView) findViewById(R.id.CommonTagsGridView);
        getCommonTags();
        gridview.setAdapter(new TagAdapter(this, commonTags));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                CCUtils.addTag(TagActivity.this, filename, commonTags.get(position), type.equals(CCUtils.JOB));
                refreshGridViews();
            }
        });
    }

    private void getCommonTags() {
        commonTags = new ArrayList<String>(tagManager.getSetOfTags());
    }

    private void refreshCurrentTagsGridView() {
        GridView gridview = (GridView) findViewById(R.id.CurrentTagsGridView);
        getCurrentTags();
        gridview.setAdapter(new TagAdapter(this, currentTags));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                CCUtils.removeTag(TagActivity.this, filename, position, type.equals(CCUtils.JOB));
                refreshGridViews();
            }
        });
    }

    private void getCurrentTags() {
        try {
            // Get the JSON
            JSONObject jsonObject = CCUtils.fileToJSON(this, filename);

            // Get the current tags
            JSONArray tags = jsonObject.getJSONArray(CCUtils.TAGS);
            currentTags = CCUtils.JSONArrayToArrayListString(tags);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        for(String tag : currentTags) {
            commonTags.remove(tag);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                HomeActivity.openSettings(TagActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
