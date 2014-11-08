package org.gamecontrol.codeclock;

import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * Created by illwrath on 11/7/14.
 *
 * Global TagManager. Holds tags associated with all project
 * and jobs within the app and occurrences of each one.
 */
public class TagManager{
    private static TagManager tagManager = null;
    private HashMap<String, Integer> tags;

    private TagManager() {
        tags = new HashMap<String, Integer>();
    }

    public static TagManager getTagManager() {
        if(tagManager == null)
            tagManager = new TagManager();
        return tagManager;
    }

    public void addTag(String tag) {
        tag = tag.toLowerCase();
        if(!tags.containsKey(tag))
            tags.put(tag, 1);
        else
            tags.put(tag, tags.get(tag)+1);
    }

    public int getTagCount(String tag) {
        if(tags.containsKey(tag))
            return tags.get(tag);
        throw new NoSuchElementException();
    }
}
