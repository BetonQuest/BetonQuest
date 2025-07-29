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
public abstract class Database {
    /**
     * The plugin instance, used for accessing plugin's data folder.
     */
    protected final Plugin plugin;

    /**
     * The prefix for the database tables, used to avoid conflicts with.
     */
    protected final String prefix;

    /**
     * The initial name for the profile, used when creating a new profile.
     */
    protected final String profileInitialName;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The current database connection.
     */
    @Nullable
    protected Connection con;

    /**
     * Creates a new Database instance.
     *
     * @param log    the BetonQuestLogger to use for logging
     * @param plugin the BetonQuest plugin instance
     */
    protected Database(final BetonQuestLogger log, final BetonQuest plugin) {
        this.log = log;
        this.plugin = plugin;
        this.prefix = plugin.getPluginConfig().getString("mysql.prefix", "");
        this.profileInitialName = plugin.getPluginConfig().getString("profile.initial_name", "default");
    }

    /**
     * Returns the current database connection.
     * If the connection is closed or broken, it will try to open a new connection.
     *
     * @return the current database connection
     */
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

    /**
     * Opens a new database connection.
     *
     * @return the new database connection
     * @throws SQLException if the connection could not be opened
     */
    protected abstract Connection openConnection() throws SQLException;

    /**
     * Closes the database connection if it is open.
     */
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

    /**
     * Creates the database tables by executing all migrations that have not been executed yet.
     */
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
