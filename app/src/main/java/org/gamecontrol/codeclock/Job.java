package org.gamecontrol.codeclock;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by illwrath on 10/25/14.
 */
public class Job {

    private UUID uuid;
    private UUID projectUUID;
    private String name;
    private long estimate;
    private String notes;
    private ArrayList<String> tags;
    private ArrayList<Long> startTimes;
    private ArrayList<Long> runningTimes;
    private long elapsed;

    public Job(UUID myUUID, UUID myProjectUUID, String name, long estimate, ArrayList<String> tags) {
        this.uuid = myUUID;
        this.projectUUID = myProjectUUID;
        this.name = name;
        this.estimate = estimate;
        notes = "";
        this.tags = tags;
        startTimes = new ArrayList<Long>();
        runningTimes = new ArrayList<Long>();
        elapsed = 0;
    }

    public UUID getUUID() {
        return uuid;
    }

    public UUID getProjectUUID() {
        return projectUUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getEstimate() {
        return estimate;
    }

    public void setEstimate(Long estimate) {this.estimate = estimate; }

    public void updateElapsed() {
        long total = 0;
        for(Long l: runningTimes){
            total += l;
        }
        elapsed = total;
    }

    public long getElapsed() {
        return elapsed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void addStartTime(Long time) {
        startTimes.add(time);
    }

    public Long getLastStartTime() { return startTimes.get(startTimes.size() - 1); }

    public void addRunningTimes(Long time) {
        runningTimes.add(time);
        elapsed += time;
    }

    public JSONObject toJSON() throws JSONException{
        return null;
    }
}
