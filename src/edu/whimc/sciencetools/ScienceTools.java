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
		
		getCommand("altitude").setExecutor(new GetData(this, ToolType.ALTITUDE));
		getCommand("oxygen").setExecutor(new GetData(this, ToolType.OXYGEN));
		getCommand("pressure").setExecutor(new GetData(this, ToolType.PRESSURE));
		getCommand("temperature").setExecutor(new GetData(this, ToolType.TEMPERATURE));
		getCommand("wind").setExecutor(new GetData(this, ToolType.WIND));
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