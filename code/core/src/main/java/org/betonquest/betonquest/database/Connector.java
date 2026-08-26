package org.betonquest.betonquest.database;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;

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
     * @param log      the logger for debug messages
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
     * @param type           type of the query
     * @param args           arguments
     * @param resultCallback callback for the result set
     * @param errorMessage   the error message to log when the callback throws an exception
     * @throws IllegalStateException if there is an error with the SQL
     */
    public void querySQL(final QueryType type, final Arguments args, final ResultSetCallback resultCallback,
                         final String errorMessage) {
        final String sql = type.createSql(prefix);
        log.debug("Executing SQL query type '%s' with arguments '%s' and sql: %s".formatted(type, args, sql));
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            args.resolve(statement);
            try {
                resultCallback.accept(statement.executeQuery());
                log.debug("SQL query type '%s' executed and processed successfully.".formatted(type));
            } catch (final SQLException e) {
                log.debug("SQL query type '%s' failed during result processing: %s".formatted(type, e.getMessage()), e);
                throw new IllegalStateException(
                        "There was a exception with SQL processing query type '%s' with the following arguments '%s'. %s; Caused by: %s"
                                .formatted(type, args, errorMessage, e.getMessage()), e);
            }
        } catch (final SQLException e) {
            throw new IllegalStateException(
                    "There was a exception with SQL executing query type '%s' with arguments '%s': %s"
                            .formatted(type, args, e.getMessage()), e);
        }
    }

    /**
     * Updates the database with the given type and arguments.
     *
     * @param type type of the update
     * @param args arguments
     * @throws IllegalStateException if there is an error with the SQL
     */
    public void updateSQL(final UpdateType type, final Arguments args) {
        final String sql = type.createSql(prefix);
        log.debug("Executing SQL update type '%s' with arguments '%s' and sql: %s".formatted(type, args, sql));
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            args.resolve(statement);
            final int affectedRows = statement.executeUpdate();
            log.debug("SQL update type '%s' completed successfully. Affected rows: %d".formatted(type, affectedRows));
        } catch (final SQLException e) {
            log.debug("SQL update type '%s' failed: %s".formatted(type, e.getMessage()), e);
            throw new IllegalStateException(
                    "There was an exception with SQL executing update type '%s' with arguments '%s': %s"
                            .formatted(type, args, e.getMessage()), e);
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
     * Callback for a result set.
     */
    @FunctionalInterface
    public interface ResultSetCallback {

        /**
         * Process a result set.
         *
         * @param resultSet the result set
         * @throws SQLException if there is an error
         */
        void accept(ResultSet resultSet) throws SQLException;
    }
}
