package org.gamecontrol.codeclock;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;


public class CreateProjectActivity extends Activity {

    private static boolean clicked = false;
    public static final String TAG = HomeActivity.TAG + ".CreateProjectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_project, menu);
        return true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        clicked = false;
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

    public void initProject(View view) {
        if(!clicked) {
            clicked = true;
            UUID projectUUID = UUID.randomUUID();
            String projectName = ((EditText) findViewById(R.id.projectName)).getText().toString();

            Writer writer = null;
            try {
                Log.d(HomeActivity.TAG, "Beginning file IO");
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
                JSONObject homeJSON;
                if (sb.toString().length() == 0) {
                    homeJSON = new JSONObject();
                } else {
                    homeJSON = new JSONObject(sb.toString());
                }
                homeJSON.put(projectName, projectUUID);
                Log.d(HomeActivity.TAG, "Writing :" + homeJSON.toString());
                OutputStream out = this.openFileOutput("home.json", Context.MODE_PRIVATE);
                writer = new OutputStreamWriter(out);
                writer.write(homeJSON.toString());
                //TODO Check for duplicate Project Names - beta
                //TODO write project file as well

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            finish();
        }
    }
}
