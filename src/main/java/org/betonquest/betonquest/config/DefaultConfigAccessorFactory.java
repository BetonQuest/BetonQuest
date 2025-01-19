package org.betonquest.betonquest.config;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Factory for {@link ConfigAccessor} instances.
 */
public class DefaultConfigAccessorFactory implements ConfigAccessorFactory {
    /**
     * Creates a new DefaultConfigAccessorFactory instance.
     */
    public DefaultConfigAccessorFactory() {
    }

    @Override
    public ConfigAccessor create(@Nullable final File configurationFile, @Nullable final Plugin plugin, @Nullable final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        return new ConfigAccessorImpl(configurationFile, plugin, resourceFile);
    }
}
