package edu.whimc.sciencetools.utils.sql;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.models.Measurement;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Handles storing position data.
 *
 * @author Jack Henhapl
 */
public class Queryer {

    /* Query for grabbing all science tool measurements */
    private static final String QUERY_GET_PLAYER_MEASUREMENTS =
            "SELECT * "
                    +
                    "FROM whimc_sciencetools "
                    +
                    "WHERE uuid = ?";

    /* Query for adding a measurement */
    private static final String QUERY_ADD_PLAYER_MEASUREMENT =
            "INSERT INTO whimc_sciencetools "
                    +
                    "(time, uuid, username, world, x, y, z, tool, measurement) "
                    +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final ScienceTools plugin;
    private final MySQLConnection sqlConnection;

    /**
     * Create a Queryer. The callback is useful for determining if the database could be successfully initialized.
     * if the value of the Queryer in the callback is null, this means the plugin could not connect to the database.
     *
     * @param plugin The main plugin instance.
     * @param callback A callback containing an instance of this Queryer if successful and null if not.
     */
    public Queryer(ScienceTools plugin, Consumer<Queryer> callback) {
        this.plugin = plugin;
        this.sqlConnection = new MySQLConnection(plugin);

        async(() -> {
            final boolean success = sqlConnection.initialize();
            sync(() -> callback.accept(success ? this : null));
        });
    }

    private PreparedStatement getStatement(Connection connection, Measurement measurement) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(QUERY_ADD_PLAYER_MEASUREMENT);

        statement.setLong(1, measurement.getTimestamp().getTime());
        statement.setString(2, measurement.getPlayer().getUniqueId().toString());
        statement.setString(3, measurement.getPlayer().getName());
        statement.setString(4, measurement.getLocation().getWorld().getName());
        statement.setDouble(5, measurement.getLocation().getX());
        statement.setDouble(6, measurement.getLocation().getY());
        statement.setDouble(7, measurement.getLocation().getZ());
        statement.setString(8, measurement.getTool().getToolKey());
        statement.setString(9, measurement.getMeasurement());

        return statement;
    }

    private void getMeasurementsFromResultSet(ResultSet results, Consumer<List<Measurement>> callback)
            throws SQLException {
        List<Measurement> measurements = new ArrayList<>();

        while (results.next()) {
            Timestamp timestamp = new Timestamp(results.getLong("time"));
            Player player = Bukkit.getPlayer(UUID.fromString(results.getString("uuid")));
            ScienceTool tool = ScienceTools.getInstance().getToolManager().getTool(results.getString("tool"));
            String measurement = results.getString("measurement");
            World world = Bukkit.getWorld(results.getString("world"));

            if (player == null || world == null || tool == null) {
                continue;
            }

            double x = results.getDouble("x");
            double y = results.getDouble("y");
            double z = results.getDouble("z");
            sync(() -> {
                Location location = new Location(world, x, y, z);
                measurements.add(new Measurement(timestamp, player, location, tool, measurement));
            });
        }

        sync(() -> callback.accept(measurements));
    }

    /**
     * Store a new measurement in the database.
     *
     * @param measurement The measurement.
     */
    public void storeNewMeasurement(Measurement measurement) {
        async(() -> {
            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = getStatement(connection, measurement)) {
                    statement.executeUpdate();
                }
            } catch (SQLException exc) {
                exc.printStackTrace();
            }
        });
    }

    /**
     * Get all measurements for a target player.
     *
     * @param target   The target player.
     * @param callback Called with a list of measurements for the target player.
     */
    public void getMeasurements(Player target, Consumer<List<Measurement>> callback) {
        async(() -> {
            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(QUERY_GET_PLAYER_MEASUREMENTS)) {
                    statement.setString(1, target.getUniqueId().toString());
                    try (ResultSet results = statement.executeQuery()) {
                        getMeasurementsFromResultSet(results, callback);
                    }
                }
            } catch (SQLException exc) {
                exc.printStackTrace();
            }
        });
    }

    private void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }

    private void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }


}
