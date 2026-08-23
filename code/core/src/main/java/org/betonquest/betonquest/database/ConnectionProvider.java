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
     * Returns true if the connection is managed.
     * Managed connections are opened and closed by the provider and may not be persisted.
     * Unmanaged connections should be manually managed.
     *
     * @return true if the connection is managed, false otherwise
     */
    default boolean isManaged() {
        return false;
    }
}
