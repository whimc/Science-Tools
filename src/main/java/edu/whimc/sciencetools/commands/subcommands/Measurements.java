package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.ScienceToolCommand;
import edu.whimc.sciencetools.utils.Utils;
import edu.whimc.sciencetools.utils.sql.Queryer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The command for seeing a user's measurements.
 * Command: /sciencetools measurements
 */
public class Measurements extends AbstractSubCommand {

    /**
     * Constructs the Measurements command.
     */
    public Measurements() {
        super("measurements", null, Arrays.asList("player", "start_time", "end_time"), "View your own or another player's measurements", Permission.USER);
    }

    /**
     * {@inheritDoc}
     *
     * View the measurements of yourself or someone else.
     */
    @Override
    protected boolean commandRoutine(CommandSender sender, String[] args) {
        // TODO display measurements in a better way
        Queryer queryer = ScienceTools.getInstance().getQueryer();

        // display sender's measurements
        if (args.length == 0 || !sender.hasPermission(Permission.ADMIN.toString())) {
            if (!(sender instanceof Player)) {
                Utils.log("&cConsole measurements are not recorded!");
                return false;
            }

            queryer.getMeasurements((Player) sender, measurements -> {

            });
            return false;
        }

        return false;
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

        // <start time> <end time>
        if (args.length == 2 || args.length == 3) {
            return Collections.singletonList("\"" + Utils.getDateNow() + "\"");
        }

        return Collections.emptyList();
    }
}
