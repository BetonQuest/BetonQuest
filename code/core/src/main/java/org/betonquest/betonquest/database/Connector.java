package org.betonquest.betonquest.database;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;

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
     * Opens a new connection to the database.
     *
     * @param log      the custom logger for logging errors
     * @param prefix   the database table prefix
     * @param database the database to connect to
     */
    public Connector(final BetonQuestLogger log, final String prefix, final Database database) {
        this.log = log;
        this.prefix = prefix;
        this.database = database;
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
     * @param type     type of the query
     * @param resolver resolver for placeholders in prepared statements
     * @return ResultSet with the requested data
     */
    @SuppressWarnings("PMD.CloseResource")
    public ResultSet querySQL(final QueryType type, final VariableResolver resolver) {
        final String sql = type.createSql(prefix);
        try {
            final PreparedStatement statement = database.getConnection().prepareStatement(sql);
            resolver.resolve(statement);
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
        try (PreparedStatement statement = database.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                statement.setString(i + 1, args[i]);
            }
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("There was an exception with SQL", e);
        }
    }

    /**
     * Gets the database.
     *
     * @return the database used for connections
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Resolver for placeholders in prepared statements.
     */
    @FunctionalInterface
    public interface VariableResolver {

        /**
         * Resolves the placeholders in the prepared statement.
         *
         * @param statement the statement to resolve
         * @throws SQLException if there is an error resolving the placeholders
         */
        void resolve(PreparedStatement statement) throws SQLException;
    }
}
