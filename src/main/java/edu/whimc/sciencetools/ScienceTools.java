package edu.whimc.sciencetools;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import edu.whimc.sciencetools.commands.BaseToolCommand;
import edu.whimc.sciencetools.commands.GetData;
import edu.whimc.sciencetools.utils.ConversionManager;
import edu.whimc.sciencetools.utils.ToolManager;
import edu.whimc.sciencetools.utils.ToolManager.ToolType;

public class ScienceTools extends JavaPlugin implements Listener {

	private ToolManager toolManager;
	private ConversionManager convManager;
	
	@Override
	public void onEnable() {
		getCommand("sciencetools").setExecutor(new BaseToolCommand(this));

		saveDefaultConfig();
		getConfig().options().copyDefaults(false);

		convManager = ConversionManager.loadConversions(this);
		toolManager = ToolManager.loadTools(this, convManager);

		for (ToolType tool : ToolType.values()) {
			getCommand(tool.toString().toLowerCase()).setExecutor(new GetData(this, tool));
		}
	}

	public ToolManager getToolManager() {
		return toolManager;
	}

	public ConversionManager getConversionManager() {
		return convManager;
	}

	public void reloadScienceTools() {
		reloadConfig();
		convManager = ConversionManager.loadConversions(this);
		toolManager = ToolManager.loadTools(this, convManager);
	}

}