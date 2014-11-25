package org.gamecontrol.codeclock;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class TagActivity extends Activity {
    private String name;
    private String filename;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        // Get the Intent from ProjectActivity
        Intent intent = getIntent();
        name = intent.getStringExtra(CCUtils.NAME);
        filename = intent.getStringExtra(CCUtils.FILENAME);
        type = intent.getStringExtra(CCUtils.TYPE);

        // Set the Action Bar Title
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(name + " - Tags");
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
