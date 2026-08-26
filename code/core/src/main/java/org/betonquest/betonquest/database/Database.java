package org.betonquest.betonquest.database;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
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
     * The connection provider instance.
     */
    protected final ConnectionProvider connectionProvider;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new Database instance.
     *
     * @param log                the BetonQuestLogger to use for logging
     * @param connectionProvider the connection provider instance
     * @param plugin             the BetonQuest plugin instance
     * @param config             the plugin configuration file
     */
    protected Database(final BetonQuestLogger log, final ConnectionProvider connectionProvider, final Plugin plugin, final ConfigAccessor config) {
        this.log = log;
        this.plugin = plugin;
        this.connectionProvider = connectionProvider;
        this.prefix = config.getString("mysql.prefix", "");
        this.profileInitialName = config.getString("profile.initial_name", "default");
    }

    /**
     * Returns the current database connection.
     * If the connection is closed or broken, it will try to open a new connection.
     *
     * @return the current database connection
     */
    public Connection getConnection() {
        log.debug("Requesting database connection from provider...");
        final Connection connection = connectionProvider.create();
        log.debug("Database connection acquired.");
        return connection;
    }

    /**
     * Closes the database connection if it is open.
     */
    public void closeConnection() {
        log.debug("Closing database connection...");
        connectionProvider.close();
        log.debug("Database connection closed.");
    }

    /**
     * Creates the database tables by executing all migrations that have not been executed yet.
     */
    public final void createTables() {
        log.debug("Checking database tables and running pending migrations...");
        try (Connection connection = getConnection()) {
            final SortedMap<MigrationKey, DatabaseUpdate> migrations = getMigrations();
            log.debug("Found %d registered migrations.".formatted(migrations.size()));
            final Set<MigrationKey> executedMigrations = queryExecutedMigrations(connection);
            log.debug("Found %d already executed migrations.".formatted(executedMigrations.size()));
            executedMigrations.forEach(migrations::remove);
            log.debug("Pending migrations to execute: %d".formatted(migrations.size()));

            while (!migrations.isEmpty()) {
                final MigrationKey key = migrations.firstKey();
                final DatabaseUpdate migration = migrations.remove(key);
                log.debug("Executing migration: %s".formatted(key));
                migration.executeUpdate(connection);
                markMigrationExecuted(connection, key);
                log.debug("Migration %s successfully executed and marked in database.".formatted(key));
            }
            log.debug("Database tables checked and up to date.");
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
