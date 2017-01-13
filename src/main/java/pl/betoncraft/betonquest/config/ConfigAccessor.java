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

import pl.betoncraft.betonquest.BetonQuest;

public class ConfigAccessor {

	private final String fileName;
	private final BetonQuest plugin;
	private final AccessorType type;

	private File configFile;
	private FileConfiguration fileConfiguration;
	
	public enum AccessorType {
		MAIN, EVENTS, CONDITIONS, OBJECTIVES, ITEMS, JOURNAL, CONVERSATION, CUSTOM, OTHER
	}

	/**
	 * Creates a new configuration accessor. If the file is null, it won't
	 * create it unless some data is added and {@link #saveConfig()} is called.
	 * 
	 * @param file
	 *            the file in which the configuration is stored; if it's null
	 *            the config will be loaded from resource and it won't be possible to save it
	 * @param fileName
	 *            the name of the resource in plugin jar for pulling default values;
	 *            it does not have to match the file, so you can load from "x" and save to "y"
	 * @param type
	 *            type of this accessor, useful for determining type of data stored inside
	 */
	public ConfigAccessor(File file, String fileName, AccessorType type) {
		plugin = BetonQuest.getInstance();
		this.fileName = fileName;
		File dataFolder = plugin.getDataFolder();
		if (dataFolder == null)
			throw new IllegalStateException();
		this.configFile = file;
		this.type = type;
	}

	/**
	 * Reloads the configuration from the file. If the file is null, it will
	 * try to load defaults, and if that fails it will create an empty yaml configuration.
	 */
	public void reloadConfig() {
		if (configFile == null) {
			InputStream str = plugin.getResource(fileName);
			if (str == null) {
				fileConfiguration = new YamlConfiguration();
			} else {
				fileConfiguration = YamlConfiguration
						.loadConfiguration(new InputStreamReader(str));
			}
		} else {
			fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
			// Look for defaults in the jar
			InputStream defConfigStream = plugin.getResource(fileName);
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration
						.loadConfiguration(new InputStreamReader(defConfigStream));
				fileConfiguration.setDefaults(defConfig);
			}
		}
	}

	/**
	 * Returns the configuration. If there's no configuration yet, it will call
	 * {@link #reloadConfig()} to create one.
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
		} catch (IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
		}
	}

	/**
	 * Saves the default configuration to a file. It won't do anything if the file is null.
	 */
	public void saveDefaultConfig() {
		if (configFile == null)
			return;
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
	
	/**
	 * @return the type of this accessor, useful for determining type of stored data
	 */
	public AccessorType getType() {
		return type;
	}

}
