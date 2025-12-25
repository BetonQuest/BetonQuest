package org.betonquest.betonquest.api.logger;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for {@link BetonQuestLogger} instances.
 */
public interface BetonQuestLoggerFactory {

    /**
     * Creates a logger for a given class.
     * <p>
     * Use this method to create a logger without a topic.
     *
     * @param clazz The class to create a logger for.
     * @return A {@link BetonQuestLogger} implementation.
     * @throws IllegalStateException Thrown if this is called from a class, that extends {@link Plugin}
     */
    default BetonQuestLogger create(final Class<?> clazz) {
        return create(clazz, null);
    }

    /**
     * Creates a logger for a given class.
     * <p>
     * Use this method to create a logger with a topic.
     *
     * @param clazz The class to create a logger for.
     * @param topic The optional topic of the logger.
     * @return A {@link BetonQuestLogger} implementation.
     * @throws IllegalStateException Thrown if this is called from a class, that extends {@link Plugin}
     */
    BetonQuestLogger create(Class<?> clazz, @Nullable String topic);

    /**
     * Creates a logger.
     * <p>
     * Use this method to create a logger for the {@link Plugin} class without a topic.
     * For other classes use the {@link BetonQuestLoggerFactory#create(Class)}
     * or {@link BetonQuestLoggerFactory#create(Class, String)} method.
     *
     * @param plugin The plugin which is used for logging.
     * @return A {@link BetonQuestLogger} implementation.
     */
    default BetonQuestLogger create(final Plugin plugin) {
        return create(plugin, null);
    }

    /**
     * Creates a logger.
     * <p>
     * Use this method to create a logger for the {@link Plugin} class without a topic.
     * For other classes use the {@link BetonQuestLoggerFactory#create(Class)}
     * or {@link BetonQuestLoggerFactory#create(Class, String)} method.
     *
     * @param plugin The plugin which is used for logging.
     * @param topic  The optional topic of the logger.
     * @return A {@link BetonQuestLogger} implementation.
     */
    BetonQuestLogger create(Plugin plugin, @Nullable String topic);
}
