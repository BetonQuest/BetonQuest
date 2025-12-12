package org.betonquest.betonquest.lib.logger;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * This Factory always returns the same logger instance injected in the constructor.
 */
public class SingletonLoggerFactory implements BetonQuestLoggerFactory {

    /**
     * The logger instance to return.
     */
    private final BetonQuestLogger logger;

    /**
     * Default {@link SingletonLoggerFactory} Constructor.
     *
     * @param logger the logger instance to return
     */
    public SingletonLoggerFactory(final BetonQuestLogger logger) {
        this.logger = logger;
    }

    @Override
    public BetonQuestLogger create(final Class<?> clazz, @Nullable final String topic) {
        return logger;
    }

    @Override
    public BetonQuestLogger create(final Plugin plugin, @Nullable final String topic) {
        return logger;
    }
}
