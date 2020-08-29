/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.io.*;
import java.util.logging.Level;

public class ConfigAccessor {

    private final String fileName;
    private final BetonQuest plugin;
    private final AccessorType type;

    private File configFile;
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
    public void reloadConfig() {
        if (configFile == null) {
            final InputStream str = plugin.getResource(fileName);
            if (str == null) {
                fileConfiguration = new YamlConfiguration();
            } else {
                fileConfiguration = YamlConfiguration
                        .loadConfiguration(new InputStreamReader(str));
            }
        } else {
            fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
            // Look for defaults in the jar
            final InputStream defConfigStream = plugin.getResource(fileName);
            if (defConfigStream != null) {
                final YamlConfiguration defConfig = YamlConfiguration
                        .loadConfiguration(new InputStreamReader(defConfigStream));
                fileConfiguration.setDefaults(defConfig);
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
        } catch (IOException e) {
            LogUtils.getLogger().log(Level.SEVERE, "Could not save config to " + configFile);
            LogUtils.logThrowable(e);
        }
    }

    /**
     * Saves the default configuration to a file. It won't do anything if the file is null.
     */
    public void saveDefaultConfig() {
        if (configFile == null) {
            return;
        }
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                final InputStream input = plugin.getResource(fileName);
                if (input == null) {
                    return;
                }
                final OutputStream out = new FileOutputStream(configFile);
                final byte[] buffer = new byte[1024];
                int length = input.read(buffer);
                while (length != -1) {
                    out.write(buffer, 0, length);
                    length = input.read(buffer);
                }
                out.close();
            } catch (IOException e) {
                LogUtils.logThrowableReport(e);
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
