package org.betonquest.betonquest.database;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.SortedMap;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL,
 * SQLite, etc.)
 */
@SuppressWarnings("PMD.CommentRequired")
public abstract class Database {
    protected final Plugin plugin;

    protected final String prefix;

    protected final String profileInitialName;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    @Nullable
    protected Connection con;

    protected Database(final BetonQuestLogger log, final BetonQuest plugin) {
        this.log = log;
        this.plugin = plugin;
        this.prefix = plugin.getPluginConfig().getString("mysql.prefix", "");
        this.profileInitialName = plugin.getPluginConfig().getString("profiles.initial_name", "");
    }

    public Connection getConnection() {
        try {
            if (con == null || con.isClosed() || isConnectionBroken(con)) {
                con = openConnection();
            }
        } catch (final SQLException e) {
            log.error("Failed opening database connection!", e);
        }
        if (con == null) {
            throw new IllegalStateException("Not able to create a database connection!");
        }
        return con;
    }

    private boolean isConnectionBroken(final Connection connection) throws SQLException {
        try {
            try (PreparedStatement statement = connection.prepareStatement("SELECT 1");
                 ResultSet result = statement.executeQuery()) {
                return !result.next();
            }
        } catch (final SQLException e) {
            return true;
        }
    }

    protected abstract Connection openConnection() throws SQLException;

    public void closeConnection() {
        if (con != null) {
            try {
                con.close();
            } catch (final SQLException e) {
                log.error("Failed to close the database connection!", e);
            }
        }
        con = null;
    }

    public final void createTables() {
        try {
            final SortedMap<MigrationKey, DatabaseUpdate> migrations = getMigrations();
            final Set<MigrationKey> executedMigrations = queryExecutedMigrations(getConnection());
            executedMigrations.forEach(migrations::remove);

            while (!migrations.isEmpty()) {
                final MigrationKey key = migrations.firstKey();
                final DatabaseUpdate migration = migrations.remove(key);
                migration.executeUpdate(getConnection());
                markMigrationExecuted(getConnection(), key);
            }
        } catch (final SQLException sqlException) {
            log.error("There was an exception with SQL while creating the database tables!", sqlException);
        }
    }

    /**
     * Returns a SortedMap of all migrations with an identifier as {@link MigrationKey} and the migration function as
     * Value.
     *
     * @return the SortedMap of all migrations
     */
    protected abstract SortedMap<MigrationKey, DatabaseUpdate> getMigrations();

    /**
     * Queries the database for all migrations that have been executed. The function have to ensure that the table
     * containing the executed migrations exists.
     *
     * @param connection the connection to the database
     * @return a set of all migrations, in form of {@link MigrationKey}, that have been executed
     * @throws SQLException if something went wrong with the query
     */
    protected abstract Set<MigrationKey> queryExecutedMigrations(Connection connection) throws SQLException;

    /**
     * Marks the migration as executed in the database to have been executed.
     *
     * @param connection   the connection to the database
     * @param migrationKey the specific migration to mark as executed
     * @throws SQLException if the migration could not be marked as executed
     */
    protected abstract void markMigrationExecuted(Connection connection, MigrationKey migrationKey) throws SQLException;
}
