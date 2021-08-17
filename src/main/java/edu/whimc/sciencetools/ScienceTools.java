package edu.whimc.sciencetools;

import edu.whimc.sciencetools.commands.ScienceToolCommand;
import edu.whimc.sciencetools.models.conversion.ConversionManager;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolManager;
import edu.whimc.sciencetools.utils.Utils;
import edu.whimc.sciencetools.utils.sql.Queryer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

/**
 * The main plugin class.
 */
public class ScienceTools extends JavaPlugin implements Listener {

    private ScienceToolManager toolManager;
    private ConversionManager conversionManager;
    private Queryer queryer;

    private static ScienceTools instance;
    private static Consumer<Queryer> handleConnection = q -> {
        if (q == null) {
            Utils.log("&cCould not establish MySQL connection! Some features are disabled.");
        } else {
            Utils.log("&aMySQL connection established!");
        }
    };

    @Override
    public void onEnable() {
        ScienceTools.instance = this;
        saveDefaultConfig();

        getCommand("sciencetools").setExecutor(new ScienceToolCommand());

        this.conversionManager = new ConversionManager();
        this.toolManager = new ScienceToolManager(this.conversionManager);
        this.queryer = new Queryer(this, handleConnection);
    }

    public ScienceToolManager getToolManager() {
        return this.toolManager;
    }

    public ConversionManager getConversionManager() {
        return this.conversionManager;
    }

    public Queryer getQueryer() {
        return this.queryer;
    }

    /**
     * Load all conversions and science tools from the config.
     */
    public void reloadScienceTools() {
        reloadConfig();
        this.conversionManager.loadConversions();
        this.toolManager.loadTools(this.conversionManager);
        this.queryer = new Queryer(this, handleConnection);
    }

    public static ScienceTools getInstance() {
        return ScienceTools.instance;
    }

}
