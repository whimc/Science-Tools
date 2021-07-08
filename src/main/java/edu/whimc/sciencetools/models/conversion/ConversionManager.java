package edu.whimc.sciencetools.models.conversion;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.javascript.JSNumericalExpression;
import edu.whimc.sciencetools.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConversionManager {

    private final ScienceTools plugin;
    private final Map<String, Conversion> conversions;

    public ConversionManager(ScienceTools plugin) {
        this.plugin = plugin;
        this.conversions = new HashMap<>();
        loadConversions();
    }

    private void loadConversions() {
        Utils.log("&eLoading Conversions from config");

        for (String convName : plugin.getConfig().getConfigurationSection("conversions").getKeys(false)) {
            Utils.log("&b - Loading &f" + convName);
            String expr = plugin.getConfig().getString("conversions." + convName + ".expression");
            String unit = plugin.getConfig().getString("conversions." + convName + ".unit");

            Utils.log("&b   - Expression: \"&f" + expr + "&b\"");
            Utils.log("&b   - Unit: \"&f" + unit + "&b\"");

            JSNumericalExpression jsExpr = new JSNumericalExpression(expr);
            if (!jsExpr.valid()) {
                Utils.log("&e   * Invalid expression! (Skipping this conversion)");
                continue;
            }

            loadConversion(convName, unit, jsExpr);
        }

        Utils.log("&eConversions loaded!");
    }

    private @NotNull Conversion loadConversion(String name, String unit, JSNumericalExpression expr) {
        Conversion conversion = new Conversion(name, unit, expr);
        conversions.put(name, conversion);
        return conversion;
    }

    public Conversion createConversion(String name, String unit, JSNumericalExpression expr) {
        Conversion conversion = loadConversion(name, unit, expr);
        saveToConfig(conversion);
        return conversion;
    }

    public void removeConversion(@NotNull Conversion conversion) {
        String name = conversion.getName();
        conversions.remove(name);
        setConfig(name, null);
    }

    public Conversion getConversion(String key) {
        return this.conversions.getOrDefault(key, null);
    }

    public Collection<Conversion> getConversions() {
        return conversions.values();
    }

    public void saveToConfig(@NotNull Conversion conversion) {
        String name = conversion.getName();
        setConfig(name + ".unit", conversion.getUnit());
        setConfig(name + ".expression", conversion.getExpression().toString());
    }

    private void setConfig(String key, Object value) {
        plugin.getConfig().set("conversions." + key, value);
        plugin.saveConfig();
    }

}
