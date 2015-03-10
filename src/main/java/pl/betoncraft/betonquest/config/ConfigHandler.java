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
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.Debug;

/**
 * 
 * @author Co0sh
 */
public class ConfigHandler {

    /**
     * Active instance of the ConfigHandler.
     */
    private static ConfigHandler instance;
    /**
     * Represents plugins root folder.
     */
    private File folder;
    /**
     * Map containing accessors for every conversation.
     */
    private HashMap<String, ConfigAccessor> conversationsMap = new HashMap<>();
    /**
     * Deprecated accessor for single conversations file, used only for updating
     * configuration.
     */
    private ConfigAccessor conversations;
    /**
     * Deprecated accessor for objectives file, used only for updating
     * configuration.
     */
    private ConfigAccessor objectives;
    /**
     * Accessor for conditions file.
     */
    private ConfigAccessor conditions;
    /**
     * Accessor for events file.
     */
    private ConfigAccessor events;
    /**
     * Accessor for messages file.
     */
    private ConfigAccessor messages;
    /**
     * Accessor for npcs file.
     */
    private ConfigAccessor npcs;
    /**
     * Accessor for journal file.
     */
    private ConfigAccessor journal;
    /**
     * Accessor for items file.
     */
    private ConfigAccessor items;

    /**
     * Creates new configuration handler, which makes it easier to access all
     * those files.
     */
    public ConfigHandler() {
        instance = this;
        // save default config if there isn't one
        BetonQuest.getInstance().saveDefaultConfig();
        BetonQuest.getInstance().reloadConfig();
        BetonQuest.getInstance().saveConfig();
        // create conversations folder if there isn't one
        folder = new File(BetonQuest.getInstance().getDataFolder(), "conversations");
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        // if it's empty copy default conversation
        if (folder.listFiles().length == 0) {
            File defaultConversation = new File(folder, "innkeeper.yml");
            try {
                Files.copy(BetonQuest.getInstance().getResource("defaultConversation.yml"),
                        defaultConversation.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // put conversations accessors in the hashmap
        for (File file : folder.listFiles()) {
            conversationsMap.put(file.getName().substring(0, file.getName().indexOf(".")),
                    new ConfigAccessor(BetonQuest.getInstance(), file, file.getName()));
        }
        // load messages safely
        try {
            messages = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance()
                    .getDataFolder(), "messages.yml"), "messages.yml");
            messages.getConfig().getString("global.plugin_prefix");
        } catch (Exception e) {
            messages = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance()
                    .getDataFolder(), "messages.yml"), "simple-messages.yml");
        }
        String simple = BetonQuest.getInstance().getConfig().getString("simple");
        if (simple != null && simple.equals("true")) {
            new File(BetonQuest.getInstance().getDataFolder(), "messages.yml").delete();
            messages = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance()
                    .getDataFolder(), "messages.yml"), "simple-messages.yml");
            BetonQuest.getInstance().getConfig().set("simple", null);
            BetonQuest.getInstance().saveConfig();
            Debug.broadcast("Using simple language files!");
        }
        // put config accesors in fields
        conversations = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest
                .getInstance().getDataFolder(), "conversations.yml"), "conversations.yml");
        objectives = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance()
                .getDataFolder(), "objectives.yml"), "objectives.yml");
        conditions = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance()
                .getDataFolder(), "conditions.yml"), "conditions.yml");
        events = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance()
                .getDataFolder(), "events.yml"), "events.yml");
        npcs = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance()
                .getDataFolder(), "npcs.yml"), "npcs.yml");
        journal = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance()
                .getDataFolder(), "journal.yml"), "journal.yml");
        items = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance()
                .getDataFolder(), "items.yml"), "items.yml");
        // save config if there isn't one
        conditions.saveDefaultConfig();
        events.saveDefaultConfig();
        messages.saveDefaultConfig();
        npcs.saveDefaultConfig();
        journal.saveDefaultConfig();
        items.saveDefaultConfig();
    }

    /**
     * Retireves from configuration the string at supplied path. The path should
     * follow this syntax: "filename.branch.(moreBranches).branch.variable". For
     * example getting color for day in journal date would be
     * "config.journal_colors.date.day". Everything should be handled as a
     * string for simplicity's sake.
     * 
     * @param rawPath
     *            path for the variable
     * @return the String object representing requested variable
     */
    public static String getString(String rawPath) {
        // get parts of path
        String[] parts = rawPath.split("\\.");
        String first = parts[0];
        String path = rawPath.substring(first.length() + 1);
        String object;
        // for every possible file try to access the path and return String
        // object
        switch (first) {
            case "config":
                object = BetonQuest.getInstance().getConfig().getString(path);
                if (object == null) {
                    // if object is null then there is no such variable at
                    // specified path
                    Debug.info("Error while accessing path: " + rawPath);
                }
                return object;
            case "conversations":
                object = null;
                // conversations should be handled with one more level, as they
                // are in
                // multiple files
                String conversationID = path.split("\\.")[0];
                String rest = path.substring(path.indexOf(".") + 1);
                if (instance.conversationsMap.get(conversationID) != null) {
                    object = instance.conversationsMap.get(conversationID).getConfig()
                            .getString(rest);
                }
                if (object == null) {
                    Debug.info("Error while accessing path: " + rawPath);
                }
                return object;
            case "objectives":
                object = instance.objectives.getConfig().getString(path);
                if (object == null) {
                    Debug.info("Error while accessing path: " + rawPath);
                }
                return object;
            case "conditions":
                object = instance.conditions.getConfig().getString(path);
                if (object == null) {
                    Debug.info("Error while accessing path: " + rawPath);
                }
                return object;
            case "events":
                object = instance.events.getConfig().getString(path);
                if (object == null) {
                    Debug.info("Error while accessing path: " + rawPath);
                }
                return object;
            case "messages":
                object = instance.messages.getConfig().getString(path);
                if (object == null) {
                    Debug.info("Error while accessing path: " + rawPath);
                }
                return object;
            case "npcs":
                object = instance.npcs.getConfig().getString(path);
                return object;
            case "journal":
                object = instance.journal.getConfig().getString(path);
                if (object == null) {
                    Debug.info("Error while accessing path: " + rawPath);
                }
                return object;
            case "items":
                object = instance.items.getConfig().getString(path);
                if (object == null) {
                    Debug.info("Error while accessing path: " + rawPath);
                }
                return object;
            default:
                Debug.info("Fatal error while accessing path: " + rawPath
                    + " (there is no such file)");
                return null;
        }
    }
    
    public static boolean setString(String path, String value) {
        if (path == null) return false;
        String[] parts = path.split("\\.");
        if (parts.length < 2) {
            Debug.info("Not enough arguments in path");
            return false;
        }
        String ID = parts[0];
        ConfigAccessor convFile = null;
        int i = 1;
        if (ID.equals("conversations")) {
            if (parts.length < 3) {
                Debug.info("Not enough arguments in path");
                return false;
            }
            Debug.info("Getting conversation accessor");
            convFile = instance.conversationsMap.get(parts[1]);
            i = 2;
        } else if (ID.equals("config")) {
            StringBuilder convPath = new StringBuilder();
            while (i < parts.length) {
                convPath.append(parts[i] + ".");
                i++;
            }
            if (convPath.length() < 2) {
                Debug.info("Path was too short");
                return false;
            }
            BetonQuest.getInstance().reloadConfig();
            BetonQuest.getInstance().getConfig().set(convPath.substring(0, convPath.length() - 1), value);
            BetonQuest.getInstance().saveConfig();
            Debug.info("Saved value to config");
            return true;
        } else {
            Debug.info("Getting standard accessor");
            try {
                convFile = (ConfigAccessor) instance.getClass().getDeclaredField(ID).get(instance);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
                    | IllegalAccessException e) {
                Debug.info("Could not get the accessor: " + e.getMessage());
                e.printStackTrace();
            }
        }
        if (convFile == null) {
            Debug.info("Accessor was null");
            return false;
        }
        StringBuilder convPath = new StringBuilder();
        while (i < parts.length) {
            convPath.append(parts[i] + ".");
            i++;
        }
        if (convPath.length() < 2) {
            Debug.info("Path was too short");
            return false;
        }
        convFile.reloadConfig();
        convFile.getConfig().set(convPath.substring(0, convPath.length() - 1), value);
        convFile.saveConfig();
        Debug.info("Saved value to config");
        return true;
    }

    /**
     * Reloads all config files
     */
    public static void reload() {
        BetonQuest.getInstance().reloadConfig();
        // put conversations accessors in the hashmap again, so it works just
        // like
        // reloading
        instance.conversationsMap.clear();
        for (File file : instance.folder.listFiles()) {
            instance.conversationsMap.put(file.getName().substring(0, file.getName().indexOf(".")),
                    new ConfigAccessor(BetonQuest.getInstance(), file, file.getName()));
        }
        String simple = BetonQuest.getInstance().getConfig().getString("simple");
        if (simple != null && simple.equals("true")) {
            new File(BetonQuest.getInstance().getDataFolder(), "messages.yml").delete();
            instance.messages = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest
                    .getInstance().getDataFolder(), "messages.yml"), "simple-messages.yml");
            BetonQuest.getInstance().getConfig().set("simple", null);
            BetonQuest.getInstance().saveConfig();
            instance.messages.saveDefaultConfig();
            Debug.broadcast("Using simple language files!");
        }
        instance.conditions.reloadConfig();
        instance.events.reloadConfig();
        instance.journal.reloadConfig();
        instance.messages.reloadConfig();
        instance.npcs.reloadConfig();
        instance.objectives.reloadConfig();
        instance.items.reloadConfig();
    }

    /**
     * Retrieves a map containing all config accessors. Should be used for more
     * advanced tasks than simply getting a String. Note that conversations are
     * not included in this map. See {@link #getConversations()
     * getConversations} method for that. Conversations accessor included in
     * this map is just a deprecated old conversations file. The same situation
     * is with unused objectives accessor.
     * 
     * @return HashMap containing all config accessors
     */
    public static HashMap<String, ConfigAccessor> getConfigs() {
        HashMap<String, ConfigAccessor> map = new HashMap<>();
        map.put("conversations", instance.conversations);
        map.put("conditions", instance.conditions);
        map.put("events", instance.events);
        map.put("objectives", instance.objectives);
        map.put("journal", instance.journal);
        map.put("messages", instance.messages);
        map.put("npcs", instance.npcs);
        map.put("items", instance.items);
        return map;
    }

    /**
     * Retrieves map containing all conversation accessors.
     * 
     * @return HashMap containing conversation accessors
     */
    public static HashMap<String, ConfigAccessor> getConversations() {
        return instance.conversationsMap;
    }
}