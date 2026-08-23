package org.betonquest.betonquest.database;

import java.sql.Connection;

/**
 * Provides a connection to the database.
 */
@FunctionalInterface
public interface ConnectionProvider {

    /**
     * Creates a new connection.
     *
     * @return the new connection
     */
    Connection create();

    /**
     * Closes the connection handling if necessary.
     */
    default void close() {
        // Empty
    }
}
