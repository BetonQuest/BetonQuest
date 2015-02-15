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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.database.Database.QueryType;
import pl.betoncraft.betonquest.database.Database.UpdateType;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Updates configuration files to newest version.
 * 
 * @author co0sh
 */
public class ConfigUpdater {

    /**
     * BetonQuest's instance
     */
    private BetonQuest instance = BetonQuest.getInstance();
    /**
     * Main configuration instance
     */
    private FileConfiguration config = instance.getConfig();
    /**
     * Destination version. At the end of the updating process this will be the
     * current version
     */
    private final String destination = "v2";

    public ConfigUpdater() {
        String version = BetonQuest.getInstance().getConfig().getString("version", null);
        Debug.info("Initializing updater with version " + version + ", destination is "
            + destination);
        // when the config is up to date then check for pending names
        // conversion;
        // conversion will occur only if UUID is manually set to true
        if (config.getString("uuid") != null && config.getString("uuid").equals("true")
            && config.getString("convert") != null && config.getString("convert").equals("true")) {
            convertNamesToUUID();
            config.set("convert", null);
            instance.saveConfig();
        }
        // move backup files to backup folder
        for (File file : instance.getDataFolder().listFiles()) {
            if (file.getName().matches("^backup-.*\\.zip$")) {
                file.renameTo(new File(file.getParentFile().getAbsolutePath() + File.separator
                    + "backups" + File.separator + file.getName()));
                Debug.broadcast("File " + file.getName() + " moved to backup folder!");
            }
        }
        if (version != null && version.equals(destination)) {
            Debug.broadcast("Configuration up to date!");
            return;
        } else {
            Debug.broadcast("Backing up before conversion!");
            File backupFolder = new File(instance.getDataFolder(), "backups");
            if (!backupFolder.isDirectory()) {
                backupFolder.mkdir();
            }
            String outputPath = backupFolder.getAbsolutePath() + File.separator + "backup-"
                + version;
            new Zipper(instance.getDataFolder().getAbsolutePath(), outputPath);
            Debug.broadcast("Done, you can find the backup in \"backups\" directory.");
        }
        // if the version is null the plugin is updated from pre-1.3 version
        // (which
        // can be 1.0, 1.1 or 1.2)
        if (version == null) {
            updateTo1_3();
        } else if (version.equals("1.3")) {
            updateTo1_4();
        } else if (version.equals("1.4")) {
            updateTo1_4_1();
        } else if (version.equals("1.4.1")) {
            updateTo1_4_2();
        } else if (version.equals("1.4.2")) {
            updateTo1_4_3();
        } else if (version.equals("1.4.3")) {
            updateTo1_5();
        } else if (version.equals("1.5")) {
            updateTo1_5_1();
        } else if (version.equals("1.5.1")) {
            updateTo1_5_2();
        } else if (version.equals("1.5.2")) {
            updateTo1_5_3();
        } else if (version.equals("1.5.3") || version.equals("1.6")) {
            updateTo1_6();
        } else if (version.matches("^v\\d+$")) {
            // this is new, post-1.5.3 updating system, where config versions
            // are
            // numbered separately from plugin's releases
            update();
        }
        updateLanguages();
        instance.saveConfig();
        // reload configuration file to apply all possible changes
        ConfigHandler.reload();
        addChangelog();
        Debug.broadcast("Successfully converted configuration to the right format!");
    }

