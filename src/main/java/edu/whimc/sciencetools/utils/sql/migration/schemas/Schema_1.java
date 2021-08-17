package edu.whimc.sciencetools.utils.sql.migration.schemas;

import edu.whimc.sciencetools.utils.sql.migration.SchemaVersion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Schema_1 extends SchemaVersion {

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `whimc_sciencetools` (" +
                    "  `rowid`       INT    AUTO_INCREMENT NOT NULL," +
                    "  `time`        BIGINT                NOT NULL," +
                    "  `uuid`        VARCHAR(36)           NOT NULL," +
                    "  `username`    VARCHAR(16)           NOT NULL," +
                    "  `world`       VARCHAR(64)           NOT NULL," +
                    "  `x`           DOUBLE                NOT NULL," +
                    "  `y`           DOUBLE                NOT NULL," +
                    "  `z`           DOUBLE                NOT NULL," +
                    "  `measurement` TEXT                  NOT NULL," +
                    "  PRIMARY KEY      (`rowid`)," +
                    "  INDEX `time`     (`time`)," +
                    "  INDEX `uuid`     (`uuid`)," +
                    "  INDEX `username` (`username`));";

    public Schema_1() {
        super(1, null);
    }

    @Override
    protected void migrateRoutine(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
            statement.execute();
        }
    }

}
