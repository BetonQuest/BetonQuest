package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.schedule.LastExecutionCache;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link LastExecutionCache}.
 */
public class ExecutionCacheComponent extends AbstractCoreComponent {

    /**
     * The File where last executions should be cached.
     */
    private static final String CACHE_FILE = ".cache/schedules.yml";

    /**
     * Create a new ExecutionCacheComponent.
     */
    public ExecutionCacheComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BetonQuestLoggerFactory.class, ConfigAccessorFactory.class, Reloader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(LastExecutionCache.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ConfigAccessorFactory configAccessorFactory = getDependency(ConfigAccessorFactory.class);
        final Reloader reloader = getDependency(Reloader.class);

        final FileConfigAccessor cache;
        try {
            final Path cacheFile = new File(plugin.getDataFolder(), CACHE_FILE).toPath();
            if (!Files.exists(cacheFile)) {
                Files.createDirectories(Optional.ofNullable(cacheFile.getParent()).orElseThrow());
                Files.createFile(cacheFile);
            }
            cache = configAccessorFactory.create(cacheFile.toFile());
        } catch (final IOException | InvalidConfigurationException e) {
            throw new IllegalStateException("Error while loading schedule cache: " + e.getMessage(), e);
        }
        final LastExecutionCache lastExecutionCache = new LastExecutionCache(loggerFactory.create(LastExecutionCache.class, "Cache"), cache);

        dependencyProvider.take(LastExecutionCache.class, lastExecutionCache);
        reloader.register(ReloadPhase.PACKAGES, lastExecutionCache::reload);
    }
}
