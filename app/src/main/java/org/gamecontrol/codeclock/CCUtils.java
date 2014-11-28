package org.gamecontrol.codeclock;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by tony on 10/25/2014.
 * quack
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

    // Request Codes
    public static final Integer NEW_NAME_REQUEST = 1;

    // CCUtils local variables
    private static final String TAG = "org.gamecontrol.codeclock.CCUtils";
    static final String BEAT = "org.gamecontrol.BEAT";

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

    public static void changeNotes(Context c, String filename, String new_notes) {
        Log.d(TAG, "New Notes: " + new_notes);
        Log.d(TAG, "Filename: " + filename);
        try {
            // Get the JSON
            JSONObject jsonObject = fileToJSON(c, filename);

            // Get the old notes and update them
            String old_notes = jsonObject.getString(CCUtils.NOTES);
            Log.d(TAG, "Old Notes: " + old_notes);
            jsonObject.put(CCUtils.NOTES, new_notes);

            // Save the new notes
            JSONToFile(c, jsonObject, filename);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
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

    public static void changeJobName(Context c, String oldName, String newName, String jobUUID, String parentProjectUIID) {
        try {
            // get the parent project's JSON
            JSONObject parentProjectJSON = fileToJSON(c, parentProjectUIID + ".json");

            // get the job names array and replace the oldName with the newName
            JSONArray jobNamesArrayJSON = parentProjectJSON.getJSONArray(CCUtils.JOB_NAMES);
            jobNamesArrayJSON = replaceStringInJSONArray(oldName, newName, jobNamesArrayJSON);

            // remove old array and put new one in
            parentProjectJSON.remove(CCUtils.JOB_NAMES);
            parentProjectJSON.put(CCUtils.JOB_NAMES, jobNamesArrayJSON);

            String parentProjectJSONString = parentProjectJSON.toString();

            // save modified string as the new parent project file
            OutputStream out = c.openFileOutput(parentProjectUIID + ".json", Context.MODE_PRIVATE);
            Writer writer = new OutputStreamWriter(out);
            Log.d(TAG, "changeJobName() writing modified *parent project* JSON: " + parentProjectJSONString);
            writer.write(parentProjectJSONString);
            writer.close();

            // get the job's JSON and convert it to a string for replacing
            JSONObject jobJSON = fileToJSON(c, jobUUID + ".json");
            String jobJSONString = jobJSON.toString();
            jobJSONString = jobJSONString.replace("\"NAME\":\"" + oldName + "\"", "\"NAME\":\"" + newName + "\"");

            // save modified string as the new job file
            out = c.openFileOutput(jobUUID + ".json", Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            Log.d(TAG, "changeJobName() writing modified *job* JSON: " + jobJSONString);
            writer.write(jobJSONString);
            writer.close();

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        Log.d(TAG, "Renamed job: " + oldName + " ---> " + newName);
    }

    private static JSONArray replaceStringInJSONArray(String oldVal, String newVal, JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getString(i).equals(oldVal)) {
                jsonArray.put(i, newVal);
                break;
            }
        }
        return jsonArray;
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

    public static JSONArray ArrayListStringToJSONArray(ArrayList<String> input) {
        if (input == null)
            return null;

        JSONArray output = new JSONArray();
        for (String s : input) {
            output.put(s);
        }

        return output;
    }

    public static long getTotalElapsed(Job job) {
        if(job.getCurrentState() == CCUtils.STATE_RUNNING){
            return job.getElapsed() + (System.currentTimeMillis() - job.getLastStartTime());
        } else
            return job.getElapsed();
    }

    public static String msToHourMinSec(long ms) {
        if(ms == 0) {
            return "00:00";
        } else {
            long seconds = (ms / 1000) % 60;
            long minutes = (ms / 1000) / 60;
            long hours = minutes / 60;

            StringBuilder sb = new StringBuilder();
            if(hours > 0) {
                sb.append(hours);
                sb.append(':');
            }
            if(minutes > 0) {
                minutes = minutes % 60;
                if(minutes >= 10) {
                    sb.append(minutes);
                } else {
                    sb.append(0);
                    sb.append(minutes);
                }
            } else {
                sb.append('0');
                sb.append('0');
            }
            sb.append(':');
            if(seconds > 0) {
                if(seconds >= 10) {
                    sb.append(seconds);
                } else {
                    sb.append(0);
                    sb.append(seconds);
                }
            } else {
                sb.append('0');
                sb.append('0');
            }
            return sb.toString();
        }
    }
}
