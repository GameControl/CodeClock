package org.gamecontrol.codeclock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by illwrath on 10/25/14.
 */
public class Job {

    private UUID uuid;
    private String projectUUID;
    private String name;
    private long estimate;
    private String notes;
    private ArrayList<String> tags;
    private ArrayList<Long> startTimes;
    private ArrayList<Long> runningTimes;
    private long elapsed;
    private int currentState;

    public Job(UUID myUUID, String myProjectUUID, String name, long estimate, ArrayList<String> tags) {
        this.uuid = myUUID;
        this.projectUUID = myProjectUUID;
        this.name = name;
        this.estimate = estimate;
        notes = "";
        if(tags == null)
            this.tags = new ArrayList<String>();
        else
            this.tags = tags;
        startTimes = new ArrayList<Long>();
        runningTimes = new ArrayList<Long>();
        elapsed = 0;
        currentState = CCUtils.STATE_INIT;
    }

    public Job(String jobUUID, String parentProjectUUID, String jobName, JSONObject jobJSON){
        // construct a new job object
        this(UUID.fromString(jobUUID), parentProjectUUID, jobName, 0 , null);
        try {
            // set job state
            currentState = (jobJSON.getInt(CCUtils.STATE));

            // set job start times
            JSONArray startTimesJSON = jobJSON.getJSONArray(CCUtils.START_TIMES);
            startTimes = CCUtils.JSONArrayToArrayListLong(startTimesJSON);

            // set job running times
            JSONArray runningTimesJSON = jobJSON.getJSONArray(CCUtils.RUNNING_TIMES);
            runningTimes = CCUtils.JSONArrayToArrayListLong(runningTimesJSON);

            // set job total elapsed time
            elapsed = (Long.valueOf(jobJSON.get(CCUtils.ELAPSED).toString()));
        } catch (Exception e) {

        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getProjectUUID() {
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

    public ArrayList<Long> getStartTimes() {
        return startTimes;
    }

    public void setStartTimes(ArrayList<Long> startTimes) {
        this.startTimes = startTimes;
    }

    public ArrayList<Long> getRunningTimes() {
        return runningTimes;
    }

    public void setRunningTimes(ArrayList<Long> runningTimes) {
        this.runningTimes = runningTimes;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    private final Object mSynchronizedObject = new Object();

    public void start() {
        if(getCurrentState() != CCUtils.STATE_RUNNING) {
            synchronized (mSynchronizedObject) {
                addStartTime(System.currentTimeMillis());
                setCurrentState(CCUtils.STATE_RUNNING);
            }
        }
    }

    public void pause() {
        if(getCurrentState() != CCUtils.STATE_PAUSED) {
            synchronized (mSynchronizedObject) {
                setCurrentState(CCUtils.STATE_PAUSED);
                long currentLap = (System.currentTimeMillis() - getLastStartTime());
                addRunningTimes(currentLap);

            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Job job = (Job) o;

        if (estimate != job.estimate) return false;
        if (name != null ? !name.equals(job.name) : job.name != null) return false;
        if (notes != null ? !notes.equals(job.notes) : job.notes != null) return false;
        if (projectUUID != null ? !projectUUID.equals(job.projectUUID) : job.projectUUID != null)
            return false;
        if (tags != null ? !tags.equals(job.tags) : job.tags != null) return false;
        if (uuid != null ? !uuid.equals(job.uuid) : job.uuid != null) return false;

        return true;
    }

    public void addStartTime(Long time) {
        startTimes.add(time);
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (projectUUID != null ? projectUUID.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (estimate ^ (estimate >>> 32));
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }

    public Long getLastStartTime() { return startTimes.get(startTimes.size() - 1); }

    public void addRunningTimes(Long time) {
        runningTimes.add(time);
        elapsed += time;
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject json = new JSONObject();

        json.put(CCUtils.NAME, name);
        json.put(CCUtils.ESTIMATE, estimate);
        json.put(CCUtils.NOTES, notes);
        json.put(CCUtils.ELAPSED, elapsed);
        json.put(CCUtils.STATE, currentState);

        JSONArray tagsArrayJSON = new JSONArray();
        json.put(CCUtils.TAGS, tagsArrayJSON);
        if (tags != null) {
            for (String t : tags)
                tagsArrayJSON.put(t);
        }

        JSONArray startTimesArrayJSON = new JSONArray();
        json.put(CCUtils.START_TIMES, startTimesArrayJSON);
        if (startTimes != null) {
            for (Long t : startTimes)
                startTimesArrayJSON.put(t);
        }

        JSONArray runningTimesArrayJSON = new JSONArray();
        json.put(CCUtils.RUNNING_TIMES, runningTimesArrayJSON);
        if (runningTimes != null) {
            for (Long t : runningTimes)
                runningTimesArrayJSON.put(t);
        }

        return json;
    }
}
