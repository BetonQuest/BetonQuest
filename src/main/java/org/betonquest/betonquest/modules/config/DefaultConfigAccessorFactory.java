package org.betonquest.betonquest.modules.config;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Factory for {@link ConfigAccessor} instances.
 */
public class DefaultConfigAccessorFactory implements ConfigAccessorFactory {
    @Override
    public ConfigAccessor create(final File configurationFile, final Plugin plugin, final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        return new ConfigAccessorImpl(configurationFile, plugin, resourceFile);
    }
}
