/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.Debug;

/**
 * Handles the configuration of the plugin
 * 
 * @author Jakub Sapalski
 */
public class Config {
    
    private static BetonQuest plugin;
    
    private static Config instance;
    
    private static ConfigAccessor messages;
    
    private static HashMap<String, ConfigPackage> packages;
    
    private static String lang;
    
    private File root;

    /**
     * Creates new instance of the Config handler
     */
    public Config() {
        
        instance = this;
        plugin = BetonQuest.getInstance();
        root = plugin.getDataFolder();
        lang = plugin.getConfig().getString("language");
        
        // save default config
        plugin.saveDefaultConfig();
        // need to be sure
        plugin.reloadConfig();
        plugin.saveConfig();
        
        // load messages
        messages = new ConfigAccessor(plugin, new File(root, "messages.yml"), "messages.yml");
        messages.saveDefaultConfig();
        saveResource(root, "advanced-messages.yml");
        
        // save example package
        createPackage("default");
        
        // load packages
        packages = new HashMap<>();
        for (File file : root.listFiles()) {
            // get directories which can be quest packages
            if (!file.isDirectory()) continue;
            if (file.getName().equals("logs") || file.getName().equals("backups") || file.getName().equals("conversations")) continue;
            // initialize ConfigPackage objects and if they are valid place them in the map
            ConfigPackage pack = new ConfigPackage(file);
            if (pack.isValid()) {
                packages.put(file.getName(), pack);
            }
        }
    }

    /**
     * Creates package with the given name and populates it with default quest
     * 
     * @param packName
     *          name of the new package
     * @return true if the package was created, false if it already existed
     */
    public static boolean createPackage(String packName) {
        File def = new File(instance.root, packName);
        if (!def.exists()) {
            Debug.broadcast("Deploying " + packName + " package!");
            def.mkdir();
            saveResource(def, "main.yml");
            saveResource(def, "events.yml");
            saveResource(def, "conditions.yml");
            saveResource(def, "journal.yml");
            saveResource(def, "items.yml");
            File conversations = new File(def, "conversations");
            conversations.mkdir();
            saveResource(conversations, "defaultConversation.yml", "innkeeper.yml");
            return true;
        }
        return false;
    }
    
    /**
     * Saves resource in a root directory
     * 
     * @param root
     *          directory where the resource will be saved
     * @param resource
     *          resource name, also name of the file
     */
    private static void saveResource(File root, String resource) {
        saveResource(root, resource, resource);
    }

    /**
     * Saves the resource with the name in a root directory
     * 
     * @param root
     *          directory where the resource will be saved
     * @param resource
     *          resource name
     * @param name
     *          file name
     */
    private static void saveResource(File root, String resource, String name) {
        if (!root.isDirectory()) return;
        File file = new File(root, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
                InputStream in = plugin.getResource(resource);
                OutputStream out = new FileOutputStream(file);
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
     * @return the current instance of the Config handler
     */
    public static Config getInstance() {
        return instance;
    }
    
    /**
     * Retrieves the message from the configuration in specified language
     * 
     * @param message
     *            name of the message to retrieve
     * @param lang
     *            language in which the message should be retrieved
     * @return message in that language, or message in English, or null if it
     *         does not exist
     */
    public static String getMessage(String message) {
        String result = messages.getConfig().getString(lang + "." + message);
        if (result == null) {
            result = messages.getConfig().getString("en." + message);
        }
        if (result != null) {
            result = result.replace('&', '§');
        }
        return result;
    }
    
    /**
     * Retrieves the ConfigPackage object for specified package
     * 
     * @param name
     *          name of the package which needs to be retrieved
     * @return the ConfigPackage object representing this package
     */
    public static ConfigPackage getPackage(String name) {
        return packages.get(name);
    }
    
    /**
     * @return the set of names of valid packages
     */
    public static Set<String> getPackageNames() {
        return packages.keySet();
    }
    
    /**
     * Retrieves the string from across all configuration. The variables are not replaced!
     * To replace variables automatically just call getString() method on ConfigPackage.
     * 
     * @param address
     *          address of the string
     * @return the requested string
     */
    public static String getString(String address) {
        if (address == null) return null;
        String main = address.split("\\.")[0];
        if (main.equals("config")) {
            return plugin.getConfig().getString(address.substring(7, address.length()));
        } else if (main.equals("messages")) {
            return messages.getConfig().getString(address.substring(9, address.length()));
        } else {
            ConfigPackage pack = packages.get(main);
            return pack.getRawString(address.substring(main.length() + 1, address.length()));
        }
    }
    
    /**
     * Sets the string at specified address
     * 
     * @param address
     *          address of the variable
     * @param value
     *          value that needs to be set
     * @return true if it was set, false otherwise
     */
    public static boolean setString(String address, String value) {
        if (address == null) return false;;
        String main = address.split("\\.")[0];
        if (main.equals("config")) {
            plugin.getConfig().set(address.substring(7, address.length()), value);
            plugin.saveConfig();
            return true;
        } else if (main.equals("messages")) {
            messages.getConfig().set(address.substring(9, address.length()), value);
            messages.saveConfig();
            return true;
        } else {
            ConfigPackage pack = packages.get(main);
            return pack.setString(address.substring(main.length()+1, address.length()), value);
        }
    }
    
    /**
     * @return the default language
     */
    public static String getLanguage() {
        return lang;
    }

    /**
     * @return the ID of the conversation assigned to this NPC
     *         or null if there isn't one 
     */
    public static String getNpc(String value) {
        // load npc assignments from all packages
        for (String packName : packages.keySet()) {
            ConfigPackage pack = packages.get(packName);
            ConfigurationSection assignemnts = pack.getMain().getConfig().getConfigurationSection("npcs");
            for (String assignment : assignemnts.getKeys(false)) {
                if (assignment.equalsIgnoreCase(value)) {
                    return packName + "." + assignemnts.getString(assignment);
                }
            }
        }
        return null;
    }
}
