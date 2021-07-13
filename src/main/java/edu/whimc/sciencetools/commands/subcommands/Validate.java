package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.models.sciencetool.NumericScienceTool;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolManager;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Validate extends AbstractSubCommand implements Listener {

    private final Map<UUID, Validation> validationTasks;

    public Validate() {
        super("validate", Arrays.asList("tool", "player"), Arrays.asList("world", "x", "y", "z"),
                "Have a user validate a tool", Permission.ADMIN);

        this.validationTasks = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, ScienceTools.getInstance());
    }

    private static class Validation {

        private final int taskId;
        private final double expected;
        private final NumericScienceTool tool;

        public Validation(int taskId, double expected, NumericScienceTool tool) {
            this.taskId = taskId;
            this.expected = expected;
            this.tool = tool;
        }

        public int getTaskId() {
            return this.taskId;
        }

        public double getExpected() {
            return this.expected;
        }

        public NumericScienceTool getTool() {
            return this.tool;
        }
    }

    @Override
    public boolean commandRoutine(CommandSender sender, String[] args) {
        ScienceToolManager manager = ScienceTools.getInstance().getToolManager();
        ScienceTool baseTool = manager.getTool(args[0]);

        if (baseTool == null) {
            Utils.msg(sender, "&cThe tool \"&4" + args[0] + "&c\" does not exist!");
            Utils.msg(sender, "&cAvailable tools: &7" +
                    String.join(", ", manager.numericToolTabComplete("")));
            return false;
        }

        if (!(baseTool instanceof NumericScienceTool)) {
            Utils.msg(sender, "&cYou can only validate numeric tools!");
            return false;
        }

        NumericScienceTool tool = (NumericScienceTool) baseTool;
        Player player = Bukkit.getPlayer(args[1]);

        if (player == null) {
            Utils.msg(sender, "&4" + args[1] + " &cis not a valid player!");
            return false;
        }

        double expected;

        if (args.length > super.getMinArgs()) {
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


        Utils.msg(sender, "&aValidating " + tool.getDisplayName() + " for " + player.getName());

        UUID uuid = player.getUniqueId();

        if (this.validationTasks.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(this.validationTasks.remove(uuid).taskId);
        }

        int id = Bukkit.getScheduler().scheduleSyncDelayedTask(ScienceTools.getInstance(), () -> {
            doConfigTasks(player, "timeout", tool, null);
            this.validationTasks.remove(uuid);
        }, 20 * ScienceTools.getInstance().getConfig().getInt("validation.timeout"));

        this.validationTasks.put(uuid, new Validation(id, expected, tool));
        doConfigTasks(player, "prompt", tool, null);

        return false;
    }

    @Override
    protected List<String> tabRoutine(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return ScienceTools.getInstance().getToolManager().numericToolTabComplete(args[0].toLowerCase());
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
            return Collections.emptyList();
        }
        Location loc = ((Player) sender).getLocation();
        if (args.length == 4) {
            return Collections.singletonList(Double.toString(loc.getBlockX()));
        }
        if (args.length == 5) {
            return Collections.singletonList(Double.toString(loc.getBlockY()));
        }
        if (args.length == 6) {
            return Collections.singletonList(Double.toString(loc.getBlockZ()));
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

        NumericScienceTool tool = validation.getTool();

        Pattern pat = Pattern.compile("(-?\\d*(\\.\\d+)?)");
        Matcher matcher = pat.matcher(event.getMessage().replace(",", ""));

        if (!matcher.find() || matcher.group().isEmpty()) {
            syncDoConfigTasks(player, "no-number", tool, null);
            return;
        }

        String match = matcher.group();

        syncDoConfigTasks(player, "found-number", tool, Double.valueOf(match));

        double number = Double.parseDouble(match);

        if (Math.abs(number - validation.getExpected()) < ScienceTools.getInstance().getConfig().getDouble("validation.tolerance")) {
            syncDoConfigTasks(player, "success", tool, number);
        } else {
            syncDoConfigTasks(player, "failure", tool, number);
        }

    }

    private void syncDoConfigTasks(Player player, String path, NumericScienceTool tool, Double value) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ScienceTools.getInstance(), () -> doConfigTasks(player, path, tool, value));
    }

    private void doConfigTasks(Player player, String path, NumericScienceTool tool, @Nullable Double value) {
        String typeStr = tool.getDisplayName();
        String valueStr = value == null ? "" : value.toString();
        String unit = tool.getMainUnit();

        doConfigTasks(player, path + ".all", typeStr, valueStr, unit);
        doConfigTasks(player, path + "." + tool.getToolKey(), typeStr, valueStr, unit);
    }

    private void doConfigTasks(Player player, String path, String type, String value, String unit) {
        FileConfiguration config = ScienceTools.getInstance().getConfig();
        for (String msg : config.getStringList("validation.messages." + path)) {
            Utils.msg(player, msg.replace("{TOOL}", type).replace("{VAL}", value).replace("{UNIT}", unit));
        }

        for (String cmd : config.getStringList("validation.commands." + path)) {
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
                    cmd.replace("{TOOL}", type).replace("{VAL}", value).replace("{UNIT}", unit).replace("{PLAYER}", player.getName()));
        }
    }

}
