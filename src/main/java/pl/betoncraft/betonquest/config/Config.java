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
import pl.betoncraft.betonquest.config.ConfigAccessor.AccessorType;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.notify.Notify;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Handles the configuration of the plugin
 *
 * @author Jakub Sapalski
 */
public class Config {

    private final static List<String> UTIL_DIR_NAMES = Arrays.asList("logs", "backups", "conversations");
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
    public Config(final boolean verboose) {

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
        for (final String key : messages.getConfig().getKeys(false)) {
            if (!key.equals("global")) {
                if (verboose) {
                    LogUtils.getLogger().log(Level.FINE, "Loaded " + key + " language");
                }
                languages.add(key);
            }
        }

        defaultPackage = plugin.getConfig().getString("default_package", defaultPackage);

        // save example package
        createDefaultPackage(defaultPackage);

        // load packages
        for (final File file : plugin.getDataFolder().listFiles()) {
            searchForPackages(file);
        }

        // load quest cancelers
        for (final ConfigPackage pack : packages.values()) {
            final ConfigurationSection section = pack.getMain().getConfig().getConfigurationSection("cancel");
            if (section == null) {
                continue;
            }
            for (final String key : section.getKeys(false)) {
                final String name = pack.getName() + "." + key;
                try {
                    cancelers.put(name, new QuestCanceler(name));
                } catch (InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not load '" + name + "' quest canceler: " + e.getMessage());
                    LogUtils.logThrowable(e);
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
    public static boolean createDefaultPackage(final String packName) {
        final File def = new File(instance.root, packName.replace("-", File.separator));
        if (!def.exists()) {
            LogUtils.getLogger().log(Level.INFO, "Deploying " + packName + " package!");
            def.mkdirs();
            saveResource(def, "default/main.yml", "main.yml");
            saveResource(def, "default/events.yml", "events.yml");
            saveResource(def, "default/conditions.yml", "conditions.yml");
            saveResource(def, "default/journal.yml", "journal.yml");
            saveResource(def, "default/items.yml", "items.yml");
            saveResource(def, "default/objectives.yml", "objectives.yml");
            saveResource(def, "default/custom.yml", "custom.yml");
            final File conversations = new File(def, "conversations");
            conversations.mkdir();
            saveResource(conversations, "default/defaultConversation.yml", "innkeeper.yml");
            plugin.saveConfig();
            return true;
        }
        return false;
    }

    /**
     * Saves the resource with the name in a root directory
     *
     * @param root     directory where the resource will be saved
     * @param resource resource name
     * @param name     file name
     */
    private static void saveResource(final File root, final String resource, final String name) {
        if (!root.isDirectory()) {
            return;
        }
        final File file = new File(root, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
                final InputStream input = plugin.getResource(resource);
                final OutputStream output = new FileOutputStream(file);
                final byte[] buffer = new byte[1024];
                int len = input.read(buffer);
                while (len != -1) {
                    output.write(buffer, 0, len);
                    len = input.read(buffer);
                }
                output.close();
            } catch (IOException e) {
                LogUtils.getLogger().log(Level.WARNING, "Could not save resource: " + e.getMessage());
                LogUtils.logThrowable(e);
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
    public static String getMessage(final String lang, final String message, final String[] variables) {
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
            if (variables != null) {
                for (int i = 0; i < variables.length; i++) {
                    result = result.replace("{" + (i + 1) + "}", variables[i]);
                }
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
    public static String getMessage(final String lang, final String message) {
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
    public static String getString(final String address) {
        if (address == null) {
            return null;
        }
        final String[] parts = address.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        final String main = parts[0];
        if (main.equals("config")) {
            return plugin.getConfig().getString(address.substring(7));
        } else if (main.equals("messages")) {
            return messages.getConfig().getString(address.substring(9));
        } else {
            final ConfigPackage pack = packages.get(main);
            if (pack == null) {
                return null;
            }
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
    @SuppressWarnings("PMD.LinguisticNaming")
    public static boolean setString(final String address, final String value) {
        if (address == null) {
            return false;
        }
        final String[] parts = address.split("\\.");
        if (parts.length < 2) {
            return false;
        }
        final String main = parts[0];
        if (main.equals("config")) {
            plugin.getConfig().set(address.substring(7), value);
            plugin.saveConfig();
            return true;
        } else if (main.equals("messages")) {
            messages.getConfig().set(address.substring(9), value);
            messages.saveConfig();
            return true;
        } else {
            final ConfigPackage pack = packages.get(main);
            if (pack == null) {
                return false;
            }
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
    public static String getNpc(final String value) {
        // load npc assignments from all packages
        for (final String packName : packages.keySet()) {
            final ConfigPackage pack = packages.get(packName);
            final ConfigurationSection assignments = pack.getMain().getConfig().getConfigurationSection("npcs");
            if (assignments != null) {
                for (final String assignment : assignments.getKeys(false)) {
                    if (assignment.equalsIgnoreCase(value)) {
                        return packName + "." + assignments.getString(assignment);
                    }
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
    public static void sendMessage(final String playerID, final String messageName) {
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
    public static void sendMessage(final String playerID, final String messageName, final String[] variables) {
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
    public static void sendMessage(final String playerID, final String messageName, final String[] variables, final String soundName) {
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
    public static void sendMessage(final String playerID, final String messageName, final String[] variables, final String soundName,
                                   final String prefixName, final String[] prefixVariables) {
        final String message = parseMessage(playerID, messageName, variables, prefixName, prefixVariables);
        if (message == null || message.length() == 0) {
            return;
        }

        final Player player = PlayerConverter.getPlayer(playerID);
        player.sendMessage(message);
        if (soundName != null) {
            playSound(playerID, soundName);
        }
    }

    public static void sendNotify(final String playerID, final String messageName, final String category) {
        sendNotify(playerID, messageName, null, category);
    }

    public static void sendNotify(final Player player, final String messageName, final String category) {
        sendNotify(player, messageName, null, category);
    }

    public static void sendNotify(final String playerID, final String messageName, final String[] variables, final String category) {
        sendNotify(playerID, messageName, variables, category, null);
    }

    public static void sendNotify(final Player player, final String messageName, final String[] variables, final String category) {
        sendNotify(player, messageName, variables, category, null);
    }

    public static void sendNotify(final String playerID, final String messageName, final String[] variables, final String category, final Map<String, String> data) {
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
    public static void sendNotify(final Player player, final String messageName, final String[] variables, final String category, final Map<String, String> data) {
        final String message = parseMessage(player, messageName, variables);
        if (message == null || message.length() == 0) {
            return;
        }

        Notify.get(category, data).sendNotify(message, player);
    }

    public static String parseMessage(final String playerID, final String messageName, final String[] variables) {
        return parseMessage(playerID, messageName, variables, null, null);
    }

    public static String parseMessage(final Player player, final String messageName, final String[] variables) {
        return parseMessage(player, messageName, variables, null, null);
    }

    public static String parseMessage(final String playerID, final String messageName, final String[] variables, final String prefixName,
                                      final String[] prefixVariables) {
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
    public static String parseMessage(final Player player, final String messageName, final String[] variables, final String prefixName,
                                      final String[] prefixVariables) {
        final PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
        if (playerData == null) {
            return null;
        }
        final String language = playerData.getLanguage();
        String message = getMessage(language, messageName, variables);
        if (message == null || message.length() == 0) {
            return null;
        }
        if (prefixName != null) {
            final String prefix = getMessage(language, prefixName, prefixVariables);
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
    public static void playSound(final String playerID, final String soundName) {
        final Player player = PlayerConverter.getPlayer(playerID);
        if (player == null) {
            return;
        }
        final String rawSound = BetonQuest.getInstance().getConfig().getString("sounds." + soundName);
        if (!rawSound.equalsIgnoreCase("false")) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf(rawSound), 1F, 1F);
            } catch (IllegalArgumentException e) {
                LogUtils.getLogger().log(Level.WARNING, "Unknown sound type: " + rawSound);
                LogUtils.logThrowable(e);
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

    private void searchForPackages(final File file) {
        if (file.isDirectory() && !UTIL_DIR_NAMES.contains(file.getName())) {
            final File[] content = file.listFiles();
            try {
                for (final File subFile : content) {
                    if (subFile.getName().equals("main.yml")) {
                        // this is a package, add it and stop searching
                        final String packPath = BetonQuest.getInstance().getDataFolder()
                                .toURI().relativize(file.toURI())
                                .toString().replace('/', ' ').trim().replace(' ', '-');
                        final ConfigPackage pack = new ConfigPackage(file, packPath);
                        if (pack.isEnabled()) {
                            packages.put(packPath, pack);
                        }
                        return;
                    }
                }
                for (final File subFile : content) {
                    searchForPackages(subFile);
                }
            } catch (final Exception e) {
                LogUtils.getLogger().log(Level.SEVERE, "Error while loading packages in path '" + file.getAbsolutePath() + "'");
                LogUtils.logThrowableIgnore(e);
            }
        }
    }
}
