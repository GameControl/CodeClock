package org.gamecontrol.codeclock;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by illwrath on 11/7/14.
 *
 * Global TagManager. Holds tags associated with all project
 * and jobs within the app and occurrences of each one.
 */
public class TagManager{
    private static TagManager tagManager = null;
    private HashMap<String, Integer> tags;
    private Context context;
    private static final String TAG = "org.gamecontrol.codeclock.TagManager";
    private static final List<String> defaultTags =
            Arrays.asList("java", "c++", "c", "c#", "php", "javascript", "haskell", "perl",
                    "python", "ruby", "scala", "html", "css", "bug_fix", "new_feature",
                    "update_feature", "refactor", "android", "ios");

    private TagManager(Context c) {
        tags = new HashMap<String, Integer>();
        context = c;

        try {
            File file = c.getFileStreamPath("tagmanager.json");
            if (!file.exists()) {
                addDefaultTags();
                JSONObject init = new JSONObject(tags);
                Log.d(TAG, "INIT tagmanager.json :" + init.toString());
                CCUtils.JSONToFile(c, init, "tagmanager.json");
            } else {
                JSONObject tagsJSON = CCUtils.fileToJSON(c, "tagmanager.json");
                Iterator<String> keysItr = tagsJSON.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    tags.put(key, (Integer) tagsJSON.get(key));
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public static TagManager getTagManager(Context c) {
        if(tagManager == null)
            tagManager = new TagManager(c);
        return tagManager;
    }

    //
    public void addTag(String tag) {
        tag = tag.toLowerCase();
        if(!tags.containsKey(tag))
            tags.put(tag, 1);
        else
            tags.put(tag, tags.get(tag)+1);
        saveTagManager();
    }
    //TODO need method that adds tag without incrementing

    // use this when removing a tag from a project or job
    public void lowerTagCount(String tag) {
        tag = tag.toLowerCase();
        if (tags.containsKey(tag)) {
            if (tags.get(tag) > 0)
                tags.put(tag, tags.get(tag) - 1);
        }
        saveTagManager();
    }

    // for completely removing a tag from the list, it's count must be 0
    private void removeTag(String tag) {
        tag = tag.toLowerCase();
        if (tags.get(tag) > 0)
            tags.remove(tag);
        else {
            Log.d(TAG, "tried to remove tag with count greater than zero");
        }
        saveTagManager();
    }

    // for adding tags to the HashMap during
    // app init, keep their 'count' at 0
    private void addDefaultTags() {
        for (String tag : defaultTags) {
            tag = tag.toLowerCase();
            if (!tags.containsKey(tag))
                tags.put(tag, 0);
        }
        saveTagManager();
    }

    public Set<String> getSetOfTags() {
        return tags.keySet();
    }

    public int getTagCount(String tag) {
        if(tags.containsKey(tag))
            return tags.get(tag);
        throw new NoSuchElementException();
    }

    private void saveTagManager() {
        JSONObject tagsJSON = new JSONObject(tags);
        CCUtils.JSONToFile(context, tagsJSON, "tagmanager.json");
    }
}
