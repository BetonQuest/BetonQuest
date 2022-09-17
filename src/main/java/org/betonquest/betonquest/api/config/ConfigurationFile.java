package org.betonquest.betonquest.api.config;

import org.betonquest.betonquest.api.config.patcher.PatchTransformerRegisterer;
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
     * <br><br>
     * Available patches can be explicitly overridden by passing a {@link PatchTransformerRegisterer}.
     * Otherwise, the default patches are used.
     * <br><br>
     *
     * @param configurationFile          where to load and save the config
     * @param plugin                     to load the jar resources from
     * @param resourceFile               path to the default config in the plugin's jar
     * @param patchTransformerRegisterer a function that registers the transformers to be used for patching
     * @return a new ConfigurationFile
     * @throws InvalidConfigurationException if the configuration is invalid or could not be saved
     * @throws FileNotFoundException         if the {@code configurationFile} or {@code resourceFile} could not be found
     */
    static ConfigurationFile create(final File configurationFile, final Plugin plugin, final String resourceFile, final PatchTransformerRegisterer patchTransformerRegisterer) throws InvalidConfigurationException, FileNotFoundException {
        return ConfigurationFileImpl.create(configurationFile, plugin, resourceFile, patchTransformerRegisterer);
    }

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
     * <br><br>
     * The default patches are used when the ConfigurationFile is loaded using this method.
     * Use {@link #create(File, Plugin, String, PatchTransformerRegisterer)} to override the default patches.
     * <br><br>
     *
     * @param configurationFile where to load and save the config
     * @param plugin            to load the jar resources from
     * @param resourceFile      path to the default config in the plugin's jar
     * @return a new ConfigurationFile
     * @throws InvalidConfigurationException if the configuration is invalid or could not be saved
     * @throws FileNotFoundException         if the {@code configurationFile} or {@code resourceFile} could not be found
     */
    static ConfigurationFile create(final File configurationFile, final Plugin plugin, final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        return create(configurationFile, plugin, resourceFile, null);
    }

    /**
     * Saves the file that is represented by this {@link ConfigAccessor}.
     * This method does nothing if no configurationFile was provided in the constructor.
     *
     * @return Only returns true if the file was saved.
     * @throws IOException if the file could not be saved.
     */
    boolean save() throws IOException;

    /**
     * Delete the file that is represented by this {@link ConfigAccessor}.
     * This method does nothing if no configurationFile was provided in the constructor.
     *
     * @return Only returns true if the file was deleted and existed before.
     * @throws IOException if the file could not be deleted.
     */
    boolean delete() throws IOException;

    /**
     * Reloads from the file that is represented by this {@link ConfigAccessor}.
     * This method does nothing if no configurationFile was provided in the constructor.
     *
     * @return Only returns true if the file was successfully reloaded.
     * @throws IOException if the file could not be reloaded.
     */
    boolean reload() throws IOException;
}
