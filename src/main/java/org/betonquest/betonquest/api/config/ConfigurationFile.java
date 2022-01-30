package org.betonquest.betonquest.api.config;

import org.betonquest.betonquest.modules.config.ConfigurationFileImpl;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Facade to make configuration file handling easier.
 */
public interface ConfigurationFile extends ConfigurationSection {

    /**
     * Loads a {@link ConfigurationFile} or creates it if absent.
     * The {@code configurationFile} is searched in the given plugin's directory.
     * A new {@code configurationFile} will be created using the given
     * {@code resourceFile} from the given plugin's jar resources if there is no file.
     * <p>
     * Also attempts to patch the {@code configurationFile} with a patch file.
     * That patch file must exist in the same directory as the resourceFile and must have the same name
     * plus a file extension of '.patch'.
     * <br>
     * E.g:
     * {@code  config.yml & config.yml.patch}
     *
     * @param configurationFile where to load and save the config
     * @param plugin            to load the jar resources from
     * @param resourceFile      path to the default config in the plugin's jar
     * @return a new ConfigurationFile
     * @throws InvalidConfigurationException if the configuration is invalid
     */
    static ConfigurationFile create(final File configurationFile, final Plugin plugin, final String resourceFile) throws InvalidConfigurationException {
        if (configurationFile == null || plugin == null || resourceFile == null) {
            throw new InvalidConfigurationException("The configurationFile, plugin and resourceFile must be defined but were null.");
        }

        final ConfigAccessor accessor = ConfigAccessor.create(configurationFile, plugin, resourceFile);
        final ConfigAccessor patchAccessor = createPatchAccessor(plugin, resourceFile);
        return new ConfigurationFileImpl(accessor, patchAccessor);
    }

    private static ConfigAccessor createPatchAccessor(final Plugin plugin, final String resourceFile) throws InvalidConfigurationException {
        int index = resourceFile.lastIndexOf('.');
        final int separatorIndex = resourceFile.lastIndexOf(File.pathSeparator);
        if (index < separatorIndex) {
            index = -1;
        }
        if (index == -1) {
            index = resourceFile.length();
        }
        final String resourceFilePatch = resourceFile.substring(0, index) + ".patch" + resourceFile.substring(index);
        try {
            return ConfigAccessor.create(plugin, resourceFilePatch);
        } catch (final InvalidConfigurationException e) {
            if (e.getMessage().endsWith("could not be found!")) {
                ConfigurationFileImpl.logMissingResourceFile(e);
                return null;
            }
            throw e;
        }
    }

    /**
     * @see ConfigAccessor#save()
     */
    void save() throws IOException;
}
