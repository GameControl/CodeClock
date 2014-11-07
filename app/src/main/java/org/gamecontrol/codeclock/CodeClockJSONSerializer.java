package org.gamecontrol.codeclock;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
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
public class CodeClockJSONSerializer {
    private static final String TAG = "JSONSerializer";
    private Context mContext;

    public CodeClockJSONSerializer(Context c) {
        mContext = c;
    }

    public void saveProject(Project p, String filename) throws JSONException, IOException {

        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(p.toJSON().toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public static JSONObject fileToJSON(InputStream in) {
        InputStreamReader streamReader = new InputStreamReader(in);
        BufferedReader reader = new BufferedReader(streamReader);
        try {
            String read = reader.readLine();
            StringBuilder sb = new StringBuilder();
            while (read != null) {
                sb.append(read);
                read = reader.readLine();
            }

            return (JSONObject) new JSONTokener(sb.toString()).nextValue();

        } catch (Exception e) {
            e.printStackTrace();
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
}
