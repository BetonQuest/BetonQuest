package org.betonquest.betonquest.config;

import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Represents a {@link YamlConfiguration} that is a file or a resource from a plugin.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods"})
public class StandardConfigAccessor extends ConfigurationSectionDecorator implements FileConfigAccessor {

    /**
     * The file from which the configuration was loaded and will be saved to.
     */
    @Nullable
    private final File configurationFile;

    /**
     * Tries to load the configurationFile.
     * If the configurationFile does not exist, the resourceFile will be loaded and then saved to the configurationFile.
     *
     * @param configurationFile the {@link File} that is represented by this {@link StandardConfigAccessor}
     * @param plugin            the plugin which is the source of the resource file
     * @param resourceFile      the resource file to load from the plugin
     * @throws InvalidConfigurationException thrown if the configurationFile or the resourceFile could not be loaded,
     *                                       or the resourceFile could not be saved to the configurationFile
     * @throws FileNotFoundException         thrown if the {@code configurationFile} or the {@code resourceFile}
     *                                       could not be found
     */
    public StandardConfigAccessor(@Nullable final File configurationFile, @Nullable final Plugin plugin, @Nullable final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        super(new YamlConfiguration());
        if (configurationFile == null && plugin == null && resourceFile == null) {
            throw new IllegalArgumentException("The configurationsFile, plugin and resourceFile are null. Pass either a configurationFile or a plugin and a resourceFile.");
        }
        loadConfig(configurationFile, plugin, resourceFile);
        this.configurationFile = configurationFile;
    }

    private void loadConfig(@Nullable final File configurationFile, @Nullable final Plugin plugin, @Nullable final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        if (configurationFile != null && configurationFile.exists()) {
            readFromFile(configurationFile);
        } else if (plugin != null && resourceFile != null) {
            saveFromResource(plugin, resourceFile);
        } else {
            throw new IllegalArgumentException("Both the plugin and the resourceFile must be defined or null!");
        }
    }

    private void saveFromResource(final Plugin plugin, final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        readFromResource(plugin, resourceFile);
        try {
            this.save();
        } catch (final IOException e) {
            throw new InvalidConfigurationException(buildExceptionMessage(true, resourceFile,
                    "could not be saved to the representing file! Reason: " + e.getMessage()), e);
        }
    }

    private void readFromFile(final File configurationFile) throws InvalidConfigurationException, FileNotFoundException {
        load(configurationFile, false, configurationFile.getPath());
    }

    @SuppressWarnings("PMD.AvoidRethrowingException")
    private void readFromResource(final Plugin plugin, final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        try (InputStream stream = plugin.getResource(resourceFile)) {
            if (stream == null) {
                throw new FileNotFoundException(buildExceptionMessage(true, resourceFile, "could not be found!"));
            }
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                load(reader, true, resourceFile);
            }
        } catch (final FileNotFoundException e) {
            throw e;
        } catch (final IOException e) {
            throw new InvalidConfigurationException(buildExceptionMessage(true, resourceFile, "could not be closed! Reason: " + e.getMessage()), e);
        }
    }

    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.AvoidThrowingNewInstanceOfSameException"})
    private void load(@Nullable final Object input, final boolean isResource, final String sourcePath) throws InvalidConfigurationException, FileNotFoundException {
        try {
            loadFromObject(input, (YamlConfiguration) original);
        } catch (final FileNotFoundException e) {
            throw new FileNotFoundException(buildExceptionMessage(isResource, sourcePath,
                    "could not be found! Reason: " + e.getMessage()));
        } catch (final IOException e) {
            throw new InvalidConfigurationException(buildExceptionMessage(isResource, sourcePath,
                    "could not be read! Reason: " + e.getMessage()), e);
        } catch (final InvalidConfigurationException e) {
            throw new InvalidConfigurationException(buildExceptionMessage(isResource, sourcePath,
                    "contains a YAML syntax error that needs to be fixed! \n" + e.getMessage()), e);
        }
    }

    @SuppressWarnings("PMD.CloseResource")
    private void loadFromObject(@Nullable final Object input, final YamlConfiguration config) throws IOException, InvalidConfigurationException {
        if (input instanceof final File file) {
            config.load(file);
        } else if (input instanceof final Reader reader) {
            config.load(reader);
        } else if (input instanceof final String string) {
            config.load(string);
        }
    }

    @Override
    public Configuration getConfig() {
        return (Configuration) original;
    }

    @Override
    public final boolean save() throws IOException {
        if (configurationFile == null) {
            return false;
        }
        try {
            ((YamlConfiguration) original).save(configurationFile);
            return true;
        } catch (final IOException e) {
            throw new IOException(buildExceptionMessage(false, configurationFile.getPath(),
                    "could not be saved! Reason: " + e.getMessage()), e);
        }
    }

    @Override
    public final boolean delete() throws IOException {
        if (configurationFile == null) {
            return false;
        }
        try {
            Files.delete(configurationFile.toPath());
            return true;
        } catch (final IOException e) {
            throw new IOException(buildExceptionMessage(false, configurationFile.getPath(),
                    "could not be deleted! Reason: " + e.getMessage()), e);
        }
    }

    @Override
    public boolean reload() throws IOException {
        if (configurationFile == null) {
            return false;
        }
        try {
            readFromFile(configurationFile);
            return true;
        } catch (InvalidConfigurationException | FileNotFoundException e) {
            throw new IOException(buildExceptionMessage(false, configurationFile.getPath(),
                    "could not be reloaded! Reason: " + e.getMessage()), e);
        }
    }

    @Override
    public File getConfigurationFile() {
        if (configurationFile == null) {
            throw new IllegalStateException("The configuration file is null!");
        }
        return configurationFile;
    }

    private String buildExceptionMessage(final boolean isResource, final String sourcePath, final String message) {
        return "The " + (isResource ? "resource" : "file") + " '" + sourcePath + "' " + message;
    }
}
