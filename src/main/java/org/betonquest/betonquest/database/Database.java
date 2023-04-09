package org.betonquest.betonquest.database;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.SortedMap;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL,
 * SQLite, etc.)
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
public abstract class Database {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(Database.class);

    protected final Plugin plugin;
    protected final String prefix;
    protected Connection con;

    protected Database(final BetonQuest plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getPluginConfig().getString("mysql.prefix", "");
    }

    public Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                con = openConnection();
            }
        } catch (final SQLException e) {
            LOG.warn("Failed opening database connection: " + e.getMessage(), e);
        }
        return con;
    }

    protected abstract Connection openConnection() throws SQLException;

    public void closeConnection() {
        try {
            con.close();
        } catch (final SQLException e) {
            LOG.error("There was an exception with SQL", e);
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
                final var migration = migrations.remove(key);
                migration.executeUpdate(getConnection());
                markMigrationExecuted(getConnection(), key);
            }
        } catch (final SQLException sqlException) {
            LOG.error("There was an exception with SQL", sqlException);
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
