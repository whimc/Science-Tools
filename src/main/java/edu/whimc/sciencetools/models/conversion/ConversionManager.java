package edu.whimc.sciencetools.models.conversion;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.javascript.JSNumericExpression;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConversionManager {

    private final Map<String, Conversion> conversions;

    public ConversionManager() {
        this.conversions = new HashMap<>();
        loadConversions();
    }

    private void loadConversions() {
        FileConfiguration config = ScienceTools.getInstance().getConfig();
        Utils.log("&eLoading Conversions from config");

        for (String conversion : config.getConfigurationSection("conversions").getKeys(false)) {
            Utils.log("&b - &f" + conversion);
            String expr = config.getString("conversions." + conversion + ".expression");
            String unit = config.getString("conversions." + conversion + ".unit");

            Utils.log("&b\t- Expression: \"&f" + expr + "&b\"");
            Utils.log("&b\t- Unit: \"&f" + unit + "&b\"");

            JSNumericExpression jsExpr = new JSNumericExpression(expr);
            if (!jsExpr.valid()) {
                Utils.log("&c\t* Invalid expression! Skipping.");
                continue;
            }

            loadConversion(conversion, unit, jsExpr);
        }

        Utils.log("&eConversions loaded!");
    }

    private @NotNull Conversion loadConversion(String name, String unit, JSNumericExpression expr) {
        Conversion conversion = new Conversion(name, unit, expr);
        conversions.put(name, conversion);
        return conversion;
    }

    public Conversion createConversion(String name, String unit, JSNumericExpression expr) {
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
        ScienceTools.getInstance().getConfig().set("conversions." + key, value);
        ScienceTools.getInstance().saveConfig();
    }

}
