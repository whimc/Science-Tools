package edu.whimc.sciencetools.utils.sql.migration;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.sql.migration.schemas.Schema_1;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;

/**
 * Manages a linked list of MySQL schema changes that are sequentially executed on the configured database.
 * The current schema version is saved as an int inside VERSION_FILE_NAME. The manager reads this file then goes through
 * each numbered schema. If the schema version is greater than that what's saved, its migration is ran and the current
 * version is updated.
 */
public class SchemaManager {

    public static final String VERSION_FILE_NAME = ".schema_version";

    private static final SchemaVersion BASE_SCHEMA = new Schema_1();

    private final ScienceTools plugin;
    private final Connection connection;

    public SchemaManager(ScienceTools plugin, Connection connection) {
        this.plugin = plugin;
        this.connection = connection;
    }

    protected Connection getConnection() {
        return this.connection;
    }

    protected File getVersionFile() {
        return new File(this.plugin.getDataFolder(), VERSION_FILE_NAME);
    }

    private int getCurrentVersion() {
        try {
            return Integer.parseInt(new String(Files.readAllBytes(getVersionFile().toPath())));
        } catch (NumberFormatException | IOException exc) {
            return 0;
        }
    }

    /**
     * Run the migration process.
     *
     * @return Whether any changes were successful (if ran).
     */
    public boolean migrate() {
        int curVersion = getCurrentVersion();

        SchemaVersion schema = BASE_SCHEMA;
        while (schema != null) {
            if (schema.getVersion() > curVersion) {
                this.plugin.getLogger().info("Migrating to schema " + schema.getVersion() + "...");
                if (!schema.migrate(this)) {
                    return false;
                }
            }
            schema = schema.getNextSchema();
        }

        return true;
    }

}
