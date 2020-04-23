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
		getConfig().options().copyDefaults(true);
		
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
	
//	
//	public void onDisable() {
//	    config.options().copyDefaults(true);
//	    saveConfig();
//	}
//
//	public boolean onCommand(CommandSender sender,
//			Command command,
//			String label,
//			String[] args) {
//		if (sender instanceof Player) {
//			Player player = (Player) sender;
//			if (command.getName().equalsIgnoreCase("speed-up")) {
//				long worldTime = player.getWorld().getTime();
//				player.getWorld().setTime(worldTime * 100);
//				String currentTime = Long.toString(worldTime);
//				player.sendMessage(currentTime + "is sped-up");
//				return true;
//			}
//			if (command.getName().equalsIgnoreCase("temperature")) {
//				int x_loc = player.getLocation().getBlockX();
//				int y_loc = player.getLocation().getBlockY();
//				int z_loc = player.getLocation().getBlockZ();
//				int time = Math.round(player.getWorld().getTime()) % 24000;
//				int temp_inc_limit = 0;
//				final String degree_fahrenheit = "\u2109";
//				final String degree_celcius = "\u2103";
//				
//				// biome raw temperature code
//				Double biome_temp_raw = player.getWorld().getBlockAt(x_loc, y_loc, z_loc).getTemperature();
//				Double biome_temp = null;
//				// per time temperature change
//				Double temp_change_raw = 15 * (Math.sin(time / 3819.72));
//				
//				// cold regions
//				if (biome_temp_raw < 0.4) {
//					biome_temp = 0 + (62 - y_loc) + temp_change_raw * 0.5;
//				}
//				// warm regions
//				else if (biome_temp_raw >= 0.4 && biome_temp_raw < 1.0) {
//					biome_temp = (60 + biome_temp_raw * 20) + (62 - y_loc) + temp_change_raw;
//				}
//				// hot regions
//				else {
//					biome_temp = (80 + biome_temp_raw * 10) + (62 - y_loc) + temp_change_raw * biome_temp_raw;
//				}
//				
//				// heat source
//				for (int i = -3; i <= 3; i++) {
//					for (int j = -3; j <= 3; j++) {
//						for (int k = -3; k <= 3; k++) {
//							if (temp_inc_limit <= 3) {
//								if (player.getWorld().getBlockAt(x_loc + i, y_loc + j, z_loc + k).getType() == Material.TORCH) {
//									biome_temp += 3;
//									temp_inc_limit += 1;
//								}
//								if (player.getWorld().getBlockAt(x_loc + i, y_loc + j, z_loc + k).getType() == Material.FIRE) {
//									biome_temp += 3;
//									temp_inc_limit += 1;
//								}
//								if (player.getWorld().getBlockAt(x_loc + i, y_loc + j, z_loc + k).getType() == Material.LAVA) {
//									biome_temp += 3;
//									temp_inc_limit += 1;
//								}
//								if (player.getWorld().getBlockAt(x_loc + i, y_loc + j, z_loc + k).getType() == Material.LAVA_BUCKET) {
//									biome_temp += 3;
//									temp_inc_limit += 1;
//								}
//								if (player.getWorld().getBlockAt(x_loc + i, y_loc + j, z_loc + k).getType() == Material.GLOWSTONE) {
//									biome_temp += 3;
//									temp_inc_limit += 1;
//								}
//							}
//						}
//					}
//				}
//				temp_inc_limit = 0;
//				
//				// water
//				for (int i = -3; i <= 3; i++) {
//					for (int j = -3; j <= 3; j++) {
//						for (int k = -3; k <= 3; k++) {
//							if (temp_inc_limit <= 3) {
//								if (player.getWorld().getBlockAt(x_loc + i, y_loc + j, z_loc + k).getType() == Material.WATER) {
//									biome_temp -= 3;
//									temp_inc_limit += 1;
//								}
//								if (player.getWorld().getBlockAt(x_loc + i, y_loc + j, z_loc + k).getType() == Material.ICE) {
//									biome_temp -= 3;
//									temp_inc_limit += 1;
//								}
//							}
//						}
//					}
//				}
//				temp_inc_limit = 0;
//				
//				// under water
//				for (int i =0; i <= 3; i++) {
//					if (temp_inc_limit < 1) {
//						if (player.getWorld().getBlockAt(x_loc, y_loc + i, z_loc).getType() == Material.WATER) {
//							biome_temp -= 15;
//							temp_inc_limit += 1;
//						}
//						if (player.getWorld().getBlockAt(x_loc, y_loc + i, z_loc).getType() == Material.ICE) {
//							biome_temp -= 15;
//							temp_inc_limit += 1;
//						}
//					}
//				}
//				temp_inc_limit = 0;
//				
//				// cave, shade
//				if (biome_temp_raw < 0.4) {
//					if (time < 23000 && time >= 12000) {
//						for (int i = 0; i < 10; i++) {
//							if(temp_inc_limit < 1) {
//								if (player.getWorld().getBlockAt(x_loc, y_loc + i, z_loc).getType() != Material.AIR) {
//									biome_temp += 10;
//									temp_inc_limit += 1;
//								}
//							}
//						}
//					}
//					else {
//						for (int i = 0; i < 10; i++) {
//							if(temp_inc_limit < 1) {
//								if (player.getWorld().getBlockAt(x_loc, y_loc + i, z_loc).getType() != Material.AIR) {
//									biome_temp -= 10;
//									temp_inc_limit += 1;
//								}
//							}
//						}
//					}
//				}
//				else {
//					if (time >= 23000 && time < 12000) {
//						for (int i = 0; i < 10; i++) {
//							if(temp_inc_limit < 1) {
//								if (player.getWorld().getBlockAt(x_loc, y_loc + i, z_loc).getType() != Material.AIR) {
//									biome_temp -= 10;
//									temp_inc_limit += 1;
//								}
//							}
//						}
//					}
//				}
//				temp_inc_limit = 0;
//				
//				
//				player.sendMessage(Double.toString(Math.round(biome_temp)) + degree_fahrenheit + " / " + Double.toString(Math.round((biome_temp-32)*5/9)) + degree_celcius );
//				return true;
//			}
//			if (command.getName().equalsIgnoreCase("pressure")) {
//				double playerAltitude = player.getLocation().getY();
//				double airPressure = 101.325 - 0.328 * (playerAltitude - 63.0);
//				if (airPressure < 0.0) {
//					airPressure = 0.0;
//				}
//				sender.sendMessage(numberFormatTwo.format(airPressure) + "kPa");
//				return true;
//			}
//			if (command.getName().equalsIgnoreCase("oxygen")) {
//				double oxygenLevel = 20.95;
//				if (oxygenLevel <= 0.0) {
//					oxygenLevel = 0.0;
//				}
//				sender.sendMessage(numberFormatTwo.format(oxygenLevel) + "%");
//				return true;
//		    }
//			if (command.getName().equalsIgnoreCase("wind")) {
//				double windSpeed = 0.0;
//				double locX = player.getLocation().getX();
//				double locY = player.getLocation().getY();
//				double locZ = player.getLocation().getZ();
//			      
//				if ((450.0 - Math.abs(locX) <= 250.0) || (450.0 - Math.abs(locZ) <= 250.0)) {
//					windSpeed = 80.0 + (Math.random() + 0.5) * (locY - 56.0);
//				}
//				else {
//					windSpeed = (Math.random() + 0.5) * (locY - 56.0);
//				}
//				sender.sendMessage(numberFormatTwo.format(windSpeed) + "mph");
//				return true;
//			}
//			if (command.getName().equalsIgnoreCase("altitude")) {
//				double locY = player.getLocation().getY();
//				String world_name = player.getWorld().getName();
//				double altitude_offset = config.getDouble(world_name + ".Altitude_Offset");
//				double altitude_multiplier = config.getDouble(world_name + ".Altitude_Multiplier");
//				
//				if(altitude_multiplier == 0.0) {
//					altitude_multiplier = 1.0;
//				}
//				double altitude = ((locY-63.0)+altitude_offset)*altitude_multiplier;
//				sender.sendMessage(Double.toString(Math.round(altitude)) + "m");				
//				return true;
//			}
//		}
//		else {
//			sender.sendMessage("You are not THE player");
//			return false;
//		}
//		return false;
//	}
}