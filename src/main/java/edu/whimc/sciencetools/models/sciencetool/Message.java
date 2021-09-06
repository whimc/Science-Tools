package edu.whimc.sciencetools.models.sciencetool;

/**
 * Class to represent messages for science tools
 */
public class Message {

    private String message;
    private final String NAME_PLACEHOLDER = "{TOOL}";
    private final String MEASURE_PLACEHOLDER = "{MEASUREMENT}";
    private final String UNIT_PLACEHOLDER = "{UNIT}";

    /**
     * Constructs a message
     * @param m the message to send to the player
     */
    Message(String m) {
        message = m;
    }

    /**
     * Returns formatted string with placeholders removed if has any
     * @param displayName name of the tool
     * @param measurement measurement of the tool
     * @param unit unit of the tool
     * @return message to send to the player for the tool
     */
    public String displayString(String displayName,String measurement, String unit) {
        String msg = message;
        msg = msg.replace(NAME_PLACEHOLDER, displayName);
        msg = msg.replace(MEASURE_PLACEHOLDER, measurement);
        msg = msg.replace(UNIT_PLACEHOLDER, unit);
        return msg;
    }

    /**
     * Returns message
     * @return message to send to the player for the tool
     */
    public String toString() {
        return message;
    }
}
