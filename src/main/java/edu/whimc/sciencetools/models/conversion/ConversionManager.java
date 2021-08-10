package edu.whimc.sciencetools.models.conversion;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.javascript.JSNumericExpression;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles operations regarding Conversions (adding, removing, saving, loading).
 */
public class ConversionManager {

    private final Map<String, Conversion> conversions;

    /**
     * Constructs a new ConversionManager.
     */
    public ConversionManager() {
        this.conversions = new HashMap<>();
        loadConversions();
    }

    /**
     * Loads all conversions from the config.
     */
    public void loadConversions() {
        FileConfiguration config = ScienceTools.getInstance().getConfig();
        Utils.log("&eLoading Conversions from config");

        // collect conversions from config
        for (String conversion : config.getConfigurationSection("conversions").getKeys(false)) {
            Utils.log("&b - &f" + conversion);
            String expr = config.getString("conversions." + conversion + ".expression");
            String unit = config.getString("conversions." + conversion + ".unit");

            Utils.log("&b\t- Expression: \"&f" + expr + "&b\"");
            Utils.log("&b\t- Unit: \"&f" + unit + "&b\"");

            // ensure conversion's expression is valid, skip if not
            JSNumericExpression jsExpr = new JSNumericExpression(expr);
            if (!jsExpr.valid()) {
                Utils.log("&c\t* Invalid expression! Skipping.");
                continue;
            }

            loadConversion(conversion, unit, jsExpr);
        }

        Utils.log("&eConversions loaded!");
    }

    /**
     * Creates and loads a new Conversion.
     *
     * @param name The name of the Conversion.
     * @param unit The unit being converted to.
     * @param expr The Conversion equation.
     * @return The Conversion.
     */
    private @NotNull Conversion loadConversion(String name, String unit, JSNumericExpression expr) {
        Conversion conversion = new Conversion(name, unit, expr);
        conversions.put(name, conversion);
        return conversion;
    }

    /**
     * Creates and saves a new Conversion to the config.
     *
     * @param name The name of the Conversion.
     * @param unit The unit being converted to.
     * @param expr The Conversion equation.
     * @return The Conversion.
     */
    public Conversion createConversion(String name, String unit, JSNumericExpression expr) {
        Conversion conversion = loadConversion(name, unit, expr);
        saveToConfig(conversion);
        return conversion;
    }

    /**
     * Removes the specified Conversion.
     *
     * @param conversion The Conversion to remove.
     */
    public void removeConversion(@NotNull Conversion conversion) {
        String name = conversion.getName();
        conversions.remove(name);
        setConfig(name, null);
    }

    /**
     * Gets the specified Conversion.
     *
     * @param key The Conversion's name.
     * @return The specified Conversion.
     */
    public Conversion getConversion(String key) {
        return this.conversions.getOrDefault(key, null);
    }

    /**
     * @return The Collection of Conversions.
     */
    public Collection<Conversion> getConversions() {
        return conversions.values();
    }

    /**
     * Saves the Conversion to the config.
     *
     * @param conversion The conversion to save.
     */
    public void saveToConfig(@NotNull Conversion conversion) {
        String name = conversion.getName();
        setConfig(name + ".unit", conversion.getUnit());
        setConfig(name + ".expression", conversion.getExpression().toString());
    }

    /**
     * Sets and saves a Conversion to the config.
     *
     * @param key The Conversion's name.
     * @param value The Conversion.
     */
    private void setConfig(String key, Object value) {
        ScienceTools.getInstance().getConfig().set("conversions." + key, value);
        ScienceTools.getInstance().saveConfig();
    }

}
