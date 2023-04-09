package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Connects to the database and queries it.
 */
public class Connector {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(Connector.class);

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
    public ResultSet querySQL(final QueryType type, final String... args) {
        return querySQL(type, statement -> {
            for (int i = 0; i < args.length; i++) {
                statement.setString(i + 1, args[i]);
            }
        });
    }

    /**
     * Queries the database with the given type and arguments.
     *
     * @param type             type of the query
     * @param variableResolver resolver for variables in prepared statements
     * @return ResultSet with the requested data
     */
    @SuppressWarnings("PMD.CloseResource")
    @SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE"})
    public ResultSet querySQL(final QueryType type, final VariableResolver variableResolver) {
        final String sql = type.createSql(prefix);
        try {
            final PreparedStatement statement = connection.prepareStatement(sql);
            variableResolver.resolve(statement);
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

    /**
     * Resolver for variables in prepared statements.
     */
    @FunctionalInterface
    public interface VariableResolver {
        /**
         * Resolves the variables in the prepared statement.
         *
         * @param statement the statement to resolve
         * @throws SQLException if there is an error resolving the variables
         */
        void resolve(PreparedStatement statement) throws SQLException;
    }
}
