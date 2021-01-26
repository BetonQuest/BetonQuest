package org.betonquest.betonquest.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class ConfigAccessor {

    private final String fileName;
    private final BetonQuest plugin;
    private final AccessorType type;

    private final File configFile;
    private FileConfiguration fileConfiguration;

    /**
     * Creates a new configuration accessor. If the file is null, it won't
     * create it unless some data is added and {@link #saveConfig()} is called.
     *
     * @param file     the file in which the configuration is stored; if it's null
     *                 the config will be loaded from resource and it won't be possible to save it
     * @param fileName the name of the resource in plugin jar for pulling default values;
     *                 it does not have to match the file, so you can load from "x" and save to "y"
     * @param type     type of this accessor, useful for determining type of data stored inside
     */
    public ConfigAccessor(final File file, final String fileName, final AccessorType type) {
        plugin = BetonQuest.getInstance();
        this.fileName = fileName;
        final File dataFolder = plugin.getDataFolder();
        if (dataFolder == null) {
            throw new IllegalStateException();
        }
        this.configFile = file;
        this.type = type;
    }

    /**
     * Reloads the configuration from the file. If the file is null, it will
     * try to load defaults, and if that fails it will create an empty yaml configuration.
     */
    @SuppressWarnings("PMD.EmptyCatchBlock")
    public void reloadConfig() {
        if (configFile == null) {
            try (InputStream str = plugin.getResource(fileName)) {
                if (str == null) {
                    fileConfiguration = new YamlConfiguration();
                } else {
                    try (InputStreamReader reader = new InputStreamReader(str, StandardCharsets.UTF_8)) {
                        fileConfiguration = YamlConfiguration.loadConfiguration(reader);
                    }
                }
            } catch (final IOException exception) {
                fileConfiguration = new YamlConfiguration();
            }
        } else {
            fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
            // Look for defaults in the jar
            try (InputStream defConfigStream = plugin.getResource(fileName)) {
                if (defConfigStream != null) {
                    try (InputStreamReader reader = new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)) {
                        final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
                        fileConfiguration.setDefaults(defConfig);
                    }
                }
            } catch (final IOException e) {
                // Empty
            }
        }
    }

    /**
     * Returns the configuration. If there's no configuration yet, it will call
     * {@link #reloadConfig()} to create one.
     *
     * @return the FileConfiguration
     */
    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            this.reloadConfig();
        }
        return fileConfiguration;
    }

    /**
     * Saves the config to a file. It won't do anything if the file is null.
     * If the configuration is empty it will delete that file.
     * If the file does not exist, it will create one.
     */
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public void saveConfig() {
        if (configFile == null) {
            return;
        }
        try {
            if (getConfig().getKeys(true).isEmpty()) {
                configFile.delete();
            } else {
                getConfig().save(configFile);
            }
        } catch (final IOException e) {
            LOG.error(null, "Could not save config to " + configFile, e);
        }
    }

    /**
     * Saves the default configuration to a file. It won't do anything if the file is null.
     */
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public void saveDefaultConfig() {
        if (configFile == null) {
            return;
        }
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream input = plugin.getResource(fileName);
                     OutputStream out = Files.newOutputStream(configFile.toPath())) {
                    if (input == null) {
                        return;
                    }
                    final byte[] buffer = new byte[1024];
                    int length = input.read(buffer);
                    while (length != -1) {
                        out.write(buffer, 0, length);
                        length = input.read(buffer);
                    }
                }
            } catch (final IOException e) {
                LOG.reportException(null, e);
            }
        }
    }

    /**
     * @return the type of this accessor, useful for determining type of stored data
     */
    public AccessorType getType() {
        return type;
    }

    public enum AccessorType {
        MAIN, EVENTS, CONDITIONS, OBJECTIVES, ITEMS, JOURNAL, CONVERSATION, CUSTOM, OTHER
    }

}
