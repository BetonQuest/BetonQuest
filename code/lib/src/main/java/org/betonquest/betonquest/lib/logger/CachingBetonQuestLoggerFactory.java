package org.betonquest.betonquest.lib.logger;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of a caching logger factory.
     *
     * @param loggerFactory The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    public CachingBetonQuestLoggerFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
        this.loggers = new ConcurrentHashMap<>();
    }

    @Override
    public BetonQuestLogger create(final Class<?> clazz, @Nullable final String topic) {
        return loggers.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(nullableTopicToEmpty(topic), t -> loggerFactory.create(clazz, topic));
    }

    @Override
    public BetonQuestLogger create(final Plugin plugin, @Nullable final String topic) {
        return loggers.computeIfAbsent(plugin.getClass(), k -> new ConcurrentHashMap<>())
                .computeIfAbsent(nullableTopicToEmpty(topic), t -> loggerFactory.create(plugin, topic));
    }

    private String nullableTopicToEmpty(@Nullable final String topic) {
        return topic == null ? "" : topic;
    }
}