    /**
     * Invokes method that updates config from current version to the next. It
     * repeats itself until everything is converted.
     */
    private void update() {
        String version = config.getString("version", null);
        // if the version is the same as destination, updating process is
        // finished
        if (version == null || version.equals(destination))
            return;
        try {
            // call the right updating method
            Method method = this.getClass().getDeclaredMethod("update_from_" + version);
            method.setAccessible(true);
            method.invoke(this);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        // update again until destination is reached
        update();
    }

    @SuppressWarnings("unused")
    private void update_from_v1() {
        Debug.info("Starting update from v1 to v2");
        config.set("debug", "false");
        Debug.broadcast("Added debug option to configuration.");
        config.set("version", "v2");
        instance.saveConfig();
        Debug.info("Conversion to v2 finished!");
    }

    private void updateTo1_6() {
        config.set("version", "v1");
        instance.saveConfig();
        update();
    }

    private void updateTo1_5_3() {
        // nothing to update
        config.set("version", "1.5.3");
        updateTo1_6();
    }

    private void updateTo1_5_2() {
        // nothing to update
        config.set("version", "1.5.2");
        updateTo1_5_3();
    }

    private void updateTo1_5_1() {
        // nothing to update
        config.set("version", "1.5.1");
        updateTo1_5_2();
    }

    private void updateTo1_5() {
        Debug.broadcast("Started converting configuration files from v1.4 to v1.5!");
        // add sound settings
        Debug.broadcast("Adding new sound options...");
        String[] array1 = new String[] { "start", "end", "journal", "update", "full" };
        for (String string : array1) {
            config.set("sounds." + string, config.getDefaults().getString("sounds." + string));
        }
        // add colors for journal
        Debug.broadcast("Adding new journal colors options...");
        String[] array2 = new String[] { "date.day", "date.hour", "line", "text" };
        for (String string : array2) {
            config.set("journal_colors." + string,
                    config.getDefaults().getString("journal_colors." + string));
        }
        // convert conditions in events to event_condition: format
        Debug.broadcast("Changing 'conditions:' to 'event_conditions:' in events.yml...");
        ConfigAccessor events = ConfigHandler.getConfigs().get("events");
        for (String key : events.getConfig().getKeys(false)) {
            if (events.getConfig().getString(key).contains("conditions:")) {
                StringBuilder parts = new StringBuilder();
                for (String part : events.getConfig().getString(key).split(" ")) {
                    if (part.startsWith("conditions:")) {
                        parts.append("event_conditions:" + part.substring(11) + " ");
                    } else {
                        parts.append(part + " ");
                    }
                }
                events.getConfig().set(key, parts.substring(0, parts.length() - 1));
            }
        }
        Debug.broadcast("Events now use 'event_conditions:' for conditioning.");
        // convert objectives to new format
        Debug.broadcast("Converting objectives to new format...");
        ConfigAccessor objectives = ConfigHandler.getConfigs().get("objectives");
        for (String key : events.getConfig().getKeys(false)) {
            if (events.getConfig().getString(key).split(" ")[0].equalsIgnoreCase("objective")) {
                events.getConfig().set(
                        key,
                        "objective "
                            + objectives.getConfig().getString(
                                    events.getConfig().getString(key).split(" ")[1]));
                Debug.broadcast("Event " + key + " converted!");
            }
        }
        Debug.broadcast("Objectives converted!");
        // convert global locations
        String globalLocations = config.getString("global_locations");
        if (globalLocations != null && !globalLocations.equals("")) {
            StringBuilder configGlobalLocs = new StringBuilder();
            Debug.broadcast("Converting global locations to use events...");
            int i = 0;
            for (String globalLoc : config.getString("global_locations").split(",")) {
                i++;
                events.getConfig().set("global_location_" + i,
                        "objective " + objectives.getConfig().getString(globalLoc));
                configGlobalLocs.append("global_location_" + i + ",");
                Debug.broadcast("Converted " + globalLoc + " objective.");
            }
            config.set("global_locations",
                    configGlobalLocs.substring(0, configGlobalLocs.length() - 1));
            Debug.broadcast("All " + i + " global locations have been converted.");
        }
        events.saveConfig();
        Debug.broadcast("Removing old file.");
        new File(instance.getDataFolder(), "objectives.yml").delete();
        // convert books to new format
        Debug.broadcast("Converting books to new format!");
        ConfigAccessor items = ConfigHandler.getConfigs().get("items");
        for (String key : items.getConfig().getKeys(false)) {
            String string = items.getConfig().getString(key);
            if (string.split(" ")[0].equalsIgnoreCase("WRITTEN_BOOK")) {
                String text = null;
                LinkedList<String> parts = new LinkedList<String>(Arrays.asList(string.split(" ")));
                for (Iterator<String> iterator = parts.iterator(); iterator.hasNext();) {
                    String part = (String) iterator.next();
                    if (part.startsWith("text:")) {
                        text = part.substring(5);
                        iterator.remove();
                        break;
                    }
                }
                if (text != null) {
                    StringBuilder pages = new StringBuilder();
                    for (String page : Utils.pagesFromString(text.replace("_", " "), true)) {
                        pages.append(page.replaceAll(" ", "_") + "|");
                    }
                    parts.add("text:" + pages.substring(0, pages.length() - 2));
                    StringBuilder instruction = new StringBuilder();
                    for (String part : parts) {
                        instruction.append(part + " ");
                    }
                    items.getConfig().set(key,
                            instruction.toString().trim().replaceAll("\\n", "\\\\n"));
                    Debug.broadcast("Converted book " + key + ".");
                }
            }
        }
        items.saveConfig();
        Debug.broadcast("All books converted!");
        // JournalBook.pagesFromString(questItem.getText(), false);
        config.set("tellraw", "false");
        Debug.broadcast("Tellraw option added to config.yml!");
        config.set("autoupdate", "true");
        Debug.broadcast("AutoUpdater is now enabled by default! You can change this if you"
            + " want and reload the plugin, nothing will be downloaded in that case.");
        // end of update
        config.set("version", "1.5");
        Debug.broadcast("Conversion to v1.5 finished.");
        updateTo1_5_1();
    }

    private void updateTo1_4_3() {
        // nothing to update
        config.set("version", "1.4.3");
        updateTo1_5();
    }

    private void updateTo1_4_2() {
        // nothing to update
        config.set("version", "1.4.2");
        updateTo1_4_3();
    }

    private void updateTo1_4_1() {
        // nothing to update
        config.set("version", "1.4.1");
        updateTo1_4_2();
    }

    private void updateTo1_4() {
        Debug.broadcast("Started converting configuration files from v1.3 to v1.4!");
        instance.getConfig().set("autoupdate", "false");
        Debug.broadcast("Added AutoUpdate option to config. It's DISABLED by default!");
        Debug.broadcast("Moving conversation to separate files...");
        ConfigAccessor convOld = ConfigHandler.getConfigs().get("conversations");
        Set<String> keys = convOld.getConfig().getKeys(false);
        File folder = new File(instance.getDataFolder(), "conversations");
        for (File file : folder.listFiles()) {
            file.delete();
        }
        for (String convID : keys) {
            File convFile = new File(folder, convID + ".yml");
            Map<String, Object> convSection = convOld.getConfig().getConfigurationSection(convID)
                    .getValues(true);
            YamlConfiguration convNew = YamlConfiguration.loadConfiguration(convFile);
            for (String key : convSection.keySet()) {
                convNew.set(key, convSection.get(key));
            }
            try {
                convNew.save(convFile);
                Debug.broadcast("Conversation " + convID + " moved to it's own file!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Debug.broadcast("All conversations moved, deleting old file.");
        new File(instance.getDataFolder(), "conversations.yml").delete();

        // updating items
        Debug.broadcast("Starting conversion of items...");
        // this map will contain all QuestItem objects extracted from
        // configs
        HashMap<String, QuestItem> items = new HashMap<>();
        // this is counter for a number in item names (in items.yml)
        int number = 0;
        // check every event
        for (String key : ConfigHandler.getConfigs().get("events").getConfig().getKeys(false)) {
            String instructions = ConfigHandler.getString("events." + key);
            String[] parts = instructions.split(" ");
            String type = parts[0];
            // if this event has items in it do the thing
            if (type.equals("give") || type.equals("take")) {
                // define all required variables
                String amount = "";
                String conditions = "";
                String material = null;
                int data = 0;
                Map<String, Integer> enchants = new HashMap<>();
                List<String> lore = new ArrayList<>();
                String name = null;
                // for each part of the instruction string check if it
                // contains some data and if so pu it in variables
                for (String part : parts) {
                    if (part.contains("type:")) {
                        material = part.substring(5);
                    } else if (part.contains("data:")) {
                        data = Byte.valueOf(part.substring(5));
                    } else if (part.contains("enchants:")) {
                        for (String enchant : part.substring(9).split(",")) {
                            enchants.put(enchant.split(":")[0],
                                    Integer.decode(enchant.split(":")[1]));
                        }
                    } else if (part.contains("lore:")) {
                        for (String loreLine : part.substring(5).split(";")) {
                            lore.add(loreLine.replaceAll("_", " "));
                        }
                    } else if (part.contains("name:")) {
                        name = part.substring(5).replaceAll("_", " ");
                    } else if (part.contains("amount:")) {
                        amount = part;
                    } else if (part.contains("conditions:")) {
                        conditions = part;
                    }
                }
                // create an item
                String newItemID = null;
                QuestItem item = new QuestItem(material, data, enchants, name, lore);
                boolean contains = false;
                for (String itemKey : items.keySet()) {
                    if (items.get(itemKey).equalsToItem(item)) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    // generate new name for an item
                    newItemID = "item" + number;
                    number++;
                    items.put(newItemID, item);
                } else {
                    for (String itemName : items.keySet()) {
                        if (items.get(itemName).equalsToItem(item)) {
                            newItemID = itemName;
                        }
                    }
                }
                ConfigHandler
                        .getConfigs()
                        .get("events")
                        .getConfig()
                        .set(key, (type + " " + newItemID + " " + amount + " " + conditions).trim());

                // replace event with updated version
                Debug.broadcast("Extracted " + newItemID + " from " + key + " event!");
            }
        }
        // check every condition (it's almost the same code, I didn't know how
        // to do
        // it better
        for (String key : ConfigHandler.getConfigs().get("conditions").getConfig().getKeys(false)) {
            String instructions = ConfigHandler.getString("conditions." + key);
            String[] parts = instructions.split(" ");
            String type = parts[0];
            // if this condition has items do the thing
            if (type.equals("hand") || type.equals("item")) {
                // define all variables
                String amount = "";
                String material = null;
                int data = 0;
                Map<String, Integer> enchants = new HashMap<>();
                List<String> lore = new ArrayList<>();
                String name = null;
                String inverted = "";
                // for every part check if it has some data and place it in
                // variables
                for (String part : parts) {
                    if (part.contains("type:")) {
                        material = part.substring(5);
                    } else if (part.contains("data:")) {
                        data = Byte.valueOf(part.substring(5));
                    } else if (part.contains("enchants:")) {
                        for (String enchant : part.substring(9).split(",")) {
                            enchants.put(enchant.split(":")[0],
                                    Integer.decode(enchant.split(":")[1]));
                        }
                    } else if (part.contains("lore:")) {
                        for (String loreLine : part.substring(5).split(";")) {
                            lore.add(loreLine.replaceAll("_", " "));
                        }
                    } else if (part.contains("name:")) {
                        name = part.substring(5).replaceAll("_", " ");
                    } else if (part.contains("amount:")) {
                        amount = part;
                    } else if (part.equalsIgnoreCase("--inverted")) {
                        inverted = part;
                    }
                }
                // create an item
                String newItemID = null;
                QuestItem item = new QuestItem(material, data, enchants, name, lore);
                boolean contains = false;
                for (String itemKey : items.keySet()) {
                    if (items.get(itemKey).equalsToItem(item)) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    // generate new name for an item
                    newItemID = "item" + number;
                    number++;
                    items.put(newItemID, item);
                } else {
                    for (String itemName : items.keySet()) {
                        if (items.get(itemName).equalsToItem(item)) {
                            newItemID = itemName;
                        }
                    }
                }
                ConfigHandler
                        .getConfigs()
                        .get("conditions")
                        .getConfig()
                        .set(key,
                                (type + " item:" + newItemID + " " + amount + " " + inverted)
                                        .trim());
                Debug.broadcast("Extracted " + newItemID + " from " + key + " condition!");
            }
        }
        // generated all items, now place them in items.yml
        for (String key : items.keySet()) {
            QuestItem item = items.get(key);
            String instruction = item.getMaterial().toUpperCase() + " data:" + item.getData();
            if (item.getName() != null) {
                instruction = instruction + " name:" + item.getName().replace(" ", "_");
            }
            if (!item.getLore().isEmpty()) {
                StringBuilder lore = new StringBuilder();
                for (String line : item.getLore()) {
                    lore.append(line + ";");
                }
                instruction = instruction + " lore:"
                    + (lore.substring(0, lore.length() - 1).replace(" ", "_"));
            }
            if (!item.getEnchants().isEmpty()) {
                StringBuilder enchants = new StringBuilder();
                for (String enchant : item.getEnchants().keySet()) {
                    enchants.append(enchant.toUpperCase() + ":" + item.getEnchants().get(enchant)
                        + ",");
                }
                instruction = instruction + " enchants:"
                    + enchants.substring(0, enchants.length() - 1);
            }
            ConfigHandler.getConfigs().get("items").getConfig().set(key, instruction);
        }
        ConfigHandler.getConfigs().get("items").saveConfig();
        ConfigHandler.getConfigs().get("events").saveConfig();
        ConfigHandler.getConfigs().get("conditions").saveConfig();
        Debug.broadcast("All extracted items has been successfully saved to items.yml!");
        // end of updating to 1.4
        instance.getConfig().set("version", "1.4");
        Debug.broadcast("Conversion to v1.4 finished.");
        updateTo1_4_1();
    }

    private void updateTo1_3() {
        Debug.broadcast("Started converting configuration files from unknown version to v1.3!");
        // add conversion options
        Debug.broadcast("Using Names by for safety. If you run UUID compatible server and "
            + "want to use UUID, change it manually in the config file and reload the plugin.");
        config.set("uuid", "false");
        // this will alert the plugin that the conversion should be done if UUID
        // is
        // set to true
        config.set("convert", "true");
        // add metrics if they are not set yet
        if (!config.isSet("metrics")) {
            Debug.broadcast("Added metrics option.");
            config.set("metrics", "true");
        }
        // add stop to conversation if not done already
        Debug.broadcast("Adding stop nodes to conversations...");
        int count = 0;
        ConfigAccessor conversations = ConfigHandler.getConfigs().get("conversations");
        Set<String> convNodes = conversations.getConfig().getKeys(false);
        for (String convNode : convNodes) {
            if (!conversations.getConfig().isSet(convNode + ".stop")) {
                conversations.getConfig().set(convNode + ".stop", "false");
                count++;
            }
        }
        conversations.saveConfig();
        Debug.broadcast("Done, modified " + count + " conversations!");
        // end of updating to 1.3
        config.set("version", "1.3");
        Debug.broadcast("Conversion to v1.3 finished.");
        updateTo1_4();
    }

    /**
     * Updates language file, so it contains all required messages.
     */
    private void updateLanguages() {
        // add new languages
        boolean isUpdated = false;
        ConfigAccessor messages = ConfigHandler.getConfigs().get("messages");
        // check every language if it exists
        for (String path : messages.getConfig().getDefaultSection().getKeys(false)) {
            if (messages.getConfig().isSet(path)) {
                // if it exists check every message if it exists
                for (String messageNode : messages.getConfig().getDefaults()
                        .getConfigurationSection(path).getKeys(false)) {
                    if (!messages.getConfig().isSet(path + "." + messageNode)) {
                        // if message doesn't exist then add it from defaults
                        messages.getConfig().set(path + "." + messageNode,
                                messages.getConfig().getDefaults().get(path + "." + messageNode));
                        isUpdated = true;
                    }
                }
            } else {
                // if language does not exist then add every message to it
                for (String messageNode : messages.getConfig().getDefaults()
                        .getConfigurationSection(path).getKeys(false)) {
                    messages.getConfig().set(path + "." + messageNode,
                            messages.getConfig().getDefaults().get(path + "." + messageNode));
                    isUpdated = true;
                }
            }
        }
        // if we updated config filse then print the message
        if (isUpdated) {
            messages.saveConfig();
            Debug.broadcast("Updated language files!");
        }
    }

    /**
     * As the name says, converts all names to UUID in database
     */
    @SuppressWarnings("deprecation")
    private void convertNamesToUUID() {
        Debug.broadcast("Converting names to UUID...");
        instance.getDB().openConnection();
        // loop all tables
        HashMap<String, String> list = new HashMap<>();
        String[] tables = new String[] { "OBJECTIVES", "TAGS", "POINTS", "JOURNAL" };
        for (String table : tables) {
            ResultSet res = instance.getDB().querySQL(QueryType.valueOf("SELECT_PLAYERS_" + table),
                    new String[] {});
            try {
                while (res.next()) {
                    // and extract from them list of player names
                    String playerID = res.getString("playerID");
                    if (!list.containsKey(playerID)) {
                        list.put(playerID, Bukkit.getOfflinePlayer(playerID).getUniqueId()
                                .toString());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // convert all player names in all tables
        for (String table : tables) {
            for (String playerID : list.keySet()) {
                instance.getDB().updateSQL(UpdateType.valueOf("UPDATE_PLAYERS_" + table),
                        new String[] { list.get(playerID), playerID });
            }
        }
        instance.getDB().closeConnection();
        Debug.broadcast("Names conversion finished!");
    }

    /**
     * Adds the changelog file.
     */
    private void addChangelog() {
        try {
            File changelog = new File(BetonQuest.getInstance().getDataFolder(), "changelog.txt");
            if (changelog.exists()) {
                changelog.delete();
            }
            Files.copy(BetonQuest.getInstance().getResource("changelog.txt"), changelog.toPath());
            Debug.broadcast("Changelog added!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
