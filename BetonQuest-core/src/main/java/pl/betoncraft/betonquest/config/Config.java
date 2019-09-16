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

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.config.ConfigAccessor.AccessorType;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.notify.Notify;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the configuration of the plugin
 *
 * @author Jakub Sapalski
 */
public class Config {

    private final static List<String> utilDirNames = Arrays.asList("logs", "backups", "conversations");
    private static BetonQuest plugin;
    private static Config instance;
    private static ConfigAccessor messages;
    private static ConfigAccessor internal;
    private static HashMap<String, ConfigPackage> packages = new HashMap<>();
    private static HashMap<String, QuestCanceler> cancelers = new HashMap<>();
    private static String lang;
    private static ArrayList<String> languages = new ArrayList<>();
    private File root;
    private static String defaultPackage = "default";

    public Config() {
        this(true);
    }

    /**
     * Creates new instance of the Config handler
     *
     * @param verboose controls if this object should log it's actions to the file
     */
    public Config(boolean verboose) {

        packages.clear();
        cancelers.clear();
        languages.clear();

        instance = this;
        plugin = BetonQuest.getInstance();
        root = plugin.getDataFolder();
        lang = plugin.getConfig().getString("language");

        // save default config
        plugin.saveDefaultConfig();
        // need to be sure everything is saved
        plugin.reloadConfig();
        plugin.saveConfig();

        // load messages
        messages = new ConfigAccessor(new File(root, "messages.yml"), "messages.yml", AccessorType.OTHER);
        messages.saveDefaultConfig();
        internal = new ConfigAccessor(null, "internal-messages.yml", AccessorType.OTHER);
        for (String key : messages.getConfig().getKeys(false)) {
            if (!key.equals("global")) {
                if (verboose)
                    Debug.info("Loaded " + key + " language");
                languages.add(key);
            }
        }

        defaultPackage = plugin.getConfig().getString("default_package", defaultPackage);

        // save example package
        createPackage(defaultPackage);

        // load packages
        for (File file : plugin.getDataFolder().listFiles()) {
            searchForPackages(file);
        }

        // load quest cancelers
        for (ConfigPackage pack : packages.values()) {
            ConfigurationSection s = pack.getMain().getConfig().getConfigurationSection("cancel");
            if (s == null)
                continue;
            for (String key : s.getKeys(false)) {
                String name = pack.getName() + "." + key;
                try {
                    cancelers.put(name, new QuestCanceler(name));
                } catch (InstructionParseException e) {
                    Debug.error("Could not load '" + name + "' quest canceler: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Creates package with the given name and populates it with default quest
     *
     * @param packName name of the new package
     * @return true if the package was created, false if it already existed
     */
    public static boolean createPackage(String packName) {
        File def = new File(instance.root, packName.replace("-", File.separator));
        if (!def.exists()) {
            Debug.broadcast("Deploying " + packName + " package!");
            def.mkdirs();
            saveResource(def, "main.yml");
            saveResource(def, "events.yml");
            saveResource(def, "conditions.yml");
            saveResource(def, "journal.yml");
            saveResource(def, "items.yml");
            saveResource(def, "objectives.yml");
            saveResource(def, "custom.yml");
            File conversations = new File(def, "conversations");
            conversations.mkdir();
            saveResource(conversations, "defaultConversation.yml", "innkeeper.yml");
            List<String> list = plugin.getConfig().getStringList("packages");
            if (list == null)
                list = new ArrayList<>();
            list.add(packName);
            plugin.getConfig().set("packages", list);
            plugin.saveConfig();
            return true;
        }
        return false;
    }

    /**
     * Saves resource in a root directory
     *
     * @param root     directory where the resource will be saved
     * @param resource resource name, also name of the file
     */
    private static void saveResource(File root, String resource) {
        saveResource(root, resource, resource);
    }

    /**
     * Saves the resource with the name in a root directory
     *
     * @param root     directory where the resource will be saved
     * @param resource resource name
     * @param name     file name
     */
    private static void saveResource(File root, String resource, String name) {
        if (!root.isDirectory())
            return;
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
     * Returns a map containing all quest cancelers from across all packages.
     *
     * @return the map with quest cancelers
     */
    public static HashMap<String, QuestCanceler> getCancelers() {
        return cancelers;
    }

    /**
     * Retrieves the message from the configuration in specified language and
     * replaces the variables
     *
     * @param lang      language in which the message should be retrieved
     * @param message   name of the message to retrieve
     * @param variables array of variables to replace
     * @return message in that language, or message in English, or null if it
     * does not exist
     */
    public static String getMessage(String lang, String message, String[] variables) {
        String result = messages.getConfig().getString(lang + "." + message);
        if (result == null) {
            result = messages.getConfig().getString(Config.getLanguage() + "." + message);
        }
        if (result == null) {
            result = messages.getConfig().getString("en." + message);
        }
        if (result == null) {
            result = internal.getConfig().getString(lang + "." + message);
        }
        if (result == null) {
            result = internal.getConfig().getString("en." + message);
        }
        if (result != null) {
            if (variables != null)
                for (int i = 0; i < variables.length; i++) {
                    result = result.replace("{" + (i + 1) + "}", variables[i]);
                }
            result = result.replace('&', '§');
        }
        return result;
    }

    /**
     * Retrieves the message from the configuration in specified language
     *
     * @param message name of the message to retrieve
     * @param lang    language in which the message should be retrieved
     * @return message in that language, or message in English, or null if it
     * does not exist
     */
    public static String getMessage(String lang, String message) {
        return getMessage(lang, message, null);
    }

    /**
     * @return the map of packages and their names
     */
    public static Map<String, ConfigPackage> getPackages() {
        return packages;
    }

    /**
     * Retrieves the string from across all configuration. The variables are not
     * replaced! To replace variables automatically just call getString() method
     * on ConfigPackage.
     *
     * @param address address of the string
     * @return the requested string
     */
    public static String getString(String address) {
        if (address == null)
            return null;
        String[] parts = address.split("\\.");
        if (parts.length < 2)
            return null;
        String main = parts[0];
        if (main.equals("config")) {
            return plugin.getConfig().getString(address.substring(7));
        } else if (main.equals("messages")) {
            return messages.getConfig().getString(address.substring(9));
        } else {
            ConfigPackage pack = packages.get(main);
            if (pack == null)
                return null;
            return pack.getRawString(address.substring(main.length() + 1));
        }
    }

    /**
     * Sets the string at specified address
     *
     * @param address address of the variable
     * @param value   value that needs to be set
     * @return true if it was set, false otherwise
     */
    public static boolean setString(String address, String value) {
        if (address == null)
            return false;
        String[] parts = address.split("\\.");
        if (parts.length < 2)
            return false;
        String main = parts[0];
        if (main.equals("config")) {
            plugin.getConfig().set(address.substring(7), value);
            plugin.saveConfig();
            return true;
        } else if (main.equals("messages")) {
            messages.getConfig().set(address.substring(9), value);
            messages.saveConfig();
            return true;
        } else {
            ConfigPackage pack = packages.get(main);
            if (pack == null)
                return false;
            return pack.setString(address.substring(main.length() + 1), value);
        }
    }

    /**
     * @return messages configuration
     */
    public static ConfigAccessor getMessages() {
        return messages;
    }

    /**
     * @return the default language
     */
    public static String getLanguage() {
        return lang;
    }

    /**
     * Returns the ID of a conversation assigned to specified NPC, across all
     * packages. If there are multiple assignments for the same value, the first
     * one will be returned.
     *
     * @param value the name of the NPC (as defined in <i>main.yml</i>)
     * @return the ID of the conversation assigned to this NPC or null if there
     * isn't one
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

    /**
     * Sends a message to player in his chosen language or default or English
     * (if previous not found).
     *
     * @param playerID    ID of the player
     * @param messageName ID of the message
     */
    public static void sendMessage(String playerID, String messageName) {
        sendMessage(playerID, messageName, null, null, null, null);
    }

    /**
     * Sends a message to player in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables.
     *
     * @param playerID    ID of the player
     * @param messageName ID of the message
     * @param variables   array of variables which will be inserted into the string
     */
    public static void sendMessage(String playerID, String messageName, String[] variables) {
        sendMessage(playerID, messageName, variables, null, null, null);
    }

    /**
     * Sends a message to player in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables and play the sound.
     *
     * @param playerID    ID of the player
     * @param messageName ID of the message
     * @param variables   array of variables which will be inserted into the string
     * @param soundName   name of the sound to play to the player
     */
    public static void sendMessage(String playerID, String messageName, String[] variables, String soundName) {
        sendMessage(playerID, messageName, variables, soundName, null, null);
    }

    /**
     * Sends a message to player in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables and play the sound. It will also add a prefix to the message.
     *
     * @param playerID        ID of the player
     * @param messageName     ID of the message
     * @param variables       array of variables which will be inserted into the message
     * @param soundName       name of the sound to play to the player
     * @param prefixName      ID of the prefix
     * @param prefixVariables array of variables which will be inserted into the prefix
     */
    public static void sendMessage(String playerID, String messageName, String[] variables, String soundName,
                                   String prefixName, String[] prefixVariables) {
        String message = parseMessage(playerID, messageName, variables, prefixName, prefixVariables);
        if (message == null || message.length() == 0)
            return;

        Player player = PlayerConverter.getPlayer(playerID);
        player.sendMessage(message);
        if (soundName != null) {
            playSound(playerID, soundName);
        }
    }

    public static void sendNotify(String playerID, String messageName, String category) {
        sendNotify(playerID, messageName, null, category);
    }

    public static void sendNotify(Player player, String messageName, String category) {
        sendNotify(player, messageName, null, category);
    }

    public static void sendNotify(String playerID, String messageName, String[] variables, String category) {
        sendNotify(playerID, messageName, variables, category, null);
    }

    public static void sendNotify(Player player, String messageName, String[] variables, String category) {
        sendNotify(player, messageName, variables, category, null);
    }

    public static void sendNotify(String playerID, String messageName, String[] variables, String category, Map<String, String> data) {
        sendNotify(PlayerConverter.getPlayer(playerID), messageName, variables, category, data);
    }

    /**
     * Sends a notification to player in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables and play the sound. It will also add a prefix to the message.
     *
     * @param player      player
     * @param messageName ID of the message
     * @param variables   array of variables which will be inserted into the message
     * @param category    notification category
     * @param data        custom notifyIO data
     */
    public static void sendNotify(Player player, String messageName, String[] variables, String category, Map<String, String> data) {
        String message = parseMessage(player, messageName, variables);
        if (message == null || message.length() == 0)
            return;

        Notify.get(category, data).sendNotify(message, player);
    }

    public static String parseMessage(String playerID, String messageName, String[] variables) {
        return parseMessage(playerID, messageName, variables, null, null);
    }

    public static String parseMessage(Player player, String messageName, String[] variables) {
        return parseMessage(player, messageName, variables, null, null);
    }

    public static String parseMessage(String playerID, String messageName, String[] variables, String prefixName,
                                      String[] prefixVariables) {
        return parseMessage(PlayerConverter.getPlayer(playerID), messageName, variables, prefixName, prefixVariables);
    }

    /**
     * Retrieve's a message in the language of the player, replacing variables
     *
     * @param player          player
     * @param messageName     name of the message to retrieve
     * @param variables       Variables to replace in message
     * @param prefixName      ID of the prefix
     * @param prefixVariables array of variables which will be inserted into the prefix
     */
    public static String parseMessage(Player player, String messageName, String[] variables, String prefixName,
                                      String[] prefixVariables) {
        PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
        if (playerData == null)
            return null;
        String language = playerData.getLanguage();
        String message = getMessage(language, messageName, variables);
        if (message == null || message.length() == 0)
            return null;
        if (prefixName != null) {
            String prefix = getMessage(language, prefixName, prefixVariables);
            if (prefix.length() > 0) {
                message = prefix + message;
            }
        }
        return message;
    }

    /**
     * Plays a sound specified in the plugins config to the player
     *
     * @param playerID  the uuid of the player
     * @param soundName the name of the sound to play to the player
     */
    public static void playSound(String playerID, String soundName) {
        Player player = PlayerConverter.getPlayer(playerID);
        if (player == null) return;
        String rawSound = BetonQuest.getInstance().getConfig().getString("sounds." + soundName);
        if (!rawSound.equalsIgnoreCase("false")) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf(rawSound), 1F, 1F);
            } catch (IllegalArgumentException e) {
                Debug.error("Unknown sound type: " + rawSound);
            }
        }
    }

    /**
     * @return the languages defined for this plugin
     */
    public static ArrayList<String> getLanguages() {
        return languages;
    }

    /**
     * @return the default package, as specified in the config
     */
    public static ConfigPackage getDefaultPackage() {
        return getPackages().get(defaultPackage);
    }

    private void searchForPackages(File file) {
        if (file.isDirectory() && !utilDirNames.contains(file.getName())) {
            File[] content = file.listFiles();
            for (File subFile : content) {
                if (subFile.getName().equals("main.yml")) {
                    // this is a package, add it and stop searching
                    String packPath = BetonQuest.getInstance().getDataFolder()
                            .toURI().relativize(file.toURI())
                            .toString().replace('/', ' ').trim().replace(' ', '-');
                    ConfigPackage pack = new ConfigPackage(file, packPath);
                    if (pack.isEnabled()) {
                        packages.put(packPath, pack);
                    }
                    return;
                }
            }
            for (File subFile : content) {
                searchForPackages(subFile);
            }
        }
    }
}
