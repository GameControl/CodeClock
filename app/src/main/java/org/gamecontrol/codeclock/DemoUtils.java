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
            "\"TAGS\":[\"Star Wars\", \"Demo Project\"]," +
            "\"JOB_UUIDS\":[\"27bb894f-6408-4910-b75e-304306d82f19\", \"c2201fc3-dc69-449a-b01b-b80c30372c15\", \"5274b931-6b1a-406d-8d33-0b9f03dc789c\"]," +
            "\"JOB_NAMES\":[\"Job1\", \"Job2\", \"Job3\"]," +
            "\"NOTES\":\"Improving the Millennium Falcon's systems for upcoming rebel attack.\"" +
            "}";

    // Jobs
    public static final String JOB1UUID = "27bb894f-6408-4910-b75e-304306d82f19";
    public static final String JOB1FILE = "{\"RUNNING_TIMES\":[600000, 1250000, 11800000, 14400000, 11800000, 7200000, 3600000]," +
            "\"STATE\":3," +
            "\"TAGS\":[\"C++\", \"OOP\", \"Weapons\"]," +
            "\"NAME\":\"Job1\"," +
            "\"NOTES\":\"Implementing target acquisition program for primary gun.\"," +
            "\"ESTIMATE\":72000000," +
            "\"START_TIMES\":[1417298989756, 1417385389756, 1417471789756, 1417558189756, 1417645589756, 1417732989756, 1417819389756]," +
            "\"ELAPSED\":50650000" +
            "}";

    public static final String JOB2UUID = "c2201fc3-dc69-449a-b01b-b80c30372c15";
    public static final String JOB2FILE = "{\"RUNNING_TIMES\":[11800000, 900000, 7200000, 4800000]," +
            "\"STATE\":1," +
            "\"TAGS\":[\"Assembly\", \"C\", \"Hyperdrive\"]," +
            "\"NAME\":\"Job2\"," +
            "\"NOTES\":\"Tuning the reflector shields.\"," +
            "\"ESTIMATE\":22505000," +
            "\"START_TIMES\":[1417471789756, 1417558189756, 1417645589756, 1417732989756]," +
            "\"ELAPSED\":24700000" +
            "}";

    public static final String JOB3UUID = "5274b931-6b1a-406d-8d33-0b9f03dc789c";
    public static final String JOB3FILE = "{\"RUNNING_TIMES\":[]," +
            "\"STATE\":2," +
            "\"TAGS\":[\"Shields\", \"Java\"]," +
            "\"NAME\":\"Job3\"," +
            "\"NOTES\":\"Debugging the Hyperdrive initializer.\"," +
            "\"ESTIMATE\":256000000," +
            "\"START_TIMES\":[1417478008219]," +
            "\"ELAPSED\":0" +
            "}";
}
