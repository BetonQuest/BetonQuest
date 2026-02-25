package org.betonquest.betonquest.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Arguments for a prepared statement.
 *
 * @param args the arguments
 */
public record Arguments(Object... args) {

    /**
     * Resolves the arguments in the prepared statement.
     *
     * @param statement the statement to resolve
     * @throws SQLException if there is an error resolving the arguments
     */
    public void resolve(final PreparedStatement statement) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            statement.setObject(i + 1, args[i]);
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(args);
    }
}
