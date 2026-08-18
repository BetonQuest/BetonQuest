package org.betonquest.betonquest.kernel.component;

import com.zaxxer.hikari.pool.HikariPool;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.Database;
import org.betonquest.betonquest.database.DatabaseType;
import org.betonquest.betonquest.database.MySQL;
import org.betonquest.betonquest.database.SQLite;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Connector}.
 */
public class DatabaseComponent extends AbstractCoreComponent {

    /**
     * Whether the database is using MySQL.
     */
    private boolean mySql;

    /**
     * Create a new DatabaseComponent.
     */
    public DatabaseComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BetonQuestLoggerFactory.class, ConfigAccessor.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(DatabaseComponent.class, Connector.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final Plugin plugin = getDependency(Plugin.class);
        final BetonQuestLogger log = loggerFactory.create(DatabaseComponent.class);

        final Path databasePropertiesPath = plugin.getDataFolder().toPath().resolve("database.properties");
        final DatabaseConfig dbConfig = loadDatabaseConfig(config, log);

        final Database database = resolveDatabase(dbConfig, loggerFactory, plugin, config, databasePropertiesPath, log);

        database.createTables();
        final Connector connector = new Connector(dbConfig.prefix, database);

        dependencyProvider.take(Connector.class, connector);
        dependencyProvider.take(DatabaseComponent.class, this);
    }

    /**
     * Whether the database is using MySQL.
     *
     * @return if the database is using MySQL
     */
    public boolean usesMySQL() {
        return mySql;
    }

    private Database resolveDatabase(final DatabaseConfig dbConfig, final BetonQuestLoggerFactory loggerFactory,
                                     final Plugin plugin, final ConfigAccessor config, final Path propertiesPath,
                                     final BetonQuestLogger log) {
        if (dbConfig.type == DatabaseType.MYSQL) {
            log.debug("Connecting to MySQL database");
            try {
                final Database mySQL = new MySQL(loggerFactory.create(MySQL.class, "Database"), plugin, config, dbConfig, propertiesPath);

                try (Connection conn = mySQL.getConnection()) {
                    if (conn.isClosed()) {
                        throw new SQLException("Cannot connect to MySQL database");
                    }
                }

                this.mySql = true;
                log.info("Successfully connected to MySQL database!");
                return mySQL;
            } catch (final HikariPool.PoolInitializationException | SQLException | IllegalStateException e) {
                log.warn("Could not connect to MySQL database, falling back to SQLite: " + e.getMessage());
                log.debug("MySQL connection error trace:", e);
                log.warn("No connection to the MySQL Database! Using SQLite for storing data as fallback!");
            }
        }

        log.debug("Connecting to SQLite database");
        return new SQLite(loggerFactory.create(SQLite.class, "database"), plugin, config, "database.db", dbConfig);
    }

    private DatabaseConfig loadDatabaseConfig(final ConfigAccessor config, final BetonQuestLogger log) {
        if (config.getBoolean("database.use_properties_file")) {
            try {
                return getDatabaseConfigWithNewConfig(config);
            } catch (final IOException e) {
                log.error("Failed to load 'database.properties' file! Using default.", e);
            }
        }
        return getDatabaseConfigWithLegacyConfig(config);
    }

    private DatabaseConfig getDatabaseConfigWithNewConfig(final ConfigAccessor config) throws IOException {
        final String prefix = config.getString("database.table_prefix", "betonquest_");
        final DatabaseType type = DatabaseType.fromString(config.getString("database.type", "sqlite"));

        return new DatabaseConfig(type, null, null, null, null, null, prefix);
    }

    private DatabaseConfig getDatabaseConfigWithLegacyConfig(final ConfigAccessor config) {
        final int port = config.getInt("mysql.port", 3306);
        final String host = config.getString("mysql.host", "");
        final String base = config.getString("mysql.base", "");
        final String user = config.getString("mysql.user", "");
        final String password = config.getString("mysql.pass", "");

        final String prefix = config.getString("database.table_prefix", "");
        final DatabaseType type = DatabaseType.fromString(config.getString("database.type", "sqlite"));

        return new DatabaseConfig(type, port, host, base, user, password, prefix);
    }

    /**
     * Database configuration record.
     *
     * @param type   the database type
     * @param port   the database port
     * @param host   the database host address
     * @param base   the database name
     * @param user   the database username
     * @param pass   the database password
     * @param prefix the table prefix
     */
    public record DatabaseConfig(DatabaseType type, @Nullable Integer port, @Nullable String host,
                                 @Nullable String base,
                                 @Nullable String user, @Nullable String pass, String prefix) {

    }
}
