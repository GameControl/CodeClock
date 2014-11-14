package org.gamecontrol.codeclock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by illwrath on 10/25/14.
 */
public class Project {

    private UUID uuid;
    private ArrayList<String> tags;
    private String notes = "";
    private ArrayList<String> jobUUIDs;
    private ArrayList<String> jobNames;

    public Project() {
        tags = new ArrayList<String>();
        jobUUIDs = new ArrayList<String>();
        jobNames = new ArrayList<String>();
    }

    public Project(UUID uuid, ArrayList<String> tags, String notes, ArrayList<String> jobUUIDs, ArrayList<String> jobNames) {
        this.uuid = uuid;
        this.tags = tags;
        this.notes = notes;
        this.jobUUIDs = jobUUIDs;
        this.jobNames =  jobNames;

    }
    public Project(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ArrayList<String> getJobUUIDs() {
        return jobUUIDs;
    }

    public void setJobUUIDs(ArrayList<String> jobUUIDs) {
        this.jobUUIDs = jobUUIDs;
    }

    public ArrayList<String> getJobNames() {
        return jobNames;
    }

    public void setJobNames(ArrayList<String> jobNames) {
        this.jobNames = jobNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (jobUUIDs != null ? !jobUUIDs.equals(project.jobUUIDs) : project.jobUUIDs != null) return false;
        if (notes != null ? !notes.equals(project.notes) : project.notes != null) return false;
        if (tags != null ? !tags.equals(project.tags) : project.tags != null) return false;
        if (uuid != null ? !uuid.equals(project.uuid) : project.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (jobUUIDs != null ? jobUUIDs.hashCode() : 0);
        return result;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(CCUtils.UUID, uuid.toString());

        JSONArray tagsArrayJSON = new JSONArray();
        json.put(CCUtils.TAGS, tagsArrayJSON);
        if (tags != null) {
            for (String t : tags)
                tagsArrayJSON.put(t);
        }

        json.put(CCUtils.NOTES, notes);

        JSONArray jobsArrayJSON = new JSONArray();
        json.put(CCUtils.JOB_UUIDS, jobsArrayJSON);
        if (jobUUIDs != null) {
            for (String j : jobUUIDs)
                jobsArrayJSON.put(j);
        }

        JSONArray jobNamesArrayJSON = new JSONArray();
        json.put(CCUtils.JOB_NAMES, jobNamesArrayJSON);
        if (jobNames != null) {
            for (String j : jobNames)
                jobNamesArrayJSON.put(j);
        }

        return json;
    }

}
