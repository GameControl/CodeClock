package org.gamecontrol.codeclock;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by tony on 10/25/2014.
 */
public class CCUtils {
    // APP CONSTANTS
    public static final int STATE_INIT = 0;
    public static final int STATE_PAUSED = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_COMPLETE = 3;

    public static final String APP_NAME = "CodeClock";
    public final static String FILENAME = "FILENAME";
    public final static String TYPE = "TYPE";
    public final static String JOB = "JOB";
    public final static String PROJECT = "PROJECT";
    public final static String NAME = "NAME";
    public final static String ESTIMATE = "ESTIMATE";
    public final static String START_TIMES = "START_TIMES";
    public final static String RUNNING_TIMES = "RUNNING_TIMES";
    public final static String ELAPSED = "ELAPSED";
    public final static String STATE = "STATE";
    public final static String UUID = "UUID";
    public final static String TAGS = "TAGS";
    public final static String NOTES = "NOTES";

    public final static String PROJECT_UUID = "PROJECT_UUID";
    public final static String JOB_UUID = "JOB_UUID";

    public final static String JOB_UUIDS = "JOB_UUIDS";
    public final static String JOB_NAMES = "JOB_NAMES";

    public final static String JOB_NAME = "JOB_NAME";
    public final static String PROJECT_NAME = "PROJECT_NAME";

    // CCUtils local variables
    private static final String TAG = "org.gamecontrol.codeclock.CCUtils";

    // Method for saving a JSON object to a file
    public static void JSONToFile(Context c, JSONObject json, String filename) {
        Log.d(TAG, "beginning to save file: " + filename);
        try {
            OutputStream out = c.openFileOutput(filename, Context.MODE_PRIVATE);
            Writer writer = new OutputStreamWriter(out);
            writer.write(json.toString());
            writer.close();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        Log.d(TAG, "Wrote: " + json.toString());
    }

    public static void changeProjectName(Context c, String oldName, String newName) {
        try {
            // get the JSON
            JSONObject homeJSON = fileToJSON(c, "home.json");

            // convert to string and replace
            String homeJSONString = homeJSON.toString();
            homeJSONString = homeJSONString.replace(oldName, newName);

            // save modified string as the new 'home.json' file
            OutputStream out = c.openFileOutput("home.json", Context.MODE_PRIVATE);
            Writer writer = new OutputStreamWriter(out);
            Log.d(TAG, "changeProjectName() writing modified JSON: " + homeJSONString);
            writer.write(homeJSONString);
            writer.close();

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        Log.d(TAG, "Renamed project: " + oldName + " ---> " + newName);
    }

    public static JSONObject fileToJSON(Context c, String filename) {
        Log.d(TAG, "beginning to read file: " + filename);
        try {
            InputStream in = c.openFileInput(filename);
            InputStreamReader streamReader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(streamReader);
            String read = reader.readLine();
            StringBuilder sb = new StringBuilder();
            while (read != null) {
                sb.append(read);
                read = reader.readLine();
            }
            reader.close();
            JSONObject readJSON = (JSONObject) new JSONTokener(sb.toString()).nextValue();
            Log.d(TAG, "fileToJSON() read in: " + readJSON);
            return readJSON;

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        return null;
    }

    public static ArrayList<Long> JSONArrayToArrayListLong(JSONArray input) throws JSONException {
        ArrayList<Long> output = new ArrayList<Long>();
        Number entry;

        if (input == null)
            return null;

        for (int i = 0; i < input.length(); i++) {
            entry = (Number) input.get(i);
            output.add(entry.longValue());
        }

        return output;
    }

    public static ArrayList<String> JSONArrayToArrayListString(JSONArray input) throws JSONException {
        ArrayList<String> output = new ArrayList<String>();

        if (input == null)
            return null;

        for (int i = 0; i < input.length(); i++) {
            output.add(input.get(i).toString());
        }

        return output;
    }
}
