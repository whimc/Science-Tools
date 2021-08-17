package edu.whimc.sciencetools.utils.sql;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Handles storing position data
 *
 * @author Jack Henhapl
 */
public class Queryer {

    /**
     * Query for grabbing all science tool measurements
     */
    private static final String QUERY_GET_MEASUREMENTS = ""; // TODO implement me

    private final ScienceTools plugin;
    private final MySQLConnection sqlConnection;

    public Queryer(ScienceTools plugin, Consumer<Queryer> callback) {
        this.plugin = plugin;
        this.sqlConnection = new MySQLConnection(plugin);

        async(() -> {
            final boolean success = sqlConnection.initialize();
            sync(() -> callback.accept(success ? this : null));
        });
    }

    private PreparedStatement getStatement(Connection connection, ScienceTool tool, String measurement) throws SQLException {
        // TODO implement me
        return null;
    }

    private void storeNewMeasurement(ScienceTool toolUsed, String measurement) {
        // TODO implement me
    }

    public void getMeasurements(BiConsumer<ScienceTool, String> callback) {
        // TODO implement me
    }

    private <T> void sync(Consumer<T> cons, T val) {
        Bukkit.getScheduler().runTask(this.plugin, () -> cons.accept(val));
    }

    private void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }

    private void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }


}
