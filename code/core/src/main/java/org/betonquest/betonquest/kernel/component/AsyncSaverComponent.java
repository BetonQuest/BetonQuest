package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.database.AsyncSaver;
import org.betonquest.betonquest.database.Backup;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link AsyncSaver}.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public class AsyncSaverComponent extends AbstractCoreComponent {

    /**
     * Create a new AsyncSaverComponent.
     */
    public AsyncSaverComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BetonQuestLoggerFactory.class, ConfigAccessorFactory.class, ConfigAccessor.class, Connector.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(AsyncSaver.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ConfigAccessorFactory configAccessorFactory = getDependency(ConfigAccessorFactory.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final Connector connector = getDependency(Connector.class);

        final AsyncSaver saver = new AsyncSaver(loggerFactory.create(AsyncSaver.class, "Database"), config.getLong("mysql.reconnect_interval"), connector);
        saver.start();
        new Backup(loggerFactory, loggerFactory.create(Backup.class), configAccessorFactory, plugin.getDataFolder(), connector)
                .loadDatabaseFromBackup();

        dependencyProvider.take(AsyncSaver.class, saver);
    }
}
