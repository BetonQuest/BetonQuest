package org.betonquest.betonquest.modules.config;

import lombok.CustomLog;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Represents a {@link YamlConfiguration} that is a file or a resource from a plugin.
 */
@CustomLog
public class ConfigAccessorImpl implements ConfigAccessor {

    /**
     * The file from which the {@link ConfigAccessorImpl#configuration} was loaded and will be saved to.
     */
    private final File configurationFile;
    /**
     * The loaded configurationFile represented by this {@link ConfigAccessorImpl}.
     */
    private final YamlConfiguration configuration;

    /**
     * Tries to load the configurationFile.
     * If the configurationFile does not exist the resourceFile will be loaded and then saved to the configurationFile.
     *
     * @param configurationFile the {@link File} that is represented by this {@link ConfigAccessorImpl}
     * @param plugin            the plugin which is the source of the resource file
     * @param resourceFile      the resource file to load from the plugin
     * @throws InvalidConfigurationException thrown if the configurationFile or the resourceFile could not be loaded,
     *                                       or the resourceFile could not be saved to the configurationFile
     */
    public ConfigAccessorImpl(final File configurationFile, final Plugin plugin, final String resourceFile) throws InvalidConfigurationException {
        checkValidParams(configurationFile, plugin, resourceFile);
        this.configurationFile = configurationFile;
        if (configurationFile != null && configurationFile.exists()) {
            this.configuration = readFromFile();
        } else {
            this.configuration = readFromResource(plugin, resourceFile);
            try {
                this.save();
            } catch (final IOException e) {
                throw new InvalidConfigurationException(buildExceptionMessage(true, resourceFile,
                        "could not be saved! Reason: " + e.getCause().getMessage()), e);
            }
        }
    }

    private void checkValidParams(final File configurationFile, final Plugin plugin, final String resourceFile) throws InvalidConfigurationException {
        if (configurationFile == null && plugin == null && resourceFile == null) {
            throw new InvalidConfigurationException("The configurationsFile, plugin and resourceFile are null. Pass either a configurationFile or a plugin and a resourceFile.");
        }
        if ((plugin != null) == (resourceFile == null)) {
            throw new InvalidConfigurationException("Both the plugin and the resourceFile must be defined or null!");
        }
    }

    private YamlConfiguration readFromFile() throws InvalidConfigurationException {
        return load(configurationFile, false, configurationFile.getPath());
    }

    private YamlConfiguration readFromResource(final Plugin plugin, final String resourceFile) throws InvalidConfigurationException {
        try (InputStream str = plugin.getResource(resourceFile)) {
            if (str == null) {
                throw new InvalidConfigurationException(buildExceptionMessage(true, resourceFile, "could not be found!"));
            }
            try (InputStreamReader reader = new InputStreamReader(str, StandardCharsets.UTF_8)) {
                return load(reader, true, resourceFile);
            }
        } catch (final IOException e) {
            throw new InvalidConfigurationException(buildExceptionMessage(true, resourceFile, "could not be closed! Reason: " + e.getMessage()), e);
        }
    }

    private YamlConfiguration load(final Object input, final boolean isResource, final String sourcePath) throws InvalidConfigurationException {
        try {
            final YamlConfiguration config = new YamlConfiguration();
            loadFromObject(input, config);
            return config;
        } catch (final FileNotFoundException e) {
            throw new InvalidConfigurationException(buildExceptionMessage(isResource, sourcePath,
                    "could not be found! Reason: " + e.getMessage()), e);
        } catch (final IOException e) {
            throw new InvalidConfigurationException(buildExceptionMessage(isResource, sourcePath,
                    "could not be read! Reason: " + e.getMessage()), e);
        } catch (final InvalidConfigurationException e) {
            throw new InvalidConfigurationException(buildExceptionMessage(isResource, sourcePath,
                    "contains a YAML syntax error that needs to be fixed! \n" + e.getMessage()), e);
        }
    }

    private void loadFromObject(final Object input, final YamlConfiguration config) throws IOException, InvalidConfigurationException {
        if (input instanceof File file) {
            config.load(file);
        } else if (input instanceof Reader reader) {
            config.load(reader);
        } else if (input instanceof String string) {
            config.load(string);
        }
    }

    @Override
    public YamlConfiguration getConfig() {
        return configuration;
    }

    @Override
    public final boolean save() throws IOException {
        if (configurationFile == null) {
            return false;
        }
        try {
            configuration.save(configurationFile);
            return true;
        } catch (final IOException e) {
            throw new IOException(buildExceptionMessage(false, configurationFile.getPath(),
                    "could not be saved! Reason: " + e.getMessage()), e);
        }
    }

    @Override
    public final boolean delete() {
        if (configurationFile == null) {
            return false;
        }
        return configurationFile.delete();
    }

    private String buildExceptionMessage(final boolean isResource, final String sourcePath, final String message) {
        return "The " + (isResource ? "resource" : "file") + " '" + sourcePath + "' " + message;
    }
}
