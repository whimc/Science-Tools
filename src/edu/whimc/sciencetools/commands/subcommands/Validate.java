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
		
		public Validation(int taskId, double expected) {
			this.taskId = taskId;
			this.expected = expected;
		}
		
		public int getTaskId() {
			return taskId;
		}
		
		public double getExpected() {
			return expected;
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
			Utils.msg(player, "&c&lYou failed to tell us a value for " + type + "! Please try again!");
			validationTasks.remove(player.getUniqueId());
		}, 20 * 30);
		
		validationTasks.put(player.getUniqueId(), new Validation(id, tool.getData(player)));
		
		Utils.msg(player, "&aWhat value did you measure for &f&l" + type + "&a?", "&7&oPlease type your answer in chat!");
		
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

		
		Pattern pat = Pattern.compile("(\\d+(\\.\\d+)?)");
		Matcher matcher = pat.matcher(event.getMessage().replace(",", ""));

		if (!matcher.find()) {
			Utils.msg(player, "&cYour message did not contain any numbers!");
			return;
		}
		
		String match = matcher.group();
		Utils.msg(player, "We found the number '&7" + match + "&f' in your message");
		double number = Double.parseDouble(match);
		
		if (Math.abs(number - validation.getExpected()) < 1) {
			Utils.msg(player, "&a&lGood job! &2" + Utils.trim2Deci(number) + " &amatches our calculations!");
		} else {
			Utils.msg(player, "&c&lTry again! &4" + Utils.trim2Deci(number) + " &cis not quite close enough.");
		}
		
	}

}
