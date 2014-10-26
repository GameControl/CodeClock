package org.gamecontrol.codeclock;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

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
}
