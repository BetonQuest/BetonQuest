package org.betonquest.betonquest.database;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides an SQLite JDBC connection.
 */
public class SQliteJdbcProvider implements ConnectionProvider {

    /**
     * The logger instance.
     */
    private final BetonQuestLogger log;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The location of the database file.
     */
    private final String dbLocation;

    /**
     * Creates a new SQLite JDBC provider.
     *
     * @param log        the logger instance
     * @param plugin     the plugin instance
     * @param dbLocation the location of the database file
     */
    public SQliteJdbcProvider(final BetonQuestLogger log, final Plugin plugin, final String dbLocation) {
        this.log = log;
        this.plugin = plugin;
        this.dbLocation = dbLocation;
    }

    @Override
    public Connection create() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            log.error("Unable to create plugin data folder!");
        }
        final File file = new File(plugin.getDataFolder(), dbLocation);
        if (!file.exists()) {
            log.debug("SQLite database file does not exist, creating new file: %s".formatted(file.getPath()));
            try {
                if (file.createNewFile()) {
                    log.debug("Created SQLite database file: %s".formatted(file.getPath()));
                } else {
                    log.error("Unable to create database file '%s'!".formatted(file.getPath()));
                }
            } catch (final IOException e) {
                log.error("Unable to create database!", e);
            }
        }
        Connection connection = null;
        try {
            log.debug("Connecting via SQLite JDBC to jdbc:sqlite:%s/%s".formatted(plugin.getDataFolder().toPath(), dbLocation));
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:%s/%s".formatted(plugin.getDataFolder().toPath(), dbLocation));
            log.debug("SQLite JDBC connection established successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            log.error("There was an exception with creating the Sqlite connection.", e);
        }
        if (connection == null) {
            throw new IllegalStateException("Not able to create a database connection!");
        }
        return connection;
    }
}
