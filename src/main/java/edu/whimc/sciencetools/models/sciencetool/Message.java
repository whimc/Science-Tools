package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.utils.Utils;

public class Message {
    private String message;
    private final String NAME_PLACEHOLDER = "{TOOL}";
    private final String MEASURE_PLACEHOLDER = "{MEASUREMENT}";
    private final String UNIT_PLACEHOLDER = "{UNIT}";
    Message(String m)
    {
        message = m;
    }
    public String displayString(String displayName,String measurement, String unit)
    {
        String msg = message;
        if(message.indexOf(NAME_PLACEHOLDER)!=-1)
            msg = msg.replace(NAME_PLACEHOLDER,displayName);
        if(message.indexOf(MEASURE_PLACEHOLDER)!=-1) {
            msg = msg.replace(MEASURE_PLACEHOLDER, measurement);
        }
        if(message.indexOf(UNIT_PLACEHOLDER)!=-1)
            msg = msg.replace(UNIT_PLACEHOLDER,unit);
        return msg;
    }
}
