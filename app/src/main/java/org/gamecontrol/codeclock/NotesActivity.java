package org.gamecontrol.codeclock;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;


public class NotesActivity extends Activity {
    private String TAG = "org.gamecontrol.codeclock.NotesActivity.java";
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        // Get the Intent
        Intent intent = getIntent();
        String name = intent.getStringExtra(CCUtils.NAME);
        filename = intent.getStringExtra(CCUtils.FILENAME) + ".json";

        // Set the Action Bar Title
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(name + " - Notes");
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Display the Project or Job's current notes
        try {
            // Get the JSON
            JSONObject jsonObject = CCUtils.fileToJSON(NotesActivity.this, filename);

            // Get the current notes
            String notes = jsonObject.getString(CCUtils.NOTES);

            EditText editNotes = (EditText) findViewById(R.id.notes);
            editNotes.setText(notes);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public void updateNotes(View v) {
        EditText editNotes = (EditText) findViewById(R.id.notes);
        String notes = editNotes.getText().toString();

        CCUtils.changeNotes(this.getApplicationContext(), filename, notes);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_description, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                HomeActivity.openSettings(NotesActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
