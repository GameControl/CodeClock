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
    private UUID project;
    private UUID timerUUID;

    private String name;
    private long estimate;
    private String notes;
    private ArrayList<String> tags;

    public Job() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getProject() {
        return project;
    }

    public void setProject(UUID project) {
        this.project = project;
    }

    public UUID getTimerUUID() {
        return timerUUID;
    }

    public void setTimerUUID(UUID timerUUID) {
        this.timerUUID = timerUUID;
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

    public void setEstimate(long estimate) {
        this.estimate = estimate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Job job = (Job) o;

        if (estimate != job.estimate) return false;
        if (name != null ? !name.equals(job.name) : job.name != null) return false;
        if (notes != null ? !notes.equals(job.notes) : job.notes != null) return false;
        if (project != null ? !project.equals(job.project) : job.project != null) return false;
        if (tags != null ? !tags.equals(job.tags) : job.tags != null) return false;
        if (timerUUID != null ? !timerUUID.equals(job.timerUUID) : job.timerUUID != null)
            return false;
        if (uuid != null ? !uuid.equals(job.uuid) : job.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (project != null ? project.hashCode() : 0);
        result = 31 * result + (timerUUID != null ? timerUUID.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (estimate ^ (estimate >>> 32));
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }

    public JSONObject toJSON() throws JSONException{
        return null;
    }
}
