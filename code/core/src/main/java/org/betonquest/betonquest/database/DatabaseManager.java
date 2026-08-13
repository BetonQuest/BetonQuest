package org.betonquest.betonquest.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages the database connection pool using HikariCP.
 */
public class DatabaseManager {

    /**
     * Lock object for thread safety.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Hikari data source.
     */
    @Nullable
    private HikariDataSource dataSource;

    /**
     * Instances a new database manager.
     */
    public DatabaseManager() {
    }

    /**
     * Retrieves a connection from the database connection pool.
     *
     * @return a database Connection object
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        throw new IllegalStateException("Database manager not initialized!");
    }

    /**
     * Closes the database data source and releases any associated resources.
     */
    public void close() {
        lock.lock();
        try {
            if (dataSource != null) {
                dataSource.close();
                dataSource = null;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Initializes the database connection pool from Hikari properties.
     *
     * @param hikariProps the base HikariCP configuration properties
     * @param configPath  path to the external properties file
     */
    public void init(final Properties hikariProps, @Nullable final Path configPath) {
        Properties props = new Properties();

        if (configPath != null) {
            try (InputStream input = Files.newInputStream(configPath)) {
                props.load(input);
            } catch (final IOException e) {
                props = null;
            }
        }

        if (props != null) {
            hikariProps.putAll(props);
        }

        lock.lock();
        try {
            if (dataSource != null) {
                if (!dataSource.isClosed()) {
                    return;
                }
                close();
            }

            try {
                final HikariConfig config = new HikariConfig(hikariProps);
                this.dataSource = new HikariDataSource(config);
            } catch (final IllegalArgumentException e) {
                this.dataSource = null;
                throw new IllegalStateException("Failed to initialize HikariCP connection pool", e);
            }
        } finally {
            lock.unlock();
        }
    }
}
