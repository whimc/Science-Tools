package edu.whimc.sciencetools.commands.subcommands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.BaseToolCommand.SubCommand;
import edu.whimc.sciencetools.utils.ScienceTool;
import edu.whimc.sciencetools.utils.ToolManager.ToolType;
import edu.whimc.sciencetools.utils.Utils;

public class Validate extends AbstractSubCommand implements Listener {

	private Map<UUID, Validation> validationTasks;
	
	public Validate(ScienceTools plugin, SubCommand subCmd) {
		super(plugin, subCmd);
		
		validationTasks = new HashMap<>();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private class Validation {
		
		private int taskId;
		private double expected;
		private ToolType type;
		
		public Validation(int taskId, double expected, ToolType type) {
			this.taskId = taskId;
			this.expected = expected;
			this.type = type;
		}
		
		public int getTaskId() {
			return taskId;
		}
		
		public double getExpected() {
			return expected;
		}
		
		public ToolType getType() {
			return type;
		}
	}
	
	@Override
	public boolean routine(CommandSender sender, String[] args) {

		ToolType type = ToolType.match(args[0]);
		
		if (type == null) {
			Utils.msg(sender, "&cThe tool \"&4" + args[0] + "&c\" does not exist!");
			Utils.msg(sender, "&cAvailable tools: &7" + 
					String.join(", ", Arrays.asList(ToolType.values())
							.stream()
							.map(s -> s.name())
							.collect(Collectors.toList())));
			return false;
		}
		
		ScienceTool tool = plugin.getToolManager().getTool(type);
		
		if (tool == null) {
			Utils.msg(sender, "&cThat data tool isn't loaded!");
			return false;
		}
		
		Player player = Bukkit.getPlayer(args[1]);
		
		if (player == null) {
			Utils.msg(sender, "&4" + args[1] + " &cis not a valid player!");
			return false;
		}
		
		Utils.msg(sender, "&aValidating " + type + " for " + player.getName());
		
		
		int id = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			doConfigTasks(player, "timeout", type, null);
			validationTasks.remove(player.getUniqueId());
		}, 20 * plugin.getConfig().getInt("validation.timeout"));
		
		validationTasks.put(player.getUniqueId(), new Validation(id, tool.getData(player), type));
		doConfigTasks(player, "prompt", type, null);
		
		return false;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		
		if (!validationTasks.containsKey(player.getUniqueId())) {
			return;
		}
		
		event.setCancelled(true);
		
		Validation validation = validationTasks.remove(player.getUniqueId());
		Bukkit.getScheduler().cancelTask(validation.getTaskId());
		
		ToolType type = validation.getType();
		
		Pattern pat = Pattern.compile("(\\d+(\\.\\d+)?)");
		Matcher matcher = pat.matcher(event.getMessage().replace(",", ""));

		if (!matcher.find()) {
			syncDoConfigTasks(player, "no-number", type, null);
			return;
		}
		
		String match = matcher.group();
		
		syncDoConfigTasks(player, "found-number", type, Double.valueOf(match));
		
		double number = Double.parseDouble(match);
		
		if (Math.abs(number - validation.getExpected()) < plugin.getConfig().getDouble("validation.tolerance")) {
			syncDoConfigTasks(player, "success", type, number);
		} else {
			syncDoConfigTasks(player, "failure", type, number);
		}
		
	}
	 
	private void syncDoConfigTasks(Player player, String path, ToolType type, Double value) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> doConfigTasks(player, path, type, value));
	}
	
	private void doConfigTasks(Player player, String path, ToolType type, Double value) {
		String typeStr = type == null ? "" : type.toString();
		String valueStr = value == null ? "" : value.toString();
		
		doConfigTasks(player, path + ".all", typeStr, valueStr);
		doConfigTasks(player, path + "." + type.name(), typeStr, valueStr);
	}
	
	private void doConfigTasks(Player player, String path, String type, String value) {
		for (String msg : plugin.getConfig().getStringList("validation.messages." + path)) {
			Utils.msg(player, msg.replace("{TOOL}", type).replace("{VAL}", value));
		}
		
		for (String cmd : plugin.getConfig().getStringList("validation.commands." + path)) {
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
					cmd.replace("{TOOL}", type).replace("{VAL}", value).replace("{PLAYER}", player.getName()));
		}
	}

}
