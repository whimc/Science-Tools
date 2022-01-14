package edu.whimc.sciencetools.utils.sql;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.sql.migration.SchemaManager;
import java.sql.Connection;
import java.sql.SQLException;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Handles creating connections to the configured MySQL database.
 */
public class MySQLConnection {

    private final ScienceTools plugin;

    private MysqlDataSource dataSource;

    /**
     * Create an instance of {@link MySQLConnection}.
     *
     * @param plugin The plugin instance.
     */
    public MySQLConnection(ScienceTools plugin) {
        this.plugin = plugin;
        this.dataSource = new MysqlConnectionPoolDataSource();

        ConfigurationSection config = plugin.getConfig();
        this.dataSource.setServerName(config.getString("mysql.host", "localhost"));
        this.dataSource.setPortNumber(config.getInt("mysql.port", 3306));
        this.dataSource.setDatabaseName(config.getString("mysql.database", "minecraft"));
        this.dataSource.setUser(config.getString("mysql.username", "user"));
        this.dataSource.setPassword(config.getString("mysql.password", "pass"));
    }

    /**
     * Attempt to connect to the database and create/update the schema.
     *
     * @return Whether the database was successfully initialized.
     */
    public boolean initialize() {
        try (Connection connection = getConnection()) {
            if (connection == null) {
                return false;
            }

            SchemaManager manager = new SchemaManager(this.plugin, connection);
            return manager.migrate();
        } catch (SQLException unused) {
            return false;
        }
    }

    /**
     * A connection to the configured database or null if unsuccessful.
     */
    public Connection getConnection() throws SQLException {
        Connection connection = this.dataSource.getConnection();
        if (!connection.isValid(1)) {
            return null;
        }

        return connection;
    }

}
