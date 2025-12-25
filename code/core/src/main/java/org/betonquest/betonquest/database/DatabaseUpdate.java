package org.betonquest.betonquest.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a database update.
 */
@FunctionalInterface
public interface DatabaseUpdate {

    /**
     * Executes the update.
     *
     * @param connection the connection to the database
     * @throws SQLException if the update fails
     */
    void executeUpdate(Connection connection) throws SQLException;
}
