package org.gamecontrol.codeclock;

/**
 * Created by illwrath on 11/29/14.
 *
 * Provides data necessary for demoing the project
 */
public class DemoUtils {
    // Home
    public static final String HOMEFILE = "{\"Demo\":\"8137ced1-0498-4b26-b0f9-682ad886b8eb\"}";

    // Projects
    public static final String PROJECTUUID = "8137ced1-0498-4b26-b0f9-682ad886b8eb";
    public static final String PROJECTFILE = "{\"UUID\":\"8137ced1-0498-4b26-b0f9-682ad886b8eb\"," +
            "\"TAGS\":[]," +
            "\"JOB_UUIDS\":[\"27bb894f-6408-4910-b75e-304306d82f19\"]," +
            "\"JOB_NAMES\":[\"Job1\"]," +
            "\"NOTES\":\"Optimizing the defense system of the Millennium Falcon for upcoming rebel attack.\"" +
            "}";

    // Jobs
    public static final String JOB1UUID = "27bb894f-6408-4910-b75e-304306d82f19";
    public static final String JOB2UUID = "";
    public static final String JOB3UUID = "";
    public static final String JOB1FILE = "{\"RUNNING_TIMES\":[3600000, 3600000, 11800000, 14400000, 7200000, 3600000, 14400000]," +
            "\"STATE\":1," +
            "\"TAGS\":[]," +
            "\"NAME\":\"Job1\"," +
            "\"NOTES\":\"Implementing target acquisition program for primary gun.\"," +
            "\"ESTIMATE\":72000000," +
            "\"START_TIMES\":[1417298989756, 1417385389756, 1417471789756, 1417558189756, 1417645589756, 1417732989756, 1417819389756]," +
            "\"ELAPSED\":55600000" +
            "}";
}
