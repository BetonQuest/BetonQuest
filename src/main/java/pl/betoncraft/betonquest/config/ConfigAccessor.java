/*
 * Copyright (C) 2012
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package pl.betoncraft.betonquest.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigAccessor {

    private final String fileName;
    private final JavaPlugin plugin;

    private File configFile;
    private FileConfiguration fileConfiguration;

    /**
     * Creates a new configuration accessor.
     * 
     * @param plugin
     *          instance of the plugin
     * @param file
     *          the file in which the configuration is stored; if it's null the
     *          config will be loaded from resource, as read-only
     * @param fileName
     *          the name of the resource in plugin jar
     */
    public ConfigAccessor(JavaPlugin plugin, File file, String fileName) {
        if (plugin == null)
            throw new IllegalArgumentException("plugin cannot be null");
        if (!plugin.isEnabled())
            throw new IllegalArgumentException("plugin must be enabled");
        this.plugin = plugin;
        this.fileName = fileName;
        File dataFolder = plugin.getDataFolder();
        if (dataFolder == null)
            throw new IllegalStateException();
        this.configFile = file;
    }

    public void reloadConfig() {
        if (configFile == null) {
            fileConfiguration = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(plugin.getResource(fileName)));
        } else {
            fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
            // Look for defaults in the jar
            InputStream defConfigStream = plugin.getResource(fileName);
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defConfigStream));
                fileConfiguration.setDefaults(defConfig);
            }
        }
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            this.reloadConfig();
        }
        return fileConfiguration;
    }

    public void saveConfig() {
        if (fileConfiguration == null || configFile == null) {
            return;
        } else {
            try {
                getConfig().save(configFile);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
            }
        }
    }

    public void saveDefaultConfig() {
        if (configFile == null) return;
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                InputStream in = plugin.getResource(fileName);
                if (in == null) {
                    return;
                }
                OutputStream out = new FileOutputStream(configFile);
                byte[] buffer = new byte[1024];
                int len = in.read(buffer);
                while (len != -1) {
                    out.write(buffer, 0, len);
                    len = in.read(buffer);
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
