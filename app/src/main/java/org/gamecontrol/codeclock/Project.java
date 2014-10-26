package org.gamecontrol.codeclock;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by illwrath on 10/25/14.
 */
public class Project {

    private static final String JSON_UUID = "uuid";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_NOTES = "notes";
    private static final String JSON_JOBS = "jobs";

    private UUID uuid;
    private ArrayList<String> tags;
    private String notes;
    private ArrayList<UUID> jobs;

    public Project() {
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

    public ArrayList<UUID> getJobs() {
        return jobs;
    }

    public void setJobs(ArrayList<UUID> jobs) {
        this.jobs = jobs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (jobs != null ? !jobs.equals(project.jobs) : project.jobs != null) return false;
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
        result = 31 * result + (jobs != null ? jobs.hashCode() : 0);
        return result;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_UUID, uuid.toString());
        json.put(JSON_NOTES, notes);
        return json;
    }

}
