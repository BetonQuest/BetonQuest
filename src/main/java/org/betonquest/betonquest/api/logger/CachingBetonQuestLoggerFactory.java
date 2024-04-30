package org.betonquest.betonquest.api.logger;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating {@link BetonQuestLogger} instances that decorates another factory and caches the created loggers.
 */
public class CachingBetonQuestLoggerFactory implements BetonQuestLoggerFactory {
    /**
     * The list of all Loggers.
     */
    private final Map<Class<?>, Map<String, BetonQuestLogger>> loggers;

    /**
     * The decorated factory.
     */
    private final BetonQuestLoggerFactory factory;

    /**
     * Creates a new instance of a caching logger factory.
     *
     * @param factory The factory to decorate.
     */
    public CachingBetonQuestLoggerFactory(final BetonQuestLoggerFactory factory) {
        this.factory = factory;
        this.loggers = new HashMap<>();
    }

    @Override
    public BetonQuestLogger create(final Class<?> clazz, @Nullable final String topic) {
        return loggers.computeIfAbsent(clazz, k -> new HashMap<>()).computeIfAbsent(topic, t -> factory.create(clazz, t));
    }

    @Override
    public BetonQuestLogger create(final Plugin plugin, @Nullable final String topic) {
        return loggers.computeIfAbsent(plugin.getClass(), k -> new HashMap<>()).computeIfAbsent(topic, t -> factory.create(plugin, t));
    }
}
