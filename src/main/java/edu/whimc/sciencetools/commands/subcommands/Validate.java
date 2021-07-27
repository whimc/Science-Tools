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

/**
 * The command for validating a ScienceTool measurement.
 */
public class Validate extends AbstractSubCommand implements Listener {

    private final Map<UUID, Validation> validationTasks;

    /**
     * Constructs the Validate command.
     */
    public Validate() {
        super("validate", Arrays.asList("tool", "player"), Arrays.asList("world", "x", "y", "z"),
                "Have a user validate a tool", Permission.ADMIN);

        this.validationTasks = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, ScienceTools.getInstance());
    }

    /**
     * Holds validation information.
     */
    private static class Validation {

        private final int taskId;
        private final double expected;
        private final NumericScienceTool tool;

        /**
         * Constructs a Validation task.
         *
         * @param taskId the id of this task
         * @param expected the expected Double value to receive
         * @param tool the NumericScienceTool to be validated
         */
        public Validation(int taskId, double expected, NumericScienceTool tool) {
            this.taskId = taskId;
            this.expected = expected;
            this.tool = tool;
        }

        /**
         * @return the task id
         */
        public int getTaskId() {
            return this.taskId;
        }

        /**
         * @return the expected Double value
         */
        public double getExpected() {
            return this.expected;
        }

        /**
         * @return the NumericScienceTool
         */
        public NumericScienceTool getTool() {
            return this.tool;
        }
    }

    /**
     * Validates the given tool's value.
     *
     * @param sender the command's sender
     * @param args the arguments passed
     * @return false
     */
    @Override
    public boolean commandRoutine(CommandSender sender, String[] args) {
        ScienceToolManager manager = ScienceTools.getInstance().getToolManager();
        ScienceTool baseTool = manager.getTool(args[0]);

        // ensure the tool is a valid tool
        if (baseTool == null) {
            Utils.msg(sender, "&cThe tool \"&4" + args[0] + "&c\" does not exist!");
            Utils.msg(sender, "&cAvailable tools: &7" +
                    String.join(", ", manager.numericToolTabComplete("")));
            return false;
        }

        // ensure the tool is numeric
        if (!(baseTool instanceof NumericScienceTool)) {
            Utils.msg(sender, "&cYou can only validate numeric tools!");
            return false;
        }

        NumericScienceTool tool = (NumericScienceTool) baseTool;
        Player player = Bukkit.getPlayer(args[1]);

        // ensure command sender is a Player
        if (player == null) {
            Utils.msg(sender, "&4" + args[1] + " &cis not a valid player!");
            return false;
        }

        double expected;

        // handle command arguments
        if (args.length > super.getMinArgs()) {
            // ensure world is valid
            World world = Bukkit.getWorld(args[2]);
            if (world == null) {
                Utils.msg(sender, "&4" + args[2] + " &cis an invalid world!");
                return false;
            }

            // handle passed coordinates
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

    /**
     * Handles auto-completing the command.
     *
     * @param sender the command's sender
     * @param args the arguments passed
     * @return the list of auto-fillable argument options
     */
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

    /**
     * Validates the number that the player inputs into the chat.
     *
     * @param event the AsyncPlayerChatEvent
     */
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // check if player already running a validation task
        if (!this.validationTasks.containsKey(player.getUniqueId())) {
            return;
        }

        // cancel the tasks
        event.setCancelled(true);

        Validation validation = this.validationTasks.remove(player.getUniqueId());
        Bukkit.getScheduler().cancelTask(validation.getTaskId());

        // get the tool and ensure player input is numeric
        NumericScienceTool tool = validation.getTool();

        Pattern pat = Pattern.compile("(-?\\d*(\\.\\d+)?)");
        Matcher matcher = pat.matcher(event.getMessage().replace(",", ""));

        // display no-number message (set in config) if input is not numeric
        if (!matcher.find() || matcher.group().isEmpty()) {
            syncDoConfigTasks(player, "no-number", tool, null);
            return;
        }

        String match = matcher.group();

        syncDoConfigTasks(player, "found-number", tool, Double.valueOf(match));

        double number = Double.parseDouble(match);

        // ensure that the provided number is within the tool's tolerance level
        if (Math.abs(number - validation.getExpected()) < ScienceTools.getInstance().getConfig().getDouble("validation.tolerance")) {
            syncDoConfigTasks(player, "success", tool, number);
        } else {
            syncDoConfigTasks(player, "failure", tool, number);
        }

    }

    /**
     * Queues up displaying the corresponding validation message and running the corresponding
     * command defined in the config.
     *
     * @param player the current player
     * @param path the path to the desired section of the validation config
     * @param tool the NumericScienceTool
     * @param value the tool's value
     */
    private void syncDoConfigTasks(Player player, String path, NumericScienceTool tool, Double value) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ScienceTools.getInstance(), () -> doConfigTasks(player, path, tool, value));
    }

    /**
     * Displays the corresponding validation message and runs the corresponding command defined in
     * the config.
     *
     * @param player the current player
     * @param path the path to the desired section of the validation config
     * @param tool the NumericScienceTool
     * @param value the tool's value
     */
    private void doConfigTasks(Player player, String path, NumericScienceTool tool, @Nullable Double value) {
        String typeStr = tool.getDisplayName();
        String valueStr = value == null ? "" : value.toString();
        String unit = tool.getMainUnit();

        doConfigTasks(player, path + ".all", typeStr, valueStr, unit);
        doConfigTasks(player, path + "." + tool.getToolKey(), typeStr, valueStr, unit);
    }

    /**
     * Displays the corresponding validation message and runs the corresponding command defined in
     * the config.
     *
     * @param player the current player
     * @param path the path to the desired section of the validation config
     * @param type the tool's name
     * @param value the tool's value
     * @param unit the tool's unit
     */
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
