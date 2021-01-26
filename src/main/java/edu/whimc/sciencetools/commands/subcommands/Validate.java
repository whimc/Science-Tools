package edu.whimc.sciencetools.commands.subcommands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.BaseToolCommand.SubCommand;
import edu.whimc.sciencetools.managers.ScienceToolManager.ToolType;
import edu.whimc.sciencetools.models.ScienceTool;
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
	public boolean commandRoutine(CommandSender sender, String[] args) {

		ToolType type = ToolType.match(args[0]);

		if (type == null) {
			Utils.msg(sender, "&cThe tool \"&4" + args[0] + "&c\" does not exist!");
			Utils.msg(sender, "&cAvailable tools: &7" +
					String.join(", ", Arrays.asList(ToolType.values())
							.stream()
							.map(ToolType::name)
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

		double expected = 0;

		if (args.length > this.subCmd.minArgs()) {
		    World world = Bukkit.getWorld(args[2]);
		    if (world == null) {
		        Utils.msg(sender, "&4" + args[2] + " &cis an invalid world!");
		        return false;
		    }
		    Double x = Utils.parseDoubleWithError(sender, args[3]);
		    if (x == null) return false;
		    Double y = Utils.parseDoubleWithError(sender, args[4]);
            if (y == null) return false;
            Double z = Utils.parseDoubleWithError(sender, args[5]);
            if (z == null) return false;
            expected = tool.getData(sender, new Location(world, x, y, z));
		} else {
		    expected = tool.getData(sender, player.getLocation());
		}


		Utils.msg(sender, "&aValidating " + type + " for " + player.getName());

		UUID uuid = player.getUniqueId();

		if (validationTasks.containsKey(uuid)) {
			Bukkit.getScheduler().cancelTask(validationTasks.remove(uuid).taskId);
		}

		int id = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			doConfigTasks(player, "timeout", type, null);
			validationTasks.remove(uuid);
		}, 20 * plugin.getConfig().getInt("validation.timeout"));

		validationTasks.put(uuid, new Validation(id, expected, type));
		doConfigTasks(player, "prompt", type, null);

		return false;
	}

	@Override
	protected List<String> tabRoutine(CommandSender sender, String[] args) {
	    if (args.length == 1) {
	        return plugin.getToolManager().toolTabComplete(args[0].toLowerCase());
	    }
	    if (args.length == 2) {
	        return Bukkit.getOnlinePlayers().stream()
	                .map(Player::getName)
	                .filter(v -> v.toLowerCase().startsWith(args[1].toLowerCase()))
	                .collect(Collectors.toList());
	    }
	    if (args.length == 3) {
	        return Bukkit.getWorlds().stream()
	                .map(World::getName)
	                .filter(v -> v.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
	    }
	    if (!(sender instanceof Player)) {
	        return Arrays.asList();
	    }
	    Location loc = ((Player) sender).getLocation();
	    if (args.length == 4) {
	        return Arrays.asList(Double.toString(loc.getBlockX()));
	    }
	    if (args.length == 5) {
            return Arrays.asList(Double.toString(loc.getBlockY()));
        }
	    if (args.length == 6) {
            return Arrays.asList(Double.toString(loc.getBlockZ()));
        }

	    return super.tabRoutine(sender, args);
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

		Pattern pat = Pattern.compile("(-?\\d+(\\.\\d+)?)");
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
		String unit = plugin.getToolManager().getMainUnit(type);

		doConfigTasks(player, path + ".all", typeStr, valueStr, unit);
		doConfigTasks(player, path + "." + type.name(), typeStr, valueStr, unit);
	}

	private void doConfigTasks(Player player, String path, String type, String value, String unit) {
		for (String msg : plugin.getConfig().getStringList("validation.messages." + path)) {
			Utils.msg(player, msg.replace("{TOOL}", type).replace("{VAL}", value).replace("{UNIT}", unit));
		}

		for (String cmd : plugin.getConfig().getStringList("validation.commands." + path)) {
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
					cmd.replace("{TOOL}", type).replace("{VAL}", value).replace("{UNIT}", unit).replace("{PLAYER}", player.getName()));
		}
	}

}
