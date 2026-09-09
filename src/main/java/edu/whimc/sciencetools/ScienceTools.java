package edu.whimc.sciencetools;

import edu.whimc.sciencetools.commands.ScienceToolCommand;
import edu.whimc.sciencetools.commands.ToolCommandCorrector;
import edu.whimc.sciencetools.commands.TricorderCommand;
import edu.whimc.sciencetools.gui.ScienceToolGui;
import edu.whimc.sciencetools.gui.TricorderManager;
import edu.whimc.sciencetools.models.conversion.ConversionManager;
import edu.whimc.sciencetools.models.sciencetool.PlayerToolState;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolManager;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolMeasureEvent;
import edu.whimc.sciencetools.utils.Utils;
import edu.whimc.sciencetools.utils.sql.Queryer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main plugin class.
 */
public class ScienceTools extends JavaPlugin implements Listener {

    private static ScienceTools instance;

    private ScienceToolManager toolManager;
    private ConversionManager conversionManager;
    private TricorderManager tricorderManager;
    private PlayerToolState playerToolState;
    private @Nullable Queryer queryer;
    private final Consumer<Queryer> handleCreateQueryer = q -> {
        if (q == null) {
            Utils.log("&cCould not establish MySQL connection! Some features are disabled.");
        } else {
            Utils.log("&aMySQL connection established!");
        }
        this.queryer = q;
    };

    public static ScienceTools getInstance() {
        return ScienceTools.instance;
    }

    @Override
    public void onEnable() {
        ScienceTools.instance = this;
        saveDefaultConfig();

        getCommand("sciencetools").setExecutor(new ScienceToolCommand());
        this.tricorderManager = new TricorderManager();
        this.playerToolState = new PlayerToolState();
        TricorderCommand tricorderCommand = new TricorderCommand(this.tricorderManager);
        getCommand("tricorder").setExecutor(tricorderCommand);
        getCommand("tricorder").setTabCompleter(tricorderCommand);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getPluginManager().registerEvents(new ScienceToolGui(this.tricorderManager), this);
        Bukkit.getServer().getPluginManager().registerEvents(this.tricorderManager, this);
        Bukkit.getServer().getPluginManager().registerEvents(new ToolCommandCorrector(), this);

        this.conversionManager = new ConversionManager();
        this.toolManager = new ScienceToolManager(this.conversionManager);
        Queryer.create(this, this.handleCreateQueryer);
    }

    public ScienceToolManager getToolManager() {
        return this.toolManager;
    }

    public ConversionManager getConversionManager() {
        return this.conversionManager;
    }

    /**
     * Per-player cooldowns, unit choices, and recent measurements.
     *
     * @return The player tool state.
     */
    public PlayerToolState getPlayerToolState() {
        return this.playerToolState;
    }

    public @Nullable Queryer getQueryer() {
        return this.queryer;
    }

    /**
     * Load all conversions and science tools from the config.
     */
    public void reloadScienceTools() {
        reloadConfig();
        this.conversionManager.loadConversions();
        this.toolManager.loadTools(this.conversionManager);
        Queryer.create(this, this.handleCreateQueryer);
        if (this.tricorderManager != null) {
            this.tricorderManager.syncOnlinePlayers();
        }
    }

    @EventHandler
    public void onMeasure(ScienceToolMeasureEvent event) {
        this.playerToolState.remember(event.getMeasurement());
        if (this.queryer != null) {
            this.queryer.storeNewMeasurement(event.getMeasurement());
        }
    }

}
