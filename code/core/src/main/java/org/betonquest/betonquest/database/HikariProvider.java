package org.betonquest.betonquest.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * A connection provider that uses HikariCP.
 */
public class HikariProvider implements ConnectionProvider {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The driver to use.
     */
    private final HikariDriver driver;

    /**
     * The Hikari data source.
     */
    private final HikariDataSource dataSource;

    /**
     * Create a new HikariCP connection provider.
     *
     * @param log    the logger to use
     * @param driver the driver to use
     * @param args   the arguments to pass to the driver
     */
    public HikariProvider(final BetonQuestLogger log, final HikariDriver driver, final String... args) {
        this.log = log;
        this.driver = driver;
        if (args.length != driver.getRequiredArgs()) {
            throw new IllegalArgumentException("Invalid number of arguments '%s' != '%s' for driver '%s'"
                    .formatted(args.length, driver.getRequiredArgs(), driver.name()));
        }
        log.debug("Initializing HikariCP pool for driver %s".formatted(driver.name()));
        final HikariConfig config = driver.getConfig(args);
        this.dataSource = new HikariDataSource(config);
        log.debug("HikariCP pool initialized successfully.");
    }

    @Override
    public Connection create() {
        try {
            log.debug("Obtaining database connection from HikariCP pool...");
            final Connection connection = dataSource.getConnection();
            log.debug("Database connection obtained from HikariCP pool successfully.");
            return connection;
        } catch (final SQLException e) {
            throw new IllegalStateException("Failed to create '%s' connection via hikari pool".formatted(driver.name()), e);
        }
    }

    @Override
    public void close() {
        log.debug("Closing HikariCP data source pool...");
        dataSource.close();
        log.debug("HikariCP data source pool closed.");
    }

    /**
     * Enum for the different database drivers.
     */
    public enum HikariDriver {
        /**
         * MySQL driver configuration for HikariCP.
         * <p>
         * Takes five arguments:
         * <ul>
         * <li>0: the host of the database </li>
         * <li>1: the port of the database </li>
         * <li>2: the name of the database </li>
         * <li>3: the username to connect with </li>
         * <li>4: the password to connect with </li>
         * </ul>
         */
        MYSQL(5, args -> {
            final HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setPoolName("betonquest-mysql-pool");
            hikariConfig.setJdbcUrl("jdbc:mysql://%s:%s/%s?&useSSL=false".formatted(args[0], args[1], args[2]));
            hikariConfig.setUsername(args[3]);
            hikariConfig.setPassword(args[4]);
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            return hikariConfig;
        });

        /**
         * The number of required arguments for this driver.
         */
        private final int requiredArgs;

        /**
         * The configuration provider function.
         */
        private final Function<String[], HikariConfig> config;

        HikariDriver(final int requiredArgs, final Function<String[], HikariConfig> config) {
            this.requiredArgs = requiredArgs;
            this.config = config;
        }

        /**
         * Get the driver-specific configuration for the given arguments.
         *
         * @param args the arguments
         * @return the configuration
         */
        public HikariConfig getConfig(final String... args) {
            return config.apply(args);
        }

        /**
         * Get the number of required arguments for this driver.
         *
         * @return the number of required arguments
         */
        public int getRequiredArgs() {
            return requiredArgs;
        }
    }
}
