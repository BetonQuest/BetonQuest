package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.database.ConnectionProvider;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.Database;
import org.betonquest.betonquest.database.HikariProvider;
import org.betonquest.betonquest.database.MySQL;
import org.betonquest.betonquest.database.MySqlJdbcProvider;
import org.betonquest.betonquest.database.SQLite;
import org.betonquest.betonquest.database.SQliteJdbcProvider;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.Plugin;

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

        final boolean mySQLEnabled = config.getBoolean("mysql.enabled", false);
        final boolean hikariEnabled = config.getBoolean("mysql.hikari_pooling", true);
        Database database = null;
        if (mySQLEnabled) {
            log.debug("Connecting to MySQL database");
            final BetonQuestLogger databaseLogger = loggerFactory.create(MySQL.class, "Database");
            final String host = config.getString("mysql.host", "");
            final String port = config.getString("mysql.port", "");
            final String base = config.getString("mysql.base", "");
            final String username = config.getString("mysql.user", "");
            final String password = config.getString("mysql.pass", "");
            final ConnectionProvider connectionProvider = hikariEnabled
                    ? new HikariProvider(HikariProvider.HikariDriver.MYSQL, host, port, base, username, password)
                    : new MySqlJdbcProvider(databaseLogger, host, port, base, username, password);
            final Database mySql = new MySQL(databaseLogger, connectionProvider, plugin, config);
            try {
                mySql.getConnection();
                database = mySql;
                this.mySql = true;
                log.info("Successfully connected to MySQL database!");
            } catch (final IllegalStateException e) {
                log.warn("MySQL: " + e.getMessage(), e);
            }
        }
        if (database == null) {
            final BetonQuestLogger databaseLogger = loggerFactory.create(SQLite.class, "Database");
            final ConnectionProvider connectionProvider = hikariEnabled
                    ? new HikariProvider(HikariProvider.HikariDriver.SQLITE, plugin.getDataFolder().toPath().toString(), "database.db")
                    : new SQliteJdbcProvider(databaseLogger, plugin, "database.db");
            database = new SQLite(databaseLogger, connectionProvider, plugin, config);
            if (mySQLEnabled) {
                log.warn("No connection to the mySQL Database! Using SQLite for storing data as fallback!");
            } else {
                log.info("Using SQLite for storing data!");
            }
        }

        database.createTables();
        final Connector connector = new Connector(config.getString("mysql.prefix"), database);

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
}
