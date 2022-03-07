package org.betonquest.betonquest.api.config;

import org.betonquest.betonquest.modules.config.ConfigurationFileImpl;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Facade to make configuration file handling easier.
 */
public interface ConfigurationFile extends ConfigurationSection {

    /**
     * Uses {@link ConfigAccessor#create(File, Plugin, String)} to either load or create a {@link ConfigurationFile}.
     * <br>
     * Additionally, attempts to patch the {@code configurationFile} with a patch file.
     * This patch file must exist in the same directory as the {@code resourceFile}.
     * Its name is the one of the {@code resourceFile} but with
     * '.patch' inserted between the file name and the file extension.
     * <br>
     * E.g:
     * {@code  config.yml & config.patch.yml}
     *
     * @param configurationFile where to load and save the config
     * @param plugin            to load the jar resources from
     * @param resourceFile      path to the default config in the plugin's jar
     * @return a new ConfigurationFile
     * @throws InvalidConfigurationException if the configuration is invalid or could not be saved
     * @throws FileNotFoundException         if the {@code configurationFile} or {@code resourceFile} could not be found
     */
    static ConfigurationFile create(final File configurationFile, final Plugin plugin, final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        return ConfigurationFileImpl.create(configurationFile, plugin, resourceFile);
    }

    /**
     * @see ConfigAccessor#save()
     */
    void save() throws IOException;

    /**
     * @see ConfigAccessor#delete()
     */
    void delete() throws IOException;

    /**
     * @see ConfigAccessor#reload()
     */
    void reload() throws IOException;
}
