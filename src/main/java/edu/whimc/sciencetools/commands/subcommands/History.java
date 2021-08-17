package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.Utils;
import edu.whimc.sciencetools.utils.sql.Queryer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
        // TODO Add start/end time arguments
        super("history", null, Arrays.asList("player"),
                "View your measurement history or someone else's", Permission.USER);
    }

    /**
     * {@inheritDoc}
     * <p>
     * View the measurement history of yourself or someone else.
     */
    @Override
    protected boolean commandRoutine(CommandSender sender, String[] args) {
        // TODO Display measurements in a GUI
        Queryer queryer = ScienceTools.getInstance().getQueryer();

        // display sender's measurements
        if (args.length == 0 || !sender.hasPermission(Permission.ADMIN.toString())) {
            if (!(sender instanceof Player)) {
                Utils.log("&cConsole measurements are not recorded!");
                return false;
            }

            queryer.getMeasurements((Player) sender, measurements ->
                    measurements.forEach(measurement -> measurement.displayToUser(sender)));
            return false;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            Utils.msg(sender, "&4" + args[1] + " &cis not a valid player!");
            return false;
        }

        queryer.getMeasurements(player, measurements ->
                measurements.forEach(measurement -> measurement.displayToUser(sender)));

        // TODO Add start/end times
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


        // TODO add start/end time tab completion
        // <start time> <end time>
//        if (args.length == 2 || args.length == 3) {
//            return Collections.singletonList("\"" + Utils.getDateNow() + "\"");
//        }

        return Collections.emptyList();
    }
}
