package edu.whimc.sciencetools.models.sciencetool;

import org.apache.commons.lang.StringUtils;

public enum ToolType {
    ALTITUDE,
    OXYGEN,
    PRESSURE,
    RADIATION,
    TEMPERATURE,
    WIND,
    ;

    @Override
    public String toString() {
        return StringUtils.capitalize(this.name().toLowerCase());
    }

    public static ToolType match(String str) {
        for (ToolType type : ToolType.values()) {
            if (type.name().equalsIgnoreCase(str)) {
                return type;
            }
        }
        return null;
    }

}
