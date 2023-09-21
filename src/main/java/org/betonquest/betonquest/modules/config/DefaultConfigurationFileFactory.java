package org.betonquest.betonquest.modules.config;

import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.config.ConfigurationFileFactory;
import org.betonquest.betonquest.api.config.patcher.PatchTransformerRegisterer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Factory for {@link ConfigurationFile} instances.
 */
public class DefaultConfigurationFileFactory implements ConfigurationFileFactory {
    @Override
    public ConfigurationFile create(final File configurationFile, final Plugin plugin, final String resourceFile, final PatchTransformerRegisterer patchTransformerRegisterer) throws InvalidConfigurationException, FileNotFoundException {
        return ConfigurationFileImpl.create(configurationFile, plugin, resourceFile, patchTransformerRegisterer);
    }
}
