package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Connects to the database and queries it.
 */
@CustomLog
public class Connector {

    /**
     * Table prefix.
     */
    private final String prefix;

    /**
     * Database connection management.
     */
    private final Database database;

    /**
     * Permanently active connection to the database.
     */
    private Connection connection;

    /**
     * Opens a new connection to the database.
     */
    public Connector() {
        final BetonQuest plugin = BetonQuest.getInstance();
        prefix = plugin.getPluginConfig().getString("mysql.prefix", "");
        database = plugin.getDB();
        connection = database.getConnection();
        refresh();
    }

    /**
     * This method should be used before any other database operations.
     */
    @SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION"})
    public final void refresh() {
        try {
            connection.prepareStatement("SELECT 1").executeQuery().close();
        } catch (final SQLException e) {
            LOG.warn("Reconnecting to the database", e);
            database.closeConnection();
            connection = database.getConnection();
        }
    }

    /**
     * Queries the database with the given type and arguments.
     *
     * @param type type of the query
     * @param args arguments
     * @return ResultSet with the requested data
     */
    @SuppressWarnings("PMD.CloseResource")
    @SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE"})
    public ResultSet querySQL(final QueryType type, final String... args) {
        final String sql = type.createSql(prefix);
        try {
            final PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                try {
                    statement.setInt(i + 1, Integer.parseInt(args[i]));
                } catch (NumberFormatException nfe) {
                    statement.setString(i + 1, args[i]);
                }
            }
            return statement.executeQuery();
        } catch (final SQLException e) {
            LOG.warn("There was a exception with SQL", e);
            return null;
        }
    }

    /**
     * Updates the database with the given type and arguments.
     *
     * @param type type of the update
     * @param args arguments
     */
    public void updateSQL(final UpdateType type, final String... args) {
        final String sql = type.createSql(prefix);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                statement.setString(i + 1, args[i]);
            }
            statement.executeUpdate();
        } catch (final SQLException e) {
            LOG.error("There was an exception with SQL", e);
        }
    }
}
