package edu.whimc.sciencetools.utils.sql;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.sql.migration.SchemaManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles creating connections to the configured MySQL database.
 */
public class MySQLConnection {

    private static final String URL_TEMPLATE = "jdbc:mysql://%s:%s/%s";

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final String url;
    private final int port;
    private final ScienceTools plugin;

    private Connection connection;

    /**
     * Create an instance of {@link MySQLConnection}.
     *
     * @param plugin The plugin instance.
     */
    public MySQLConnection(ScienceTools plugin) {
        this.host = plugin.getConfig().getString("mysql.host", "localhost");
        this.port = plugin.getConfig().getInt("mysql.port", 3306);
        this.database = plugin.getConfig().getString("mysql.database", "minecraft");
        this.username = plugin.getConfig().getString("mysql.username", "user");
        this.password = plugin.getConfig().getString("mysql.password", "pass");

        this.url = String.format(URL_TEMPLATE, this.host, this.port, this.database);

        this.plugin = plugin;
    }

    /**
     * Attempt to connect to the database and create/update the schema.
     *
     * @return Whether the database was successfully initialized.
     */
    public boolean initialize() {
        if (getConnection() == null) {
            return false;
        }

        SchemaManager manager = new SchemaManager(this.plugin, this.connection);
        return manager.migrate();
    }

    /**
     * A connection to the configured database or null if unsuccessful.
     */
    public Connection getConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                return this.connection;
            }

            this.connection = DriverManager.getConnection(this.url, this.username, this.password);
        } catch (SQLException unused) {
            return null;
        }

        return this.connection;
    }

}
