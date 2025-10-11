package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Connects to the database and queries it.
 */
public class Connector {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

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
    @Nullable
    private Connection connection;

    /**
     * Opens a new connection to the database.
     */
    public Connector() {
        final BetonQuest plugin = BetonQuest.getInstance();
        this.log = plugin.getLoggerFactory().create(Connector.class);
        prefix = plugin.getPluginConfig().getString("mysql.prefix", "");
        database = plugin.getDB();
        connection = database.getConnection();
    }

    /**
     * This method should be used before any other database operations.
     *
     * @return true if the connection is refreshed successfully
     */
    @SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION"})
    public final boolean refresh() {
        if (connection == null) {
            connection = database.getConnection();
        } else {
            try {
                connection.prepareStatement("SELECT 1").executeQuery().close();
            } catch (final SQLException e) {
                log.warn("Database connection was lost, reconnecting...", e);
                database.closeConnection();
                connection = database.getConnection();
            }
        }
        return connection != null;
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
    public ResultSet querySQL(final QueryType type, final VariableResolver variableResolver) {
        final String sql = type.createSql(prefix);
        try {
            Objects.requireNonNull(connection);
            final PreparedStatement statement = connection.prepareStatement(sql);
            variableResolver.resolve(statement);
            return statement.executeQuery();
        } catch (final SQLException e) {
            throw new IllegalStateException("There was a exception with SQL", e);
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
        Objects.requireNonNull(connection);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                statement.setString(i + 1, args[i]);
            }
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("There was an exception with SQL", e);
        }
    }

    /* default */ Database getDatabase() {
        return database;
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
