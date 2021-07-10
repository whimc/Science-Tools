package edu.whimc.sciencetools;

import edu.whimc.sciencetools.commands.BaseToolCommand;
import edu.whimc.sciencetools.commands.GetData;
import edu.whimc.sciencetools.models.conversion.ConversionManager;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolManager;
import edu.whimc.sciencetools.models.sciencetool.ToolType;
import edu.whimc.sciencetools.models.validation.ValidationManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ScienceTools extends JavaPlugin implements Listener {

    private ScienceToolManager toolManager;
    private ConversionManager conversionManager;
    private ValidationManager validationManager;

    private static ScienceTools instance;

    @Override
    public void onEnable() {
        ScienceTools.instance = this;

        getCommand("sciencetools").setExecutor(new BaseToolCommand(this));

//        getConfig().options().copyDefaults(false);
        saveDefaultConfig();

        this.conversionManager = new ConversionManager();
        this.toolManager = new ScienceToolManager(this.conversionManager);
//        this.validationManager = new ValidationManager(this);

        for (ToolType tool : ToolType.values()) {
            getCommand(tool.toString().toLowerCase()).setExecutor(new GetData(this, tool));
        }
    }

    public ScienceToolManager getToolManager() {
        return this.toolManager;
    }

    public ConversionManager getConversionManager() {
        return this.conversionManager;
    }

    public ValidationManager getValidationManager() {
        return this.validationManager;
    }

    public void reloadScienceTools() {
        reloadConfig();
        this.conversionManager = new ConversionManager();
        this.toolManager = new ScienceToolManager(this.conversionManager);
//        this.validationManager = new ValidationManager(this);
    }

    public static ScienceTools getInstance() {
        return ScienceTools.instance;
    }

}
