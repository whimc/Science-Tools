package edu.whimc.sciencetools.utils.sql.migration;

import com.google.common.io.Files;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a schema change. The first schema should create the necessary database layout while subsequent
 * SchemaVersions should make changes to the existing database.
 */
public abstract class SchemaVersion {

    private final int version;
    private final SchemaVersion nextSchema;

    protected SchemaVersion(int version, SchemaVersion next) {
        this.version = version;
        this.nextSchema = next;
    }

    public int getVersion() {
        return this.version;
    }

    public SchemaVersion getNextSchema() {
        return this.nextSchema;
    }

    protected abstract void migrateRoutine(Connection connection) throws SQLException;

    /**
     * Run this schema's migration on the database.
     *
     * @param manager The schema manager.
     * @return Whether the migration was successful.
     */
    public final boolean migrate(SchemaManager manager) {
        // Migrate the database
        try {
            migrateRoutine(manager.getConnection());
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        }

        // Update the schema version
        try {
            Files.write(String.valueOf(this.version).getBytes(), manager.getVersionFile());
        } catch (IOException exc) {
            exc.printStackTrace();
            return false;
        }

        return true;
    }

}
