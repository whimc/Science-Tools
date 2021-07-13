package edu.whimc.sciencetools;

import edu.whimc.sciencetools.commands.ScienceToolCommand;
import edu.whimc.sciencetools.models.conversion.ConversionManager;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolManager;
import edu.whimc.sciencetools.models.validation.ValidationManager;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ScienceTools extends JavaPlugin implements Listener {

    private ScienceToolManager toolManager;
    private ConversionManager conversionManager;
    private ValidationManager validationManager;

    private final List<Command> commands = new ArrayList<>();

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

//    public ValidationManager getValidationManager() {
//        return this.validationManager;
//    }

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
