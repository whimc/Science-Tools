package edu.whimc.sciencetools;

import edu.whimc.sciencetools.commands.ScienceToolCommand;
import edu.whimc.sciencetools.models.conversion.ConversionManager;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main plugin class.
 */
public class ScienceTools extends JavaPlugin implements Listener {

    private ScienceToolManager toolManager;
    private ConversionManager conversionManager;

    private static ScienceTools instance;

    @Override
    public void onEnable() {
        ScienceTools.instance = this;
        saveDefaultConfig();

        getCommand("sciencetools").setExecutor(new ScienceToolCommand());

        reloadScienceTools();
    }

    public ScienceToolManager getToolManager() {
        return this.toolManager;
    }

    public ConversionManager getConversionManager() {
        return this.conversionManager;
    }

    /**
     * Load all conversions and science tools from the config.
     */
    public void reloadScienceTools() {
        reloadConfig();
        this.conversionManager = new ConversionManager();
        this.toolManager = new ScienceToolManager(this.conversionManager);
    }

    public static ScienceTools getInstance() {
        return ScienceTools.instance;
    }

}
