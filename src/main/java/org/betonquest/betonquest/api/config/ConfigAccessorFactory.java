package org.betonquest.betonquest.api.config;

import org.betonquest.betonquest.config.ConfigAccessorImpl;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Factory for {@link ConfigAccessor} instances.
 */
public interface ConfigAccessorFactory {
    /**
     * Loads a configurationFile.
     *
     * @param configurationFile the {@link File} that is represented by this {@link ConfigAccessorImpl}
     * @return the created {@link ConfigAccessor}
     * @throws InvalidConfigurationException thrown if the configurationFile could not be loaded
     * @throws FileNotFoundException         thrown if the {@code configurationFile} could not be found
     */
    default ConfigAccessor create(final File configurationFile) throws InvalidConfigurationException, FileNotFoundException {
        return create(configurationFile, null, null);
    }

    /**
     * Tries to load a resourceFile.
     *
     * @param plugin       the plugin which is the source of the resource file
     * @param resourceFile the resource file to load from the plugin
     * @return the created {@link ConfigAccessor}
     * @throws InvalidConfigurationException thrown if the resourceFile could not be loaded
     * @throws FileNotFoundException         thrown if the {@code resourceFile} could not be found
     */
    default ConfigAccessor create(final Plugin plugin, final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        return create(null, plugin, resourceFile);
    }

    /**
     * Tries to load and save a configurationFile.
     * If the configurationFile does not exist, a fallback resourceFile will be loaded
     * and then saved as the configurationFile if given.
     *
     * @param configurationFile the {@link File} that is represented by this {@link ConfigAccessorImpl}
     * @param plugin            the plugin which is the source of the resource file
     * @param resourceFile      the resource file to load from the plugin
     * @return the created {@link ConfigAccessor}
     * @throws InvalidConfigurationException thrown if the configurationFile or the resourceFile could not be loaded,
     *                                       or the resourceFile could not be saved to the configurationFile
     * @throws FileNotFoundException         thrown if the {@code configurationFile} or the {@code resourceFile}
     *                                       could not be found
     */
    ConfigAccessor create(@Nullable File configurationFile, @Nullable Plugin plugin, @Nullable String resourceFile) throws InvalidConfigurationException, FileNotFoundException;
}
