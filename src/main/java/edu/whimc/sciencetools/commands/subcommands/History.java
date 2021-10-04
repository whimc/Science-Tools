package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.Utils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

/**
 * The command for seeing a user's measurement history.
 * Command: /sciencetools history
 */
public class History extends AbstractSubCommand {

    /**
     * Constructs the History command.
     */
    public History() {
        // TODO Add start/end time arguments
        super("history", null, Arrays.asList("player"),
                "View your or someone else's measurement history", Permission.USER);
    }

    /**
     * {@inheritDoc}
     *
     * <p>View the measurement history of yourself or someone else.
     */
    @Override
    protected boolean commandRoutine(CommandSender sender, String[] args) {
        // TODO Display measurements in a GUI

        // display sender's measurements
        if (args.length == 0 || !sender.hasPermission(Permission.ADMIN.toString())) {
            if (!(sender instanceof Player)) {
                Utils.log("&cConsole measurements are not recorded!");
                return false;
            }

            sendMeasurements(sender, (Player) sender);
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Utils.msg(sender, "&4" + args[0] + " &cis not a valid player!");
            return false;
        }

        // TODO Add start/end times
        sendMeasurements(sender, target);
        return false;
    }

    private void sendMeasurements(CommandSender sender, Player target) {
        ScienceTools.getInstance().getQueryer().getMeasurements(target, measurements -> {
            if (measurements.isEmpty()) {
                Utils.msg(sender, "&7No measurements found!");
                return;
            }

            measurements.forEach(measurement -> measurement.displayToUser(sender));
            if (sender instanceof Player) {
                Utils.msg(sender, "&7Hover over a measurement to see more information about it!");
            }
        });
    }

    @Override
    protected List<String> tabRoutine(CommandSender sender, String[] args) {
        // sciencetools measurements [<player> [<start time> <end time>]]

        // <player>
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        // TODO add start/end time tab completion

        return Collections.emptyList();
    }
}
