package org.betonquest.betonquest.logger;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for creating {@link BetonQuestLogger} instances.
 */
public class DefaultBetonQuestLoggerFactory implements BetonQuestLoggerFactory {
    /**
     * Creates a new instance.
     */
    public DefaultBetonQuestLoggerFactory() {
    }

    @Override
    @SuppressWarnings("PMD.UseProperClassLoader")
    public BetonQuestLogger create(final Class<?> clazz, @Nullable final String topic) {
        if (Plugin.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("It is not allowed to use this create method from the class '"
                    + clazz.getName() + "' which directly or indirectly extends 'org.bukkit.plugin.Plugin'!");
        }
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getClass().getClassLoader().equals(clazz.getClassLoader())) {
                return new DefaultBetonQuestLogger(plugin, plugin.getLogger(), clazz, topic);
            }
        }
        throw new IllegalStateException("The class '" + clazz.getName()
                + "' has not been loaded by a 'org.bukkit.plugin.Plugin'. "
                + "Therefore, it was not possible to create a logger for this class!");
    }

    @Override
    public BetonQuestLogger create(final Plugin plugin, @Nullable final String topic) {
        return new DefaultBetonQuestLogger(plugin, plugin.getLogger(), plugin.getClass(), topic);
    }
}
