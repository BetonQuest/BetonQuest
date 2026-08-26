package org.betonquest.betonquest.database;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides a MySQL JDBC connection.
 */
public class MySqlJdbcProvider implements ConnectionProvider {

    /**
     * The logger instance.
     */
    private final BetonQuestLogger log;

    /**
     * The hostname of the database.
     */
    private final String hostname;

    /**
     * The port of the database.
     */
    private final String port;

    /**
     * The database name.
     */
    private final String database;

    /**
     * The user to connect with.
     */
    private final String user;

    /**
     * The password to connect with.
     */
    private final String password;

    /**
     * Creates a new MySQL JDBC connection provider.
     *
     * @param log      the logger instance
     * @param hostname the hostname of the database
     * @param port     the port of the database
     * @param database the database name
     * @param user     the user to connect with
     * @param password the password to connect with
     */
    public MySqlJdbcProvider(final BetonQuestLogger log, final String hostname, final String port, final String database, final String user, final String password) {
        this.log = log;
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    @Override
    public Connection create() {
        Connection connection = null;
        final String jdbcPath = "jdbc:mysql://%s:%s/%s?&useSSL=false".formatted(this.hostname, this.port, this.database);
        try {
            log.debug("Checking for MySQL JDBC driver...");
            Class.forName("com.mysql.jdbc.Driver");
            log.debug("Connecting via MySQL JDBC to '%s' with user '%s'".formatted(jdbcPath, this.user));
            connection = DriverManager.getConnection(jdbcPath, this.user, this.password);
            final String connectionClassName = connection.getClass().getName();
            if (!connectionClassName.startsWith("com.mysql.")) {
                log.warn("External source modified or changed the MySQL connector! We can not guarantee that BetonQuest will work correctly with this connector: '%s'".formatted(connectionClassName));
            }
            log.debug("MySQL JDBC connection established successfully.");
        } catch (final ClassNotFoundException | SQLException e) {
            log.warn("MySQL says: '%s'".formatted(e.getMessage()), e);
        }
        if (connection == null) {
            throw new IllegalStateException("Not able to create a database connection!");
        }
        return connection;
    }
}
