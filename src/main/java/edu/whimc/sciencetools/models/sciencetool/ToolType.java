package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.javascript.JSPlaceholder;
import org.apache.commons.lang.StringUtils;

public enum ToolType {
    ALTITUDE(JSPlaceholder.ALTITUDE),
    OXYGEN(JSPlaceholder.OXYGEN),
    PRESSURE(JSPlaceholder.PRESSURE),
    RADIATION(JSPlaceholder.RADIATION),
    TEMPERATURE(JSPlaceholder.TEMPERATURE),
    WIND(JSPlaceholder.WIND),
    ;

    private final JSPlaceholder placeholder;

    ToolType(JSPlaceholder placeholder) {
        this.placeholder = placeholder;
    }

    public JSPlaceholder getPlaceholder() {
        return this.placeholder;
    }

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
