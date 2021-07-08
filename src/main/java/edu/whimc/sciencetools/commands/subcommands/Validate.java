package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.BaseToolCommand.SubCommand;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import edu.whimc.sciencetools.models.sciencetool.ToolType;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Validate extends AbstractSubCommand implements Listener {

    private final Map<UUID, Validation> validationTasks;

    public Validate(ScienceTools plugin, SubCommand subCmd) {
        super(plugin, subCmd);

        this.validationTasks = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private class Validation {

        private final int taskId;
        private final double expected;
        private final ToolType type;

        public Validation(int taskId, double expected, ToolType type) {
            this.taskId = taskId;
            this.expected = expected;
            this.type = type;
        }

        public int getTaskId() {
            return this.taskId;
        }

        public double getExpected() {
            return this.expected;
        }

        public ToolType getType() {
            return this.type;
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

        ScienceTool tool = this.plugin.getToolManager().getTool(type);

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
            expected = tool.getData(new Location(world, x, y, z));
        } else {
            expected = tool.getData(player.getLocation());
        }


        Utils.msg(sender, "&aValidating " + type + " for " + player.getName());

        UUID uuid = player.getUniqueId();

        if (this.validationTasks.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(this.validationTasks.remove(uuid).taskId);
        }

        int id = Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            doConfigTasks(player, "timeout", type, null);
            this.validationTasks.remove(uuid);
        }, 20 * this.plugin.getConfig().getInt("validation.timeout"));

        this.validationTasks.put(uuid, new Validation(id, expected, type));
        doConfigTasks(player, "prompt", type, null);

        return false;
    }

    @Override
    protected List<String> tabRoutine(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return this.plugin.getToolManager().toolTabComplete(args[0].toLowerCase());
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

        if (!this.validationTasks.containsKey(player.getUniqueId())) {
            return;
        }

        event.setCancelled(true);

        Validation validation = this.validationTasks.remove(player.getUniqueId());
        Bukkit.getScheduler().cancelTask(validation.getTaskId());

        ToolType type = validation.getType();

        Pattern pat = Pattern.compile("(-?\\d*(\\.\\d+)?)");
        Matcher matcher = pat.matcher(event.getMessage().replace(",", ""));

        if (!matcher.find() || matcher.group().isEmpty()) {
            syncDoConfigTasks(player, "no-number", type, null);
            return;
        }

        String match = matcher.group();

        syncDoConfigTasks(player, "found-number", type, Double.valueOf(match));

        double number = Double.parseDouble(match);

        if (Math.abs(number - validation.getExpected()) < this.plugin.getConfig().getDouble("validation.tolerance")) {
            syncDoConfigTasks(player, "success", type, number);
        } else {
            syncDoConfigTasks(player, "failure", type, number);
        }

    }

    private void syncDoConfigTasks(Player player, String path, ToolType type, Double value) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> doConfigTasks(player, path, type, value));
    }

    private void doConfigTasks(Player player, String path, ToolType type, Double value) {
        String typeStr = type == null ? "" : type.toString();
        String valueStr = value == null ? "" : value.toString();
        String unit = this.plugin.getToolManager().getMainUnit(type);

        doConfigTasks(player, path + ".all", typeStr, valueStr, unit);
        doConfigTasks(player, path + "." + type.name(), typeStr, valueStr, unit);
    }

    private void doConfigTasks(Player player, String path, String type, String value, String unit) {
        for (String msg : this.plugin.getConfig().getStringList("validation.messages." + path)) {
            Utils.msg(player, msg.replace("{TOOL}", type).replace("{VAL}", value).replace("{UNIT}", unit));
        }

        for (String cmd : this.plugin.getConfig().getStringList("validation.commands." + path)) {
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
                    cmd.replace("{TOOL}", type).replace("{VAL}", value).replace("{UNIT}", unit).replace("{PLAYER}", player.getName()));
        }
    }

}
