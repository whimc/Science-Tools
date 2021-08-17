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
 * The command for seeing a user's measurement history.
 * Command: /sciencetools history
 */
public class History extends AbstractSubCommand {

    /**
     * Constructs the History command.
     */
    public History() {
        super("history", null, Arrays.asList("player", "start time", "end time"), "View your measurement history or someone else's", Permission.USER);
    }

    /**
     * {@inheritDoc}
     *
     * View the measurement history of yourself or someone else.
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
