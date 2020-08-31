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

import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.ConfigAccessor.AccessorType;
import pl.betoncraft.betonquest.database.Connector;
import pl.betoncraft.betonquest.database.Connector.QueryType;
import pl.betoncraft.betonquest.database.Connector.UpdateType;
import pl.betoncraft.betonquest.database.Database;
import pl.betoncraft.betonquest.database.Saver.Record;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

/**
 * Updates configuration files to the newest version.
 *
 * @author Jakub Sapalski
 */
public class ConfigUpdater {

    // abandon all hope, ye who enter here

    /**
     * Error which should be displayed to the player when something goes wrong
     */
    private final static String ERROR = "There was an error during updating process! Please "
            + "downgrade to the previous working version of the plugin and restore your "
            + "configuration from the backup. Don't forget to send this error to the developer"
            + ", so he can fix it! Sorry for inconvenience, here's the link:"
            + " <https://github.com/BetonQuest/BetonQuest/issues> and a cookie: <http://i.imgur.com/iR4UMH5.png>";
    /**
     * Destination version. At the end of the updating process this will be the
     * current version
     */
    private final String destination = "v62";
    /**
     * BetonQuest's instance
     */
    private BetonQuest instance = BetonQuest.getInstance();
    /**
     * Main configuration instance
     */
    private FileConfiguration config = instance.getConfig();
    /**
     * Deprecated ConfigHandler, used for updating older configuration files
     */
    private ConfigHandler configHandler;

    public ConfigUpdater() {
        final String version = BetonQuest.getInstance().getConfig().getString("version", null);
        LogUtils.getLogger().log(Level.FINE, "Initializing updater with version " + version + ", destination is " + destination);
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
        final File[] backupFiles = instance.getDataFolder().listFiles();
        if (backupFiles != null) {
            for (final File file : backupFiles) {
                if (file.getName().matches("^backup-.*\\.zip$")) {
                    file.renameTo(new File(file.getParentFile().getAbsolutePath() + File.separator + "backups"
                            + File.separator + file.getName()));
                    LogUtils.getLogger().log(Level.INFO, "File " + file.getName() + " moved to backup folder!");
                }
            }
        }
        if (version != null && version.equals(destination)) {
            LogUtils.getLogger().log(Level.INFO, "Configuration up to date!");
            return;
        } else {
            Utils.backup();
        }
        // instantiate old configuration handler
        configHandler = new ConfigHandler();
        // if the version is null the plugin is updated from pre-1.4 version
        // (which can be 1.0, 1.1 or 1.2)
        if (version == null || version.equals("1.4")) {
            updateTo1dot4dot1();
        } else if (version.equals("1.4.1")) {
            updateTo1dot4dot2();
        } else if (version.equals("1.4.2")) {
            updateTo1dot4dot3();
        } else if (version.equals("1.4.3")) {
            updateTo1dot5();
        } else if (version.equals("1.5")) {
            updateTo1dot5dot1();
        } else if (version.equals("1.5.1")) {
            updateTo1dot5dot2();
        } else if (version.equals("1.5.2")) {
            updateTo1dot5dot3();
        } else if (version.equals("1.5.3") || version.equals("1.5.4") || version.equals("1.6")) {
            updateTo1dot6();
        } else if (version.matches("^v\\d+$")) {
            performUpdate();
        } else {
            LogUtils.getLogger().log(Level.INFO, "Something is not right with configuration version. Consider fixing this.");
        }
    }

    /**
     * Performs full update in new updating system.
     */
    private void performUpdate() {
        // this is new, post-1.5.3 updating system, where config versions
        // are numbered separately from plugin's releases
        LogUtils.getLogger().log(Level.INFO, "Updating configuration to version " + destination);
        update();
        updateLanguages();
        instance.saveConfig();
        // reload configuration file to apply all possible changes
        new Config(false);
        LogUtils.getLogger().log(Level.INFO, "Updating done!");
        addChangelog();
    }

    /**
     * Invokes method that updates config from current version to the next. It
     * repeats itself until everything is converted.
     */
    private void update() {
        final String configVersion = config.getString("version", null);
        // if the version is the same as destination, updating process is
        // finished
        if (configVersion == null || configVersion.equals(destination)) {
            return;
        }
        final String version = configVersion.toUpperCase();
        try {
            // reload existing configuration
            new Config(false);
            config = instance.getConfig();
            // call the right updating method
            final Method method = this.getClass().getDeclaredMethod("updateFrom" + version);
            method.setAccessible(true);
            LogUtils.getLogger().log(Level.FINE, "Starting update from " + version + "!");
            method.invoke(this);
            LogUtils.getLogger().log(Level.FINE, "Update to " + config.getString("version") + " done!");
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            LogUtils.getLogger().log(Level.WARNING, "Cannot update configuration. Maybe it comes from an even newer version and you did a downgrade?");
            LogUtils.logThrowable(e);
            // return, so it does not fall into an infinite loop
            return;
        }
        // update again until destination is reached
        update();
    }

    private void updateFromV61() {
        LogUtils.getLogger().log(Level.INFO, "Renaming 'xp' event to 'experience'");
        LogUtils.getLogger().log(Level.INFO, "Adding 'level' argument to 'experience' condition and objective");
        for (final ConfigPackage pack : Config.getPackages().values()) {
            LogUtils.getLogger().log(Level.FINE, "  Replacing in '" + pack.getName() + "' package");
            for (final String key : pack.getEvents().getConfig().getKeys(false)) {
                final String instruction = pack.getEvents().getConfig().getString(key);
                if (instruction.startsWith("xp ")) {
                    LogUtils.getLogger().log(Level.FINE, "    Replacing xp in '" + key + "' event");
                    pack.getEvents().getConfig().set(key, instruction.replaceFirst("xp ", "experience "));
                }
            }
            for (final String key : pack.getConditions().getConfig().getKeys(false)) {
                final String instruction = pack.getConditions().getConfig().getString(key);
                if (instruction.startsWith("experience ")) {
                    LogUtils.getLogger().log(Level.FINE, "    Adding level argument in '" + key + "' condition");
                    pack.getConditions().getConfig().set(key, instruction + " level");
                }
            }
            for (final String key : pack.getObjectives().getConfig().getKeys(false)) {
                final String instruction = pack.getObjectives().getConfig().getString(key);
                if (instruction.startsWith("experience ")) {
                    LogUtils.getLogger().log(Level.FINE, "    Adding level argument in '" + key + "' objectives");
                    pack.getObjectives().getConfig().set(key, instruction + " level");
                }
            }
            pack.getEvents().saveConfig();
            pack.getConditions().saveConfig();
            pack.getObjectives().saveConfig();
        }
        LogUtils.getLogger().log(Level.INFO, "Successfully renamed 'xp' event to 'experience'");
        LogUtils.getLogger().log(Level.INFO, "Successfully added 'level' argument to 'experience' conditions and objectives");
        config.set("version", "v62");
        instance.saveConfig();
    }

    private void updateFromV60() {
        LogUtils.getLogger().log(Level.INFO, "Renaming 'monsters' condition to 'entities'");
        for (final ConfigPackage pack : Config.getPackages().values()) {
            LogUtils.getLogger().log(Level.FINE, "  Replacing in '" + pack.getName() + "' package");
            for (final String key : pack.getConditions().getConfig().getKeys(false)) {
                final String instruction = pack.getConditions().getConfig().getString(key);
                if (instruction.startsWith("monsters ")) {
                    LogUtils.getLogger().log(Level.FINE, "    Replacing monsters in '" + key + "' condition");
                    pack.getConditions().getConfig().set(key, instruction.replaceFirst("monsters ", "entities "));
                }
            }
            pack.getConditions().saveConfig();
        }
        LogUtils.getLogger().log(Level.INFO, "Successfully renamed 'monsters' condition to 'entities'");
        config.set("version", "v61");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV59() {
        LogUtils.getLogger().log(Level.INFO, "Rename Objective potion to brew");
        for (final ConfigPackage pack : Config.getPackages().values()) {
            LogUtils.getLogger().log(Level.FINE, "  Replacing in '" + pack.getName() + "' package");
            for (final String key : pack.getObjectives().getConfig().getKeys(false)) {
                final String instruction = pack.getObjectives().getConfig().getString(key);
                if (instruction.startsWith("potion ")) {
                    LogUtils.getLogger().log(Level.FINE, "    Replacing potion in '" + key + "' objective");
                    pack.getObjectives().getConfig().set(key, instruction.replaceFirst("potion ", "brew "));
                }
            }
            pack.getObjectives().saveConfig();
        }
        LogUtils.getLogger().log(Level.INFO, "Successfully renamed Objective potion to brew");
        config.set("version", "v60");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV58() {
        config.set("config.journal.chars_per_line", 19);
        config.set("config.journal.lines_per_page", 13);
        LogUtils.getLogger().log(Level.INFO, "Added config options chars_per_line and lines_per_page for the journal!");
        config.set("version", "v59");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV57() {
        if (config.contains("default_conversation_IO") && config.getString("default_conversation_IO").equalsIgnoreCase("chest")) {
            LogUtils.getLogger().log(Level.INFO, "Renamed default ConversationIO to 'combined'");
            config.set("default_conversation_IO", "combined");
        }
        for (final ConfigPackage pack : Config.getPackages().values()) {
            for (final String convName : pack.getConversationNames()) {
                final String convIO = pack.getRawString("conversations." + convName + ".conversationIO");
                if (convIO == null) {
                    continue;
                }
                if (!convIO.equalsIgnoreCase("chest")) {
                    continue;
                }
                LogUtils.getLogger().log(Level.INFO, "Renamed conversationIO in conversation " + pack.getName() + "." + convName + " to 'combined'");
                pack.setString("conversations." + convName + ".conversationIO", "combined");
            }
        }
        config.set("version", "v58");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV56() {
        config.set("citizens_npcs_by_name", "false");
        LogUtils.getLogger().log(Level.INFO, "Added option to allow identifying citizens npcs by name");
        config.set("version", "v57");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV55() {
        config.set("hook.brewery", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with Brewery");
        config.set("version", "v56");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV54() {
        config.set("hook.protocollib", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with ProtocolLib");
        config.set("version", "v55");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV53() {
        final ConfigurationSection section = config.getConfigurationSection("effectlib_npc_effect");
        if (section != null) {
            final ConfigAccessor custom = Config.getDefaultPackage().getCustom();
            final Configuration config = custom.getConfig();
            config.set("npc_effects.default", section);
            config.set("npc_effects.default.interval", config.getInt("npc_effects.default.delay") * 20);
            config.set("npc_effects.default.delay", null);
            custom.saveConfig();
        }
        config.set("effectlib_npc_effect", null);
        LogUtils.getLogger().log(Level.INFO, "Moved NPC effects to custom.yml");
        config.set("version", "v54");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV52() {
        config.set("hook.bountifulapi", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with BountifulAPI");
        config.set("version", "v53");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV51() {
        config.set("hook.betonlangapi", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with BetonLangAPI");
        config.set("version", "v52");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV50() {
        LogUtils.getLogger().log(Level.FINE, "Moving custom settings from main.yml to custom.yml");
        final List<String> coreSettings = Arrays.asList("npcs", "variables", "static", "global_locations",
                "cancel", "journal_main_page", "compass", "enabled");
        for (final ConfigPackage pack : Config.getPackages().values()) {
            LogUtils.getLogger().log(Level.FINE, "  Moving custom settings in package " + pack.getName());
            final ConfigAccessor main = pack.getMain();
            final ConfigAccessor custom = pack.getCustom();
            main:
            for (final String key : main.getConfig().getKeys(false)) {
                for (final String coreSetting : coreSettings) {
                    if (key.equals(coreSetting)) {
                        LogUtils.getLogger().log(Level.FINE, "    Key " + key + " is core setting, skipping");
                        continue main;
                    }
                }
                LogUtils.getLogger().log(Level.FINE, "    Key " + key + " is custom, moving it");
                custom.getConfig().set(key, main.getConfig().get(key));
                main.getConfig().set(key, null);
            }
            main.saveConfig();
            custom.saveConfig();
        }
        LogUtils.getLogger().log(Level.INFO, "Moved custom settings from main.yml to custom.yml file");
        config.set("version", "v51");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV49() {
        final Set<String> enabledPackages = new HashSet<>(config.getStringList("packages"));
        LogUtils.getLogger().log(Level.FINE, "Disabling packages not listed in the config");
        for (final Iterator<ConfigPackage> iterator = Config.getPackages().values().iterator(); iterator.hasNext(); ) {
            final ConfigPackage pack = iterator.next();
            LogUtils.getLogger().log(Level.FINE, "  Looking at package " + pack.getName());
            if (enabledPackages.contains(pack.getName())) {
                pack.getMain().getConfig().set("enabled", true);
                pack.getMain().saveConfig();
            } else {
                LogUtils.getLogger().log(Level.FINE, "    Package is not enabled, removing it from the list.");
                pack.getMain().getConfig().set("enabled", false);
                pack.getMain().saveConfig();
                iterator.remove();
            }
        }
        LogUtils.getLogger().log(Level.FINE, "All packages enabled/disabled, removing 'packages' section from config");
        config.set("packages", null);
        LogUtils.getLogger().log(Level.INFO, "Moved package enabling from config to main files");
        config.set("version", "v50");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV48() {
        for (final ConfigPackage pack : Config.getPackages().values()) {
            final String packName = pack.getName();
            final List<ConfigAccessor> sections = new ArrayList<>();
            // the idea is to get index of location argument for every type
            // and use a method to replace last semicolon with a space, because
            // all range arguments are right next to location arguments
            sections.add(pack.getConditions());
            sections.add(pack.getEvents());
            sections.add(pack.getObjectives());
            for (final ConfigAccessor acc : sections) {
                final AccessorType type = acc.getType();
                final ConfigurationSection sec = acc.getConfig();
                for (final String key : sec.getKeys(false)) {
                    final String value = sec.getString(key);
                    final int indexOf = value.indexOf(' ');
                    if (indexOf < 0) {
                        continue;
                    }
                    final String object = value.substring(0, indexOf).toLowerCase();
                    int index = -1;
                    switch (type) {
                        case CONDITIONS:
                            switch (object) {
                                case "location":
                                    index = 1;
                                    break;
                                case "monsters":
                                    index = 2;
                                    break;
                            }
                            break;
                        case EVENTS:
                            switch (object) {
                                case "clear":
                                    index = 2;
                                    break;
                            }
                            break;
                        case OBJECTIVES:
                            switch (object) {
                                case "action":
                                    // action objective uses optional argument, so convert it manually
                                    final String[] parts = value.split(" ");
                                    String loc = null;
                                    for (final String part : parts) {
                                        if (part.startsWith("loc:")) {
                                            loc = part;
                                            break;
                                        }
                                    }
                                    if (loc != null) {
                                        final int lastIndexOf = loc.lastIndexOf(';');
                                        if (lastIndexOf < 0 || lastIndexOf >= loc.length() - 1) {
                                            continue;
                                        }
                                        final String front = loc.substring(0, lastIndexOf);
                                        final String back = loc.substring(lastIndexOf + 1);
                                        final String newLoc = front + " range:" + back;
                                        sec.set(key, value.replace(loc, newLoc));
                                    }
                                    break;
                                case "arrow":
                                    index = 1;
                                    break;
                                case "location":
                                    index = 1;
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                    if (index >= 0) {
                        sec.set(key, semicolonToSpace(value, index));
                    }
                }
                acc.saveConfig();
            }
        }
        LogUtils.getLogger().log(Level.INFO, "Converted additional location arguments to the new format");
        config.set("version", "v49");
        instance.saveConfig();
    }

    private String semicolonToSpace(final String string, final int argument) {
        if (string == null) {
            return null;
        }
        final String[] parts = string.split(" ");
        if (parts.length <= argument) {
            return null;
        }
        final String original = parts[argument];
        final int lastSemicolon = original.lastIndexOf(';');
        if (lastSemicolon < 0) {
            return null;
        }
        final char[] chars = original.toCharArray();
        chars[lastSemicolon] = ' ';
        final String replaced = new String(chars);
        return string.replace(original, replaced);
    }

    @SuppressWarnings("unused")
    private void updateFromV47() {
        config.set("quest_items_unbreakable", "true");
        LogUtils.getLogger().log(Level.INFO, "Added option to disable quest item unbreakability");
        config.set("version", "v48");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV46() {
        config.set("journal.full_main_page", "false");
        LogUtils.getLogger().log(Level.INFO, "Added 'full_main_page' option to config");
        config.set("version", "v47");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV45() {
        config.set("hook.legendquest", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with LegendQuest");
        config.set("hook.worldedit", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with WorldEdit");
        config.set("version", "v46");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV44() {
        try {
            LogUtils.getLogger().log(Level.FINE, "Translating items in 'potion' objectives");
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                LogUtils.getLogger().log(Level.FINE, "  Handling " + packName + " package");
                final FileConfiguration objectives = pack.getObjectives().getConfig();
                final FileConfiguration items = pack.getItems().getConfig();
                for (final String key : objectives.getKeys(false)) {
                    final String instruction = objectives.getString(key);
                    if (!instruction.startsWith("potion ")) {
                        continue;
                    }
                    LogUtils.getLogger().log(Level.FINE, "    Found potion objective: '" + instruction + "'");
                    final String[] parts = instruction.split(" ");
                    if (parts.length < 2) {
                        LogUtils.getLogger().log(Level.FINE, "    It's incorrect.");
                        continue;
                    }
                    final int data;
                    try {
                        data = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        LogUtils.getLogger().log(Level.WARNING, "    It's incorrect");
                        LogUtils.logThrowable(e);
                        continue;
                    }
                    ItemStack itemStack = new QuestItem("potion data:" + data).generate(1);
                    {
                        // it doesn't work without actually spawning the item in-game...
                        final World world = Bukkit.getWorlds().get(0);
                        final Location loc = new Location(world, 0, 254, 0);
                        final Item item = world.dropItem(loc, itemStack);
                        itemStack = item.getItemStack();
                        item.remove();
                    }
                    final String updatedInstruction = QuestItem.itemToString(itemStack);
                    LogUtils.getLogger().log(Level.FINE, "    Potion instruction: '" + updatedInstruction + "'");
                    String item = null;
                    for (final String itemKey : items.getKeys(false)) {
                        if (items.getString(itemKey).equals(updatedInstruction)) {
                            item = itemKey;
                        }
                    }
                    if (item == null) {
                        if (items.contains("potion")) {
                            int index = 2;
                            while (items.contains("potion" + index)) {
                                index++;
                            }
                            item = "potion" + index;
                        } else {
                            item = "potion";
                        }
                    }
                    LogUtils.getLogger().log(Level.FINE, "    The item with this instruction has key " + item);
                    items.set(item, updatedInstruction);
                    objectives.set(key, instruction.replace(String.valueOf(data), item));
                }
                pack.getItems().saveConfig();
                pack.getObjectives().saveConfig();
            }
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Translated items in 'potion' objective");
        config.set("display_chat_after_conversation", "false");
        LogUtils.getLogger().log(Level.INFO, "Added an option to display chat messages after the conversation");
        config.set("version", "v45");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV43() {
        try {
            LogUtils.getLogger().log(Level.FINE, "Translating potion instructions");

            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                LogUtils.getLogger().log(Level.FINE, "  Handling " + packName + " package");
                final FileConfiguration items = pack.getItems().getConfig();
                for (final String key : items.getKeys(false)) {
                    final String instruction = items.getString(key);
                    if (!instruction.toLowerCase().startsWith("potion ") && !instruction.startsWith("splash_potion ")) {
                        continue;
                    }
                    LogUtils.getLogger().log(Level.FINE, "    Found " + key + " potion with instruction '" + instruction + "'");
                    try {
                        final QuestItem questItem = new QuestItem(instruction);
                        ItemStack itemStack = questItem.generate(1);
                        {
                            // it doesn't work without actually spawning the item in-game...
                            final World world = Bukkit.getWorlds().get(0);
                            final Location loc = new Location(world, 0, 254, 0);
                            final Item item = world.dropItem(loc, itemStack);
                            itemStack = item.getItemStack();
                            item.remove();
                            // lol
                        }
                        final String updatedInstruction = QuestItem.itemToString(itemStack);
                        LogUtils.getLogger().log(Level.FINE, "    New instruction: '" + updatedInstruction + "'");
                        items.set(key, updatedInstruction);
                    } catch (InstructionParseException e) {
                        LogUtils.getLogger().log(Level.WARNING, "Item " + packName + "." + key + " was incorrect, skipping.");
                        LogUtils.logThrowable(e);
                    }
                }
                pack.getItems().saveConfig();
            }
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Translated potions to a new format");
        config.set("hook.racesandclasses", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with RacesAndClasses");
        config.set("version", "v44");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV42() {
        config.set("hook.holographicdisplays", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with HolographicDisplays");
        config.set("version", "v43");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV41() {
        try {
            // change raw material names in craft objectives to items from items.yml
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                final ConfigAccessor objectives = pack.getObjectives();
                final ConfigAccessor items = pack.getItems();
                final ArrayList<String> materials = new ArrayList<>();
                // get a list of materials and their data values
                for (final String key : objectives.getConfig().getKeys(false)) {
                    final String objective = objectives.getConfig().getString(key);
                    if (objective.startsWith("craft ")) {
                        final String[] parts = objective.split(" ");
                        if (parts.length > 1) {
                            materials.add(parts[1]);
                        }
                    }
                }
                // translate materials to item instructions
                final ArrayList<String> itemInstructions = new ArrayList<>();
                for (final String material : materials) {
                    if (material.contains(":")) {
                        final String[] parts = material.split(":");
                        final String materialName = parts[0];
                        final String data = parts[1];
                        itemInstructions.add(materialName + " data:" + data);
                    } else {
                        itemInstructions.add(material);
                    }
                }
                // find items with the same instruction and store them in map (material, itemID)
                final HashMap<String, String> itemIDs = new HashMap<>();
                for (int i = 0; i < materials.size(); i++) {
                    final String material = materials.get(i);
                    final String itemInstruction = itemInstructions.get(i);
                    String itemID = null;
                    // look for existing items
                    for (final String key : items.getConfig().getKeys(false)) {
                        if (items.getConfig().getString(key).equalsIgnoreCase(itemInstruction)) {
                            itemID = key;
                            break;
                        }
                    }
                    // if there are no such items, create them
                    if (itemID == null) {
                        final String materialName = material.contains(":") ? material.split(":")[0] : material;
                        if (items.getConfig().contains(materialName)) {
                            int index = 2;
                            while (items.getConfig().contains(materialName + index)) {
                                index++;
                            }
                            items.getConfig().set(materialName + index, itemInstruction);
                            itemID = materialName + index;
                        } else {
                            items.getConfig().set(materialName, itemInstruction);
                            itemID = materialName;
                        }
                    }
                    itemIDs.put(material, itemID);
                }
                items.saveConfig();
                // replace materials in craft objectives
                for (final String key : objectives.getConfig().getKeys(false)) {
                    final String objective = objectives.getConfig().getString(key);
                    if (objective.startsWith("craft ")) {
                        final String[] parts = objective.split(" ");
                        if (parts.length > 1) {
                            objectives.getConfig().set(key, objective.replace(parts[1], itemIDs.get(parts[1])));
                        }
                    }
                }
                objectives.saveConfig();
            }
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Changed 'craft' objective to use items.yml");
        config.set("version", "v42");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV40() {
        config.set("hook.placeholderapi", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with PlaceholderAPI");
        config.set("version", "v41");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV39() {
        config.set("hook.shopkeepers", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with Shopkeepers");
        config.set("version", "v40");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV38() {
        final boolean enabled = config.getString("autoupdate").equalsIgnoreCase("true");
        config.set("autoupdate", null);
        config.set("update.enabled", enabled);
        config.set("update.download_bugfixes", true);
        config.set("update.notify_new_release", true);
        LogUtils.getLogger().log(Level.INFO, "Modified autoupdater");
        config.set("version", "v39");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV37() {
        try {
            LogUtils.getLogger().log(Level.FINE, "Updating global location tags in the database");
            LogUtils.getLogger().log(Level.FINE, "    oiienwfiu wenfiu nweiufn weiunf iuwenf iuw");
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                final String locList = pack.getMain().getConfig().getString("global_locations");
                LogUtils.getLogger().log(Level.FINE, "  Handling package '" + packName + "': " + locList);
                if (locList == null) {
                    continue;
                }
                for (final String locName : locList.split(",")) {
                    LogUtils.getLogger().log(Level.FINE, "Adding '" + packName + "' prefix to '" + locName + "' global location tags.");
                    instance.getSaver().add(new Record(UpdateType.RENAME_ALL_TAGS,
                            new String[]{packName + ".global_" + locName, "global_" + locName}));
                }
            }
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Updated tags of global locations with package names");
        config.set("version", "v38");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV36() {
        config.set("hook.quests", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with Quests");
        config.set("version", "v37");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV35() {
        config.set("hook.denizen", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with Denizen");
        config.set("hook.skillapi", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with SkillAPI");
        config.set("version", "v36");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV34() {
        config.set("hook.magic", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with Magic");
        config.set("version", "v35");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV33() {
        config.set("hook.heroes", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with Heroes");
        config.set("version", "v34");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV32() {
        config.set("hook.playerpoints", "true");
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with PlayerPoints");
        config.set("version", "v33");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV31() {
        config.set("hook.effectlib", "true");
        config.set("effectlib_npc_effect.class", "VortexEffect");
        config.set("effectlib_npc_effect.iterations", 20);
        config.set("effectlib_npc_effect.particle", "crit_magic");
        config.set("effectlib_npc_effect.helixes", 3);
        config.set("effectlib_npc_effect.circles", 1);
        config.set("effectlib_npc_effect.grow", 0.1);
        config.set("effectlib_npc_effect.radius", 0.5);
        config.set("effectlib_npc_effect.delay", 5);
        LogUtils.getLogger().log(Level.INFO, "Added compatibility with EffectLib");
        config.set("version", "v32");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV30() {
        try {
            LogUtils.getLogger().log(Level.FINE, "Converting cancelers to a new format");
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                LogUtils.getLogger().log(Level.FINE, "Searching " + packName + " package");
                final ConfigurationSection section = pack.getMain().getConfig().getConfigurationSection("cancel");
                if (section == null) {
                    continue;
                }
                for (final String key : section.getKeys(false)) {
                    final String instruction = section.getString(key);
                    LogUtils.getLogger().log(Level.FINE, "  Converting " + key + " canceler: " + instruction);
                    final String[] parts = instruction.split(" ");
                    final HashMap<String, String> names = new HashMap<>();
                    String events = null, conditions = null, tags = null, points = null, objectives = null,
                            journal = null, loc = null;
                    for (final String part : parts) {
                        LogUtils.getLogger().log(Level.FINE, "    Checking part " + part);
                        if (part.startsWith("name:")) {
                            LogUtils.getLogger().log(Level.FINE, "    Found general name: " + part.substring(5));
                            names.put(Config.getLanguage(), part.substring(5));
                        } else if (part.startsWith("name_")) {
                            final int colonIndex = part.indexOf(':');
                            if (colonIndex < 0) {
                                continue;
                            }
                            final String lang = part.substring(5, colonIndex);
                            LogUtils.getLogger().log(Level.FINE, "    Found " + lang + " name: " + part.substring(colonIndex));
                            names.put(lang, part.substring(colonIndex));
                        } else if (part.startsWith("events:")) {
                            LogUtils.getLogger().log(Level.FINE, "    Found events: " + part.substring(7));
                            events = part.substring(7);
                        } else if (part.startsWith("conditions:")) {
                            LogUtils.getLogger().log(Level.FINE, "    Found conditions: " + part.substring(11));
                            conditions = part.substring(11);
                        } else if (part.startsWith("tags:")) {
                            LogUtils.getLogger().log(Level.FINE, "    Found tags: " + part.substring(5));
                            tags = part.substring(5);
                        } else if (part.startsWith("points:")) {
                            LogUtils.getLogger().log(Level.FINE, "    Found points: " + part.substring(7));
                            points = part.substring(7);
                        } else if (part.startsWith("objectives:")) {
                            LogUtils.getLogger().log(Level.FINE, "    Found objectives: " + part.substring(11));
                            objectives = part.substring(11);
                        } else if (part.startsWith("journal:")) {
                            LogUtils.getLogger().log(Level.FINE, "    Found journal entries: " + part.substring(8));
                            journal = part.substring(8);
                        } else if (part.startsWith("loc:")) {
                            LogUtils.getLogger().log(Level.FINE, "    Found location: " + part.substring(4));
                            loc = part.substring(4);
                        }
                    }
                    LogUtils.getLogger().log(Level.FINE, "  - Setting the values");
                    section.set(key, null);
                    for (final String lang : names.keySet()) {
                        section.set(key + ".name." + lang, names.get(lang));
                    }
                    section.set(key + ".events", events);
                    section.set(key + ".conditions", conditions);
                    section.set(key + ".tags", tags);
                    section.set(key + ".points", points);
                    section.set(key + ".objectives", objectives);
                    section.set(key + ".journal", journal);
                    section.set(key + ".loc", loc);
                    pack.getMain().saveConfig();
                }
            }
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Made quest cancelers more convenient to define");
        config.set("version", "v31");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV29() {
        try {
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                final ConfigurationSection section = pack.getMain().getConfig().getConfigurationSection("variables");
                for (final String key : section.getKeys(true)) {
                    final String variable = section.getString(key);
                    if (variable.matches(
                            "^\\$[a-zA-Z0-9]+\\$->\\(\\-?\\d+\\.?\\d*,\\-?\\d+\\.?\\d*,\\-?\\d+\\.?\\d*\\)$")) {
                        section.set(key, variable.replace(',', ';'));
                    }
                }
                pack.getMain().saveConfig();
            }
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Changed commas to semicolons in vector variables");
        config.set("version", "v30");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV28() {
        String globalName = "global";
        try {
            final HashMap<String, ArrayList<String>> tags = new HashMap<>();
            final HashMap<String, ArrayList<String>> points = new HashMap<>();
            // this will ensure that there is no "global" package already
            // defined
            int index = 1;
            while (Config.getPackages().get(globalName) != null) {
                index++;
                globalName = "global-" + index;
            }
            LogUtils.getLogger().log(Level.FINE, "Global package will be called '" + globalName + "'");
            // create lists for tags/points that are duplicated across multiple
            // packages
            // these will be "global", the rest will be converted to their local
            // packages
            final ArrayList<String> globalTagList = new ArrayList<>();
            final ArrayList<String> globalPointList = new ArrayList<>();
            tags.put(globalName, globalTagList);
            points.put(globalName, globalPointList);
            final ArrayList<ConfigPackage> packages = new ArrayList<>();
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                LogUtils.getLogger().log(Level.FINE, "  Checking '" + packName + "' package");
                // skip packages that already use prefixes
                final String prefixOption = pack.getString("main.tag_point_prefix");
                if (prefixOption != null && prefixOption.equalsIgnoreCase("true")) {
                    continue;
                }
                LogUtils.getLogger().log(Level.FINE, "  - It's outdated, extracting tags and points from events");
                packages.add(pack);
                // create array lists
                final ArrayList<String> tagList = new ArrayList<>();
                final ArrayList<String> pointList = new ArrayList<>();
                tags.put(packName, tagList);
                points.put(packName, pointList);
                // handle all tags/points in events
                for (final String key : pack.getEvents().getConfig().getKeys(false)) {
                    LogUtils.getLogger().log(Level.FINE, "    Checking '" + key + "' event");
                    final String rawInstruction = pack.getEvents().getConfig().getString(key);
                    final ArrayList<String> instructions = new ArrayList<>();
                    // run event also needs to be checked in case it contained
                    // any tags
                    if (rawInstruction.startsWith("run ")) {
                        LogUtils.getLogger().log(Level.FINE, "    - It's \"run\" event, extracting additional instructions");
                        // this part is copied from run event
                        final String[] parts = rawInstruction.substring(3).trim().split(" ");
                        StringBuilder builder = new StringBuilder();
                        for (final String part : parts) {
                            if (part.startsWith("^")) {
                                if (builder.length() != 0) {
                                    instructions.add(builder.toString().trim());
                                    builder = new StringBuilder();
                                }
                                builder.append(part.substring(1) + " ");
                            } else {
                                builder.append(part + " ");
                            }
                        }
                        instructions.add(builder.toString().trim());
                    } else {
                        // if it's not run event, add whole instruction string
                        instructions.add(rawInstruction);
                    }
                    // check every instruction that was specified
                    for (final String instruction : instructions) {
                        if (instruction.startsWith("tag ")) {
                            LogUtils.getLogger().log(Level.FINE, "      Found tag event, extracting tag");
                            final String[] parts = instruction.split(" ");
                            // check if it contains the tag, if not - continue
                            if (parts.length < 3) {
                                LogUtils.getLogger().log(Level.FINE, "      - Could not find tags");
                                continue;
                            }
                            // add tag to the list if it does not contain a
                            // package
                            for (final String tag : parts[2].split(",")) {
                                if (!tag.contains(".")) {
                                    tagList.add(tag);
                                }
                            }
                        } else if (instruction.startsWith("point ")) {
                            LogUtils.getLogger().log(Level.FINE, "      Found point event, extracting points");
                            final String[] parts = instruction.split(" ");
                            // check if the point has defined a category
                            if (parts.length < 2) {
                                LogUtils.getLogger().log(Level.FINE, "      - Could not find the category");
                                continue;
                            }
                            // add point to the list if it does not contain a
                            // package
                            if (!parts[1].contains(".")) {
                                pointList.add(parts[1]);
                            }
                        }
                    }
                    // done, all tags in events are extracted
                }
                LogUtils.getLogger().log(Level.FINE, "  All tags and points extracted from events, moving to conditions");
                // handle all tags/points in conditions
                for (final String key : pack.getConditions().getConfig().getKeys(false)) {
                    LogUtils.getLogger().log(Level.FINE, "    Checking '" + key + "' condition");
                    final String rawInstruction = pack.getConditions().getConfig().getString(key);
                    final ArrayList<String> instructions = new ArrayList<>();
                    // check condition also needs to be checked in case it
                    // contained any tags
                    if (rawInstruction.startsWith("check ")) {
                        LogUtils.getLogger().log(Level.FINE, "    - It's \"check\" condition, extracting additional instructions");
                        // this part is copied from run event
                        final String[] parts = rawInstruction.substring(5).trim().split(" ");
                        StringBuilder builder = new StringBuilder();
                        for (final String part : parts) {
                            if (part.startsWith("^")) {
                                if (builder.length() != 0) {
                                    instructions.add(builder.toString().trim());
                                    builder = new StringBuilder();
                                }
                                builder.append(part.substring(1) + " ");
                            } else {
                                builder.append(part + " ");
                            }
                        }
                        instructions.add(builder.toString().trim());
                    } else {
                        // if it's not check condition, add whole instruction
                        // string
                        instructions.add(rawInstruction);
                    }
                    // check every instruction that was specified
                    for (final String instruction : instructions) {
                        if (instruction.startsWith("tag ")) {
                            LogUtils.getLogger().log(Level.FINE, "      Found tag condition, extracting tag");
                            final String[] parts = instruction.split(" ");
                            // check if it contains the tag, if not - continue
                            if (parts.length < 2) {
                                LogUtils.getLogger().log(Level.FINE, "      - Could not find the tag");
                                continue;
                            }
                            // add tag to the list if it does not contain a
                            // package
                            if (!parts[1].contains(".")) {
                                tagList.add(parts[1]);
                            }
                        } else if (instruction.startsWith("point ")) {
                            LogUtils.getLogger().log(Level.FINE, "      Found point condition, extracting points");
                            final String[] parts = instruction.split(" ");
                            // check if the point has defined a category
                            if (parts.length < 2) {
                                LogUtils.getLogger().log(Level.FINE, "      - Could not find the category");
                                continue;
                            }
                            // add point to the list if it does not contain a
                            // package
                            if (!parts[1].contains(".")) {
                                pointList.add(parts[1]);
                            }
                        }
                    }
                    // done, all tags in conditions are extracted
                }
                LogUtils.getLogger().log(Level.FINE, "  All tags and points extracted from conditions");
                // done, events and conditions in package extracted
            }
            LogUtils.getLogger().log(Level.FINE, "All tags and points in all packages extracted, checking tags for duplicates");
            // find tags/points that are duplicated in package hashMaps,
            // put them to global package and remove from those packages
            // first tags in each package
            for (int j = 0; j < packages.size(); j++) {
                LogUtils.getLogger().log(Level.FINE, "  Checking list '" + packages.get(j).getName() + "'");
                // get a list
                final ArrayList<String> list = tags.get(packages.get(j).getName());
                // and for each element
                for (int k = 0; k < list.size(); k++) {
                    final String checked = list.get(k);
                    LogUtils.getLogger().log(Level.FINE, "    Checking tag '" + checked + "'");
                    // go to each next package
                    for (int l = j + 1; l < packages.size(); l++) {
                        final ArrayList<String> nextList = tags.get(packages.get(l).getName());
                        // and check if it contains that element
                        if (nextList.contains(checked)) {
                            LogUtils.getLogger().log(Level.FINE, "    - list '" + packages.get(l).getName() + "' contains this tag, removing");
                            nextList.remove(checked);
                            if (!globalTagList.contains(checked)) {
                                globalTagList.add(checked);
                                LogUtils.getLogger().log(Level.FINE, "      Tag was added to the global list");
                            }
                        }
                    }
                }
            }
            LogUtils.getLogger().log(Level.FINE, "List of global tags is filled, checking points");
            // now points in each package
            for (int j = 0; j < packages.size(); j++) {
                LogUtils.getLogger().log(Level.FINE, "  Checking list '" + packages.get(j).getName() + "'");
                // get a list
                final ArrayList<String> list = points.get(packages.get(j).getName());
                // and for each element
                for (int k = 0; k < list.size(); k++) {
                    final String checked = list.get(k);
                    LogUtils.getLogger().log(Level.FINE, "    Checking point '" + checked + "'");
                    // go to each next package
                    for (int l = j + 1; l < packages.size(); l++) {
                        final ArrayList<String> nextList = points.get(packages.get(l).getName());
                        // and check if it contains that element
                        if (nextList.contains(checked)) {
                            LogUtils.getLogger().log(Level.FINE, "    - list '" + packages.get(l).getName() + "' contains this point, removing");
                            nextList.remove(checked);
                            if (!globalPointList.contains(checked)) {
                                globalPointList.add(checked);
                                LogUtils.getLogger().log(Level.FINE, "      Point was added to the global list");
                            }
                        }
                    }
                }
            }
            LogUtils.getLogger().log(Level.FINE, "List of global points is filled, now adding \"global\" prefix in configuration files");
            // done, global lists are filled
            for (final ConfigPackage pack : packages) {
                LogUtils.getLogger().log(Level.FINE, "  Replacing in '" + pack.getName() + "' package");
                // update tags/points in events
                for (final String key : pack.getEvents().getConfig().getKeys(false)) {
                    LogUtils.getLogger().log(Level.FINE, "    Replacing tags/points in '" + key + "' event");
                    final String instruction = pack.getEvents().getConfig().getString(key);
                    if (instruction.startsWith("tag ")) {
                        LogUtils.getLogger().log(Level.FINE, "      Found tag event, replacing tags");
                        final String[] parts = instruction.split(" ");
                        // check if it contains the tag, if not - continue
                        if (parts.length < 3) {
                            LogUtils.getLogger().log(Level.FINE, "      - Could not find tags");
                            continue;
                        }
                        // replace tags
                        final String[] localTags = parts[2].split(",");
                        for (int j = 0; j < localTags.length; j++) {
                            if (globalTagList.contains(localTags[j])) {
                                final String replaced = globalName + "." + localTags[j];
                                LogUtils.getLogger().log(Level.FINE, "        Replacing '" + localTags[j] + "' with '" + replaced + "'");
                                localTags[j] = replaced;
                            }
                        }
                        pack.getEvents().getConfig().set(key,
                                instruction.replace(parts[2], StringUtils.join(Arrays.asList(localTags), ',')));
                    } else if (instruction.startsWith("point ")) {
                        LogUtils.getLogger().log(Level.FINE, "      Found point event, replacing points");
                        final String[] parts = instruction.split(" ");
                        // check if the point has defined a category
                        if (parts.length < 2) {
                            LogUtils.getLogger().log(Level.FINE, "      - Could not find the category");
                            continue;
                        }
                        // replace points category
                        if (globalPointList.contains(parts[1])) {
                            final String replaced = globalName + "." + parts[1];
                            LogUtils.getLogger().log(Level.FINE, "        Replacing '" + parts[1] + "' with '" + replaced + "'");
                            pack.getEvents().getConfig().set(key,
                                    StringUtils.replaceOnce(instruction, parts[1], replaced));
                        }
                    } else if (instruction.startsWith("run ")) {
                        LogUtils.getLogger().log(Level.FINE, "      Found run event, looking for tags and points");
                        final String[] parts = instruction.split(" ");
                        for (int j = 0; j < parts.length; j++) {
                            // if the part is beginning of the "tag" instruction
                            // and it contains a tag
                            if (parts[j].equals("^tag") && j + 2 < parts.length) {
                                LogUtils.getLogger().log(Level.FINE, "        There is a tag event, replacing tags");
                                final String[] localTags = parts[j + 2].split(",");
                                for (int k = 0; k < localTags.length; k++) {
                                    if (globalTagList.contains(localTags[k])) {
                                        final String replaced = globalName + "." + localTags[k];
                                        LogUtils.getLogger().log(Level.FINE, "        Replacing '" + localTags[k] + "' with '" + replaced + "'");
                                        localTags[k] = replaced;
                                    }
                                }
                                parts[j + 2] = StringUtils.join(Arrays.asList(localTags), ',');
                            } else if (parts[j].equals("^point") && j + 1 < parts.length) {
                                LogUtils.getLogger().log(Level.FINE, "        There is a point event, replacing points");
                                if (globalTagList.contains(parts[j + 1])) {
                                    final String replaced = globalName + "." + parts[j + 1];
                                    LogUtils.getLogger().log(Level.FINE, "        Replacing '" + parts[j + 1] + "' with '" + replaced + "'");
                                    parts[j + 1] = replaced;
                                }
                            }
                        }
                        pack.getEvents().getConfig().set(key, StringUtils.join(Arrays.asList(parts), ' '));
                    }
                }
                pack.getEvents().saveConfig();
                LogUtils.getLogger().log(Level.FINE, "  All tags/points replaced in all events");
                // done, everything replaced in events
                // replacing tags/points in conditions
                for (final String key : pack.getConditions().getConfig().getKeys(false)) {
                    LogUtils.getLogger().log(Level.FINE, "    Replacing tags/points in '" + key + "' condition");
                    final String instruction = pack.getConditions().getConfig().getString(key);
                    if (instruction.startsWith("tag ")) {
                        LogUtils.getLogger().log(Level.FINE, "      Found tag condition, replacing the tag");
                        final String[] parts = instruction.split(" ");
                        // check if it contains the tag, if not - continue
                        if (parts.length < 2) {
                            LogUtils.getLogger().log(Level.FINE, "      - Could not find tags");
                            continue;
                        }
                        // replace tag
                        if (globalTagList.contains(parts[1])) {
                            final String replaced = globalName + "." + parts[1];
                            LogUtils.getLogger().log(Level.FINE, "        Replacing '" + parts[1] + "' with '" + replaced + "'");
                            pack.getConditions().getConfig().set(key,
                                    StringUtils.replaceOnce(instruction, parts[1], replaced));
                        }

                    } else if (instruction.startsWith("point ")) {
                        LogUtils.getLogger().log(Level.FINE, "      Found point condition, replacing points");
                        final String[] parts = instruction.split(" ");
                        // check if the point has defined a category
                        if (parts.length < 2) {
                            LogUtils.getLogger().log(Level.FINE, "      - Could not find the category");
                            continue;
                        }
                        // replace points category
                        if (globalPointList.contains(parts[1])) {
                            final String replaced = globalName + "." + parts[1];
                            LogUtils.getLogger().log(Level.FINE, "        Replacing '" + parts[1] + "' with '" + replaced + "'");
                            pack.getConditions().getConfig().set(key,
                                    StringUtils.replaceOnce(instruction, parts[1], replaced));
                        }
                    } else if (instruction.startsWith("check ")) {
                        LogUtils.getLogger().log(Level.FINE, "      Found check condition, looking for tags and points");
                        final String[] parts = instruction.split(" ");
                        for (int j = 0; j < parts.length; j++) {
                            // if the part is beginning of the "tag" instruction
                            // and it contains a tag
                            if (parts[j].equals("^tag") && j + 1 < parts.length) {
                                LogUtils.getLogger().log(Level.FINE, "        There is a tag condition, replacing tags");
                                if (globalTagList.contains(parts[j + 1])) {
                                    final String replaced = globalName + "." + parts[j + 1];
                                    LogUtils.getLogger().log(Level.FINE, "        Replacing '" + parts[j + 1] + "' with '" + replaced + "'");
                                    parts[j + 1] = replaced;
                                }
                            } else if (parts[j].equals("^point") && j + 1 < parts.length) {
                                LogUtils.getLogger().log(Level.FINE, "        There is a point condition, replacing points");
                                if (globalTagList.contains(parts[j + 1])) {
                                    final String replaced = globalName + "." + parts[j + 1];
                                    LogUtils.getLogger().log(Level.FINE, "        Replacing '" + parts[j + 1] + "' with '" + replaced + "'");
                                    parts[j + 1] = replaced;
                                }
                            }
                        }
                        pack.getConditions().getConfig().set(key, StringUtils.join(Arrays.asList(parts), ' '));
                    }
                }
                pack.getConditions().saveConfig();
                LogUtils.getLogger().log(Level.FINE, "  All tags/points replaced in all conditions, time for quest cancelers");
                // done, everything replaced in conditions
                // time for quest cancelers
                for (final String key : pack.getMain().getConfig().getConfigurationSection("cancel").getKeys(false)) {
                    LogUtils.getLogger().log(Level.FINE, "    Replacing tags/points in '" + key + "' canceler");
                    final String instruction = pack.getMain().getConfig().getString("cancel." + key);
                    final String[] parts = instruction.split(" ");
                    for (int j = 0; j < parts.length; j++) {
                        if (parts[j].startsWith("tags:")) {
                            final String[] localTags = parts[j].substring(5).split(",");
                            for (int k = 0; k < localTags.length; k++) {
                                if (globalTagList.contains(localTags[k])) {
                                    final String replaced = globalName + "." + localTags[k];
                                    LogUtils.getLogger().log(Level.FINE, "      Replaced  tag '" + localTags[k] + "' to '" + replaced + "'");
                                    localTags[k] = replaced;
                                }
                            }
                            parts[j] = "tags:" + StringUtils.join(Arrays.asList(localTags), ',');
                        } else if (parts[j].startsWith("points:")) {
                            final String[] localPoints = parts[j].substring(5).split(",");
                            for (int k = 0; k < localPoints.length; k++) {
                                if (globalPointList.contains(localPoints[k])) {
                                    final String replaced = globalName + "." + localPoints[k];
                                    LogUtils.getLogger().log(Level.FINE, "      Replaced  point '" + localPoints[k] + "' to '" + replaced + "'");
                                    localPoints[k] = replaced;
                                }
                            }
                            parts[j] = "points:" + StringUtils.join(Arrays.asList(localPoints), ',');
                        }
                    }
                    pack.getMain().getConfig().set("cancel." + key, StringUtils.join(Arrays.asList(parts), " "));
                }
                LogUtils.getLogger().log(Level.FINE, "  All tags/points replaced in quest cancelers");
                pack.getMain().saveConfig();
            }
            // done, all packages have replaced tags and points
            LogUtils.getLogger().log(Level.FINE,
                    "Done, all global tags and points are prefixed as global everywhere in every package. Updating the database.");
            for (final String packName : tags.keySet()) {
                for (final String tag : tags.get(packName)) {
                    instance.getSaver()
                            .add(new Record(UpdateType.RENAME_ALL_TAGS, new String[]{packName + "." + tag, tag}));
                }
            }
            for (final String packName : points.keySet()) {
                for (final String point : points.get(packName)) {
                    instance.getSaver().add(
                            new Record(UpdateType.RENAME_ALL_POINTS, new String[]{packName + "." + point, point}));
                }
            }
            for (final String packName : points.keySet()) {
                for (final String point : points.get(packName)) {
                    instance.getSaver().add(
                            new Record(UpdateType.RENAME_ALL_GLOBAL_POINTS, new String[]{packName + "." + point, point}));
                }
            }
            // remove "tag_point_prefix" option from main.yml files
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                final ConfigAccessor main = pack.getMain();
                main.getConfig().set("tag_point_prefix", null);
                main.saveConfig();
            }
            LogUtils.getLogger().log(Level.FINE, "Done, all cross-package tags and points are now global, the rest is local.");
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Moved all package-less cross-package tags and points to \"" + globalName
                + "\" package (you probably won't notice this change)");
        config.set("version", "v29");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV27() {
        try {
            config.set("journal.chars_per_page", "245");
            config.set("journal.one_entry_per_page", "false");
            config.set("journal.reversed_order", "false");
            config.set("journal.hide_date", "false");
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Added journal options");
        config.set("version", "v28");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV26() {
        try {
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                for (final String convName : pack.getConversationNames()) {
                    final FileConfiguration conv = pack.getConversation(convName).getConfig();
                    final ConfigurationSection playerSection = conv.getConfigurationSection("player_options");
                    if (playerSection != null) {
                        for (final String playerKey : playerSection.getKeys(false)) {
                            if (conv.isConfigurationSection("player_options." + playerKey + ".text")) {
                                for (final String langKey : conv
                                        .getConfigurationSection("player_options." + playerKey + ".text")
                                        .getKeys(false)) {
                                    conv.set("player_options." + playerKey + ".text." + langKey,
                                            conv.getString("player_options." + playerKey + ".text." + langKey)
                                                    .replace("%quester%", "%npc%"));
                                }
                            } else {
                                conv.set("player_options." + playerKey + ".text",
                                        conv.getString("player_options." + playerKey + ".text").replace("%quester%",
                                                "%npc%"));
                            }
                        }
                    }
                    final ConfigurationSection npcSection = conv.getConfigurationSection("NPC_options");
                    if (npcSection != null) {
                        for (final String npcKey : npcSection.getKeys(false)) {
                            if (conv.isConfigurationSection("NPC_options." + npcKey + ".text")) {
                                for (final String langKey : conv.getConfigurationSection("NPC_options." + npcKey + ".text")
                                        .getKeys(false)) {
                                    conv.set("NPC_options." + npcKey + ".text." + langKey,
                                            conv.getString("NPC_options." + npcKey + ".text." + langKey)
                                                    .replace("%quester%", "%npc%"));
                                }
                            } else {
                                conv.set("NPC_options." + npcKey + ".text", conv
                                        .getString("NPC_options." + npcKey + ".text").replace("%quester%", "%npc%"));
                            }
                        }
                    }
                    pack.getConversation(convName).saveConfig();
                }
            }
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Changed %quester% variables to %npc%");
        config.set("version", "v27");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV25() {
        try {
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                final FileConfiguration events = pack.getEvents().getConfig();
                for (final String key : events.getKeys(false)) {
                    final String event = events.getString(key);
                    if (event.startsWith("journal ")) {
                        events.set(key, "journal add " + event.substring(8));
                    }
                }
                pack.getEvents().saveConfig();
            }
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Added \"add\" keyword to journal events");
        config.set("version", "v26");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV24() {
        LogUtils.getLogger().log(Level.INFO, "Added prefix to language files");
        config.set("version", "v25");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV23() {
        try {
            LogUtils.getLogger().log(Level.FINE, "Adding option to disable mcMMO hooking to the config");
            config.set("hook.mcmmo", "true");
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Added mcMMO compatibility");
        config.set("version", "v24");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV22() {
        LogUtils.getLogger().log(Level.INFO, "Added Dutch translation");
        config.set("version", "v23");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV21() {
        try {
            LogUtils.getLogger().log(Level.FINE, "Updating the database");
            final Connection con = instance.getDB().getConnection();
            final String prefix = Config.getString("config.mysql.prefix");
            // update database format
            LogUtils.getLogger().log(Level.FINE, "Adding conversation column to player table");
            if (instance.isMySQLUsed()) {
                con.prepareStatement(
                        "ALTER TABLE " + prefix + "player ADD conversation VARCHAR(512) AFTER language;")
                        .executeUpdate();
            } else {
                con.prepareStatement("BEGIN TRANSACTION").executeUpdate();
                con.prepareStatement("ALTER TABLE " + prefix + "player RENAME TO " + prefix + "player_old")
                        .executeUpdate();
                con.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix
                        + "player (id INTEGER PRIMARY KEY AUTOINCREMENT, playerID"
                        + " VARCHAR(256) NOT NULL, language VARCHAR(16) NOT NULL, conversation VARCHAR(512));")
                        .executeUpdate();
                con.prepareStatement("INSERT INTO " + prefix + "player SELECT id, playerID, language, 'null'"
                        + " FROM " + prefix + "player_old").executeUpdate();
                con.prepareStatement("COMMIT").executeUpdate();
            }
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Added conversations to database format");
        config.set("version", "v22");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV20() {
        try {
            final ArrayList<ChatColor> npcColors = new ArrayList<>();
            final ArrayList<ChatColor> textColors = new ArrayList<>();
            final ArrayList<ChatColor> numberColors = new ArrayList<>();
            final ArrayList<ChatColor> optionColors = new ArrayList<>();
            final ArrayList<ChatColor> playerColors = new ArrayList<>();
            final ArrayList<ChatColor> answerColors = new ArrayList<>();
            // get npc message format
            final String npcFormat = config.getString("conversation.quester_line_format");
            final String[] npcParts = npcFormat.split("%quester%");
            if (npcParts.length == 2) {
                try {
                    for (final String code : npcParts[0].split("&")) {
                        if (code.length() < 1) {
                            continue;
                        }
                        npcColors.add(ChatColor.getByChar(code.charAt(0)));
                    }
                    for (final String code : npcParts[1].split("&")) {
                        if (code.length() < 1) {
                            continue;
                        }
                        textColors.add(ChatColor.getByChar(code.charAt(0)));
                    }
                } catch (Exception e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not parse NPC text format, saving defaults");
                    LogUtils.logThrowable(e);
                    npcColors.add(ChatColor.DARK_RED);
                    textColors.add(ChatColor.GREEN);
                    textColors.add(ChatColor.ITALIC);
                }
            } else {
                LogUtils.getLogger().log(Level.FINE, "Could not parse NPC text format, saving defaults");
                npcColors.add(ChatColor.DARK_RED);
                textColors.add(ChatColor.GREEN);
                textColors.add(ChatColor.ITALIC);
            }
            // get player option format
            final String optionFormat = config.getString("conversation.quester_reply_format");
            final String[] optionParts = optionFormat.split("%number%");
            if (optionParts.length == 2) {
                try {
                    for (final String code : optionParts[0].split("&")) {
                        if (code.length() < 1) {
                            continue;
                        }
                        numberColors.add(ChatColor.getByChar(code.charAt(0)));
                    }
                    for (final String code : optionParts[1].split("&")) {
                        if (code.length() < 1) {
                            continue;
                        }
                        optionColors.add(ChatColor.getByChar(code.charAt(0)));
                    }
                } catch (Exception e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not parse player option format, saving defaults");
                    LogUtils.logThrowable(e);
                    numberColors.add(ChatColor.YELLOW);
                    optionColors.add(ChatColor.AQUA);
                }
            } else {
                LogUtils.getLogger().log(Level.FINE, "Could not parse player option format, saving defaults");
                numberColors.add(ChatColor.YELLOW);
                optionColors.add(ChatColor.AQUA);
            }
            // get player answer format
            final String answerFormat = config.getString("conversation.player_reply_format");
            final String[] answerParts = answerFormat.split("%player%");
            if (answerParts.length == 2) {
                try {
                    for (final String code : answerParts[0].split("&")) {
                        if (code.length() < 1) {
                            continue;
                        }
                        playerColors.add(ChatColor.getByChar(code.charAt(0)));
                    }
                    for (final String code : answerParts[1].split("&")) {
                        if (code.length() < 1) {
                            continue;
                        }
                        answerColors.add(ChatColor.getByChar(code.charAt(0)));
                    }
                } catch (Exception e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not parse player answer format, saving defaults");
                    LogUtils.logThrowable(e);
                    playerColors.add(ChatColor.DARK_GREEN);
                    answerColors.add(ChatColor.GRAY);
                }
            } else {
                LogUtils.getLogger().log(Level.FINE, "Could not parse player answer format, saving defaults");
                playerColors.add(ChatColor.DARK_GREEN);
                answerColors.add(ChatColor.GRAY);
            }
            final StringBuilder npc = new StringBuilder();
            final StringBuilder text = new StringBuilder();
            final StringBuilder number = new StringBuilder();
            final StringBuilder option = new StringBuilder();
            final StringBuilder player = new StringBuilder();
            final StringBuilder answer = new StringBuilder();
            for (final ChatColor color : npcColors) {
                if (color == null) {
                    continue;
                }
                npc.append(color.name().toLowerCase() + ",");
            }
            config.set("conversation_colors.npc", npc.substring(0, npc.length() - 1));
            for (final ChatColor color : textColors) {
                if (color == null) {
                    continue;
                }
                text.append(color.name().toLowerCase() + ",");
            }
            config.set("conversation_colors.text", text.substring(0, text.length() - 1));
            for (final ChatColor color : numberColors) {
                if (color == null) {
                    continue;
                }
                number.append(color.name().toLowerCase() + ",");
            }
            config.set("conversation_colors.number", number.substring(0, number.length() - 1));
            for (final ChatColor color : optionColors) {
                if (color == null) {
                    continue;
                }
                option.append(color.name().toLowerCase() + ",");
            }
            config.set("conversation_colors.option", option.substring(0, option.length() - 1));
            for (final ChatColor color : playerColors) {
                if (color == null) {
                    continue;
                }
                player.append(color.name().toLowerCase() + ",");
            }
            config.set("conversation_colors.player", player.substring(0, player.length() - 1));
            for (final ChatColor color : answerColors) {
                if (color == null) {
                    continue;
                }
                answer.append(color.name().toLowerCase() + ",");
            }
            config.set("conversation_colors.answer", answer.substring(0, answer.length() - 1));
            config.set("conversation", null);
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Converted conversation format strings to colors");
        config.set("version", "v21");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV19() {
        try {
            if (config.getString("tellraw").equalsIgnoreCase("true")) {
                config.set("default_conversation_IO", "tellraw");
            } else {
                config.set("default_conversation_IO", "simple");
            }
            config.set("tellraw", null);
            final FileConfiguration messages = Config.getMessages().getConfig();
            String message;
            message = messages.getString("global.quester_line_format");
            if (message == null) {
                message = "&4%quester%&f: &a&o";
            }
            config.set("conversation.quester_line_format", message);
            message = messages.getString("global.quester_reply_format");
            if (message == null) {
                message = "&e%number%. &b";
            }
            config.set("conversation.quester_reply_format", message);
            message = messages.getString("global.player_reply_format");
            if (message == null) {
                message = "&2%player%&f: &7";
            }
            config.set("conversation.player_reply_format", message);
            message = messages.getString("global.date_format");
            if (message == null) {
                message = "dd.MM.yyyy HH:mm";
            }
            config.set("date_format", message);
            final String cancelColor = messages.getString("global.cancel_color", "&2");
            messages.set("global", null);
            LogUtils.getLogger().log(Level.INFO, "Moved 'global' messages to main config.");
            Config.getMessages().saveConfig();
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                LogUtils.getLogger().log(Level.FINE, "Processing " + packName + " package");
                final ConfigurationSection cancelers = pack.getMain().getConfig().getConfigurationSection("cancel");
                for (final String key : cancelers.getKeys(false)) {
                    final String canceler = cancelers.getString(key);
                    final StringBuilder string = new StringBuilder();
                    for (final String part : canceler.split(" ")) {
                        if (part.startsWith("name")) {
                            string.append(part.replace(":", ":" + cancelColor) + " ");
                        } else {
                            string.append(part + " ");
                        }
                    }
                    cancelers.set(key, string.toString().trim());
                    LogUtils.getLogger().log(Level.FINE, "  Updated " + key + " canceler name color");
                }
                pack.getMain().saveConfig();
                for (final String convName : pack.getConversationNames()) {
                    final ConfigAccessor conv = pack.getConversation(convName);
                    conv.getConfig().set("unknown", null);
                    conv.saveConfig();
                    LogUtils.getLogger().log(Level.FINE, "  Removed 'unknown' messages from " + convName + " conversation");
                }
            }
            LogUtils.getLogger().log(Level.INFO, "Removed no longer used 'unknown' message from conversations.");
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        config.set("version", "v20");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV18() {
        try {
            final ConfigAccessor confMessages = Config.getMessages();
            final FileConfiguration messages = confMessages.getConfig();
            for (final String lang : messages.getKeys(false)) {
                if (lang.equalsIgnoreCase("global")) {
                    continue;
                }
                LogUtils.getLogger().log(Level.FINE, "Updating " + lang + " language");
                try {
                    messages.set(lang + ".purged", messages.getString(lang + ".purged").replace("%player%", "{1}"));
                    messages.set(lang + ".item_created",
                            messages.getString(lang + ".item_created").replace("%item%", "{1}"));
                    messages.set(lang + ".player_event",
                            messages.getString(lang + ".player_event").replace("%event%", "{1}"));
                    messages.set(lang + ".player_condition", messages.getString(lang + ".player_condition")
                            .replace("%condition%", "{1}").replace("%outcome%", "{2}"));
                    messages.set(lang + ".quest_canceled",
                            messages.getString(lang + ".quest_canceled").replace("%quest%", "{1}"));
                    messages.set(lang + ".items_given", messages.getString(lang + ".items_given")
                            .replace("%name%", "{1}").replace("%amount%", "{2}"));
                    messages.set(lang + ".items_taken", messages.getString(lang + ".items_taken")
                            .replace("%name%", "{1}").replace("%amount%", "{2}"));
                    messages.set(lang + ".blocks_to_break",
                            messages.getString(lang + ".blocks_to_break").replace("%amount%", "{1}"));
                    messages.set(lang + ".blocks_to_place",
                            messages.getString(lang + ".blocks_to_place").replace("%amount%", "{1}"));
                    messages.set(lang + ".mobs_to_kill",
                            messages.getString(lang + ".mobs_to_kill").replace("%amount%", "{1}"));
                    messages.set(lang + ".conversation_start",
                            messages.getString(lang + ".conversation_start").replace("%quester%", "{1}"));
                    messages.set(lang + ".conversation_end",
                            messages.getString(lang + ".conversation_end").replace("%quester%", "{1}"));
                } catch (NullPointerException e) {
                    LogUtils.getLogger().log(Level.WARNING, "The language " + lang + " is not present in the defaults, please update it manually.");
                    LogUtils.logThrowable(e);
                }
            }
            confMessages.saveConfig();
            LogUtils.getLogger().log(Level.INFO, "Updated messages to new replace format");
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        config.set("version", "v19");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV17() {
        try {
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                final ConfigAccessor main = pack.getMain();
                main.getConfig().set("tag_point_prefix", "false");
                main.saveConfig();
            }
            LogUtils.getLogger().log(Level.INFO, "Added prefix option to all packages.");
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        config.set("version", "v18");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV16() {
        try {
            // move objectives from events.yml to objectives.yml
            LogUtils.getLogger().log(Level.FINE, "Moving objectives to objectives.yml");
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                LogUtils.getLogger().log(Level.FINE, "  Package " + packName);
                final ConfigAccessor events = pack.getEvents();
                final ConfigAccessor objectives = pack.getObjectives();
                final ConfigAccessor main = pack.getMain();
                for (final String event : events.getConfig().getKeys(false)) {
                    // extract label and build the new instruction
                    int index = 0; // counts unnamed objectives
                    final String instruction = events.getConfig().getString(event);
                    if (instruction.startsWith("objective ")) {
                        LogUtils.getLogger().log(Level.FINE, "    Starting event " + event);
                        final String[] parts = instruction.substring(10).split(" ");
                        final StringBuilder string = new StringBuilder();
                        String label = null;
                        String conditions = "";
                        for (final String part : parts) {
                            if (part.startsWith("label:")) {
                                label = part.substring(6);
                            } else if (part.startsWith("event_conditions:")) {
                                conditions = part;
                            } else if (parts[0].equalsIgnoreCase("delay") && part.startsWith("delay:")) {
                                string.append(part.substring(6));
                                string.append(' ');
                            } else {
                                string.append(part);
                                string.append(' ');
                            }
                        }
                        final String newInstruction = string.toString().trim();
                        // if label is not present, skip this one
                        if (label == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no label, generating one");
                            label = "objective" + index;
                            index++;
                        }
                        LogUtils.getLogger().log(Level.FINE, "      Saving the objective as " + label + ", instruction: " + newInstruction);
                        // save objective and generate label
                        objectives.getConfig().set(label, newInstruction);
                        events.getConfig().set(event, ("objective start " + label + " " + conditions).trim());
                    } else if (instruction.startsWith("delete ")) {
                        // update delete events
                        LogUtils.getLogger().log(Level.FINE, "    Delete event " + event);
                        events.getConfig().set(event, "objective " + instruction);
                    }
                }
                // rename event_conditions to conditions
                for (final String event : events.getConfig().getKeys(false)) {
                    final String instruction = events.getConfig().getString(event);
                    events.getConfig().set(event, instruction.replace("event_conditions:", "conditions:"));
                }
                // update global locations
                final String raw = main.getConfig().getString("global_locations");
                if (raw != null && !raw.equals("")) {
                    final StringBuilder string = new StringBuilder();
                    final String[] parts = raw.split(",");
                    for (final String event : parts) {
                        final String inst = events.getConfig().getString(event);
                        if (inst == null) {
                            continue;
                        }
                        final String[] instParts = inst.split(" ");
                        if (instParts.length > 2 && inst.startsWith("objective start ")) {
                            string.append(instParts[2] + ",");
                        }
                    }
                    main.getConfig().set("global_locations", string.substring(0, string.length() - 1));
                }
                events.saveConfig();
                objectives.saveConfig();
                main.saveConfig();
            }
            LogUtils.getLogger().log(Level.INFO, "Moved objectives to a separate file and renamed"
                    + " 'event_conditions:' argument to 'conditions:'");
            LogUtils.getLogger().log(Level.FINE, "Updating the database");
            final Connection con = instance.getDB().getConnection();
            final String prefix = Config.getString("config.mysql.prefix");
            // update database format
            LogUtils.getLogger().log(Level.FINE, "Updating the database format");
            if (instance.isMySQLUsed()) {
                con.prepareStatement(
                        "ALTER TABLE " + prefix + "objectives ADD objective VARCHAR(512) NOT NULL AFTER playerID;")
                        .executeUpdate();
            } else {
                con.prepareStatement("BEGIN TRANSACTION").executeUpdate();
                con.prepareStatement("ALTER TABLE " + prefix + "objectives RENAME TO " + prefix + "objectives_old")
                        .executeUpdate();
                con.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS " + prefix + "objectives (id INTEGER PRIMARY KEY AUTOINCREMENT,"
                                + " playerID VARCHAR(256) NOT NULL, objective VARCHAR(512)"
                                + " NOT NULL, instructions VARCHAR(2048) NOT NULL);")
                        .executeUpdate();
                con.prepareStatement("INSERT INTO " + prefix + "objectives"
                        + " SELECT id, playerID, 'null', instructions FROM " + prefix + "objectives_old")
                        .executeUpdate();
                con.prepareStatement("COMMIT").executeUpdate();
            }
            // update each entry
            LogUtils.getLogger().log(Level.FINE, "Updating entries");
            final ResultSet res = con.prepareStatement("SELECT * FROM " + prefix + "objectives").executeQuery();
            while (res.next()) {
                final String oldInst = res.getString("instructions");
                LogUtils.getLogger().log(Level.FINE, "  Loaded instruction: " + oldInst);
                String label = null;
                final String[] parts = oldInst.split(" ");
                final String newInst;
                for (final String part : parts) {
                    if (part.startsWith("label:")) {
                        label = part.substring(6);
                        break;
                    }
                }
                if (label == null) {
                    LogUtils.getLogger().log(Level.FINE, "    The objective without label, removing");
                    final PreparedStatement stmt = con.prepareStatement("DELETE FROM " + prefix + "objectives WHERE id = ?");
                    stmt.setInt(1, res.getInt("id"));
                    stmt.executeUpdate();
                    continue;
                }
                // attack correct package in front of the label
                for (final ConfigPackage pack : Config.getPackages().values()) {
                    final String packName = pack.getName();
                    if (pack.getObjectives().getConfig().contains(label)) {
                        label = packName + "." + label;
                        break;
                    }
                }
                try {
                    switch (parts[0].toLowerCase()) {
                        case "tame":
                        case "block":
                        case "smelt":
                        case "craft":
                        case "mobkill":
                            newInst = parts[2];
                            break;
                        case "delay":
                            newInst = parts[1].substring(6);
                            break;
                        case "npckill":
                        case "mmobkill":
                            newInst = parts[2].substring(7);
                            break;
                        default:
                            newInst = "";
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    LogUtils.getLogger().log(Level.WARNING, "    Could not read data from objective " + label + ", removing");
                    LogUtils.logThrowable(e);
                    final PreparedStatement stmt = con
                            .prepareStatement("DELETE FROM " + prefix + "objectives WHERE id = ?");
                    stmt.setInt(1, res.getInt("id"));
                    stmt.executeUpdate();
                    continue;
                }
                LogUtils.getLogger().log(Level.FINE, "    Updating the " + label + " objective: '" + newInst + "'");
                final PreparedStatement stmt = con.prepareStatement(
                        "UPDATE " + prefix + "objectives SET objective=?, instructions=? WHERE id = ?");
                stmt.setString(1, label);
                stmt.setString(2, newInst);
                stmt.setInt(3, res.getInt("id"));
                stmt.executeUpdate();
            }
            LogUtils.getLogger().log(Level.INFO, "Updated objective instruction strings in the database");
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        config.set("version", "v17");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV15() {
        try {
            config.set("remove_items_after_respawn", "true");
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        config.set("version", "v16");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV14() {
        try {
            if (config.getString("uuid").equals("false")) {
                convertNamesToUUID();
            }
            config.set("default_package", "default");
            config.set("cmd_blacklist", new String[]{"spawn"});
            config.set("uuid", null);
            config.set("metrics", null);
            config.set("hook.citizens", "true");
            config.set("hook.mythicmobs", "true");
            config.set("hook.vault", "true");
            config.set("hook.worldguard", "true");
            config.set("hook.skript", "true");
            LogUtils.getLogger().log(Level.INFO, "Added default_package, hook and cmd_blacklist"
                    + " options to main config, removed metrics and uuid!");
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        config.set("version", "v15");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV13() {
        try {
            LogUtils.getLogger().log(Level.FINE, "Removing empty lines in conversation files");
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                LogUtils.getLogger().log(Level.FINE, "  Package " + packName);
                for (final String convName : pack.getConversationNames()) {
                    LogUtils.getLogger().log(Level.FINE, "    Conversation " + convName);
                    final ConfigAccessor conv = pack.getConversation(convName);
                    for (final String key : conv.getConfig().getKeys(true)) {
                        if (conv.getConfig().getString(key).equals("")) {
                            LogUtils.getLogger().log(Level.FINE, "      Key removed: " + key);
                            conv.getConfig().set(key, null);
                        }
                    }
                    conv.saveConfig();
                }
            }
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Removed unnecessary empty lines in conversation config files.");
        config.set("version", "v14");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV12() {
        try {
            LogUtils.getLogger().log(Level.FINE, "Moving all configuration to \"default\" package");
            // clear the default package, which contains only default quest
            final File defPkg = Config.getPackages().get("default").getFolder();
            LogUtils.getLogger().log(Level.FINE, "  Deleting default files");
            for (final File file : defPkg.listFiles()) {
                file.delete();
            }
            // move files that can be moved without modifications
            final File root = instance.getDataFolder();
            final String[] filesToMove = {"events", "conditions", "items", "journal"};
            for (final String fileToMove : filesToMove) {
                LogUtils.getLogger().log(Level.FINE, "  Moving " + fileToMove + ".yml file");
                new File(root, fileToMove + ".yml").renameTo(new File(defPkg, fileToMove + ".yml"));
            }
            // move all conversations
            final File newConversationFolder = new File(defPkg, "conversations");
            final File oldConversationFolder = new File(root, "conversations");
            newConversationFolder.mkdir();
            for (final File conversation : oldConversationFolder.listFiles()) {
                LogUtils.getLogger().log(Level.FINE, "  Moving " + conversation.getName() + " conversation file");
                conversation.renameTo(new File(newConversationFolder, conversation.getName()));
            }
            // generate main.yml file
            LogUtils.getLogger().log(Level.FINE, "  Generating main.yml file");
            final File mainFile = new File(defPkg, "main.yml");
            mainFile.createNewFile();
            final FileConfiguration main = YamlConfiguration.loadConfiguration(mainFile);
            // copy the data
            final String globalLocations = config.getString("global_locations");
            final ConfigurationSection staticEvents = config.getConfigurationSection("static");
            final ConfigurationSection npcs = configHandler.getConfigs().get("npcs").getConfig().getRoot();
            main.set("global_locations", globalLocations);
            if (staticEvents != null) {
                for (final String key : staticEvents.getKeys(false)) {
                    main.set("static." + key, staticEvents.getString(key));
                }
            }
            if (npcs != null) {
                for (final String key : npcs.getKeys(false)) {
                    main.set("npcs." + key, npcs.getString(key));
                }
                for (final File conv : newConversationFolder.listFiles()) {
                    main.set("npcs." + conv.getName().replace(".yml", ""), conv.getName().replace(".yml", ""));
                }
            }
            main.save(mainFile);
            // remove old values from configuration
            LogUtils.getLogger().log(Level.FINE, "  Removing old files and config values");
            oldConversationFolder.delete();
            config.set("global_locations", null);
            config.set("static", null);
            new File(root, "npcs.yml").delete();
            LogUtils.getLogger().log(Level.FINE, "Configuration updated!");
            LogUtils.getLogger().log(Level.INFO, "Updating the database, it may take a long time!");
            final Connection con = instance.getDB().getConnection();
            final String prefix = instance.getConfig().getString("mysql.prefix", "");
            ResultSet res = con.createStatement().executeQuery("SELECT * FROM " + prefix + "objectives");
            final ArrayList<String[]> objectives = new ArrayList<>();
            // iterate over every objective string in the database
            while (res.next()) {
                final String[] parts = res.getString("instructions").split(" ");
                final StringBuilder newInstruction = new StringBuilder();
                for (final String part : parts) {
                    if (part.startsWith("events:")) {
                        newInstruction.append("events:");
                        final String[] events = part.substring(7).split(",");
                        for (final String event : events) {
                            newInstruction.append("default." + event + ",");
                        }
                        newInstruction.deleteCharAt(newInstruction.length() - 1);
                    } else if (part.startsWith("conditions:")) {
                        newInstruction.append("conditions:");
                        final String[] conditions = part.substring(11).split(",");
                        for (final String condition : conditions) {
                            newInstruction.append("default." + condition + ",");
                        }
                        newInstruction.deleteCharAt(newInstruction.length() - 1);
                    } else {
                        newInstruction.append(part);
                    }
                    newInstruction.append(' ');
                }
                objectives.add(new String[]{res.getString("playerID"), newInstruction.toString().trim()});
            }
            res = con.createStatement().executeQuery("SELECT * FROM " + prefix + "journal");
            final ArrayList<String[]> pointers = new ArrayList<>();
            // iterate over every journal pointer in the database
            while (res.next()) {
                pointers.add(new String[]{res.getString("playerID"), "default." + res.getString("pointer"),
                        res.getString("date")});
            }
            con.createStatement().executeUpdate("DELETE FROM " + prefix + "objectives");
            con.createStatement().executeUpdate("DELETE FROM " + prefix + "journal");
            for (final String[] objective : objectives) {
                final PreparedStatement stmt = con.prepareStatement(
                        "INSERT INTO " + prefix + "objectives (playerID, instructions) VALUES (?,?)");
                stmt.setString(1, objective[0]);
                stmt.setString(2, objective[1]);
                stmt.executeUpdate();
            }
            for (final String[] pointer : pointers) {
                final PreparedStatement stmt = con.prepareStatement(
                        "INSERT INTO " + prefix + "journal (playerID, pointer, date) VALUES (?,?,?)");
                stmt.setString(1, pointer[0]);
                stmt.setString(2, pointer[1]);
                stmt.setString(3, pointer[2]);
                stmt.executeUpdate();
            }
            LogUtils.getLogger().log(Level.FINE, "Done! Everything converted.");
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Introduced new packaging system and moved configuration to \"default\" package!");
        config.set("version", "v13");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV11() {
        try {
            LogUtils.getLogger().log(Level.FINE, "Updating objectives in configuration");
            final ConfigAccessor events = configHandler.getConfigs().get("events");
            final ArrayList<String> labels = new ArrayList<>();
            boolean notified = false;
            // for every event check if it's objective
            for (final String key : events.getConfig().getKeys(false)) {
                final String value = events.getConfig().getString(key);
                if (value.startsWith("objective ")) {
                    LogUtils.getLogger().log(Level.FINE, "  Found " + key + " objective event");
                    // replace "tag:" with "label:" in all found objectives
                    final String[] parts = value.split(" ");
                    final StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].startsWith("tag:")) {
                            final String label = parts[i].substring(4);
                            if (!notified && labels.contains(label)) {
                                notified = true;
                                LogUtils.getLogger().log(Level.WARNING, "You have multiple objectives with the same label!"
                                        + " That is an error, because the player cannot have"
                                        + " active more than one objective with the same label");
                            }
                            labels.add(label);
                            parts[i] = "label:" + label;
                        }
                        builder.append(parts[i]);
                        builder.append(" ");
                    }
                    final String newValue = builder.toString().trim();
                    LogUtils.getLogger().log(Level.FINE, "    After processing: " + newValue);
                    events.getConfig().set(key, newValue);
                }
            }
            events.saveConfig();
            LogUtils.getLogger().log(Level.FINE, "Converted all objectives in configuration");
            // update all objectives in the database
            LogUtils.getLogger().log(Level.INFO, "Converting objectives in the database, it may take a long time");
            final Connection con = instance.getDB().getConnection();
            final String prefix = instance.getConfig().getString("mysql.prefix", "");
            final ResultSet res = con.createStatement().executeQuery("SELECT * FROM " + prefix + "objectives");
            final HashMap<String, ArrayList<String>> objectives = new HashMap<>();
            final HashMap<String, ArrayList<String>> labels2 = new HashMap<>();
            // iterate over every objective string in the database
            while (res.next()) {
                final String playerID = res.getString("playerID");
                final String objective = res.getString("instructions");
                String label = null;
                for (final String part : objective.split(" ")) {
                    if (part.startsWith("tag:")) {
                        label = part.substring(4);
                    }
                }
                if (label == null) {
                    LogUtils.getLogger().log(Level.FINE, "  Found objective without a label, that's strange... Anyway, skipping. Player: "
                            + playerID);
                    continue;
                }
                LogUtils.getLogger().log(Level.FINE, "  Found objective for player " + playerID + " with label " + label);
                ArrayList<String> oList = objectives.get(playerID);
                ArrayList<String> lList = labels2.get(playerID);
                if (oList == null) {
                    oList = new ArrayList<>();
                    lList = new ArrayList<>();
                }
                // cannot have two objectives with the same tag
                if (lList.contains(label)) {
                    LogUtils.getLogger().log(Level.FINE, "    Label already exists, skipping this one!");
                    continue;
                }
                final String converted = convertObjective(objective);
                LogUtils.getLogger().log(Level.FINE, "    Objective converted: " + converted);
                oList.add(converted);
                lList.add(label);
                objectives.put(playerID, oList);
                labels2.put(playerID, lList);
            }
            // everything is extracted from the database and converted
            // time to put it back
            LogUtils.getLogger().log(Level.FINE, "Inserting everything into the database...");
            con.createStatement().executeUpdate("DELETE FROM " + prefix + "objectives");
            for (final String playerID : objectives.keySet()) {
                for (final String objective : objectives.get(playerID)) {
                    final PreparedStatement stmt = con.prepareStatement(
                            "INSERT INTO " + prefix + "objectives (playerID, instructions) VALUES (?,?);");
                    stmt.setString(1, playerID);
                    stmt.setString(2, objective);
                    stmt.executeUpdate();
                }
            }
            LogUtils.getLogger().log(Level.FINE, "Done! Everything converted");
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Changed keyword \"tag:\" to \"label:\" in all objectives!");
        config.set("version", "v12");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV10() {
        try {
            LogUtils.getLogger().log(Level.FINE, "Updating instruction strings");
            LogUtils.getLogger().log(Level.FINE, "  Updating conditions");
            final ConfigAccessor conditions = configHandler.getConfigs().get("conditions");
            conditions:
            for (final String key : conditions.getConfig().getKeys(false)) {
                LogUtils.getLogger().log(Level.FINE, "    Processing " + key + " condition");
                final String instruction = conditions.getConfig().getString(key).trim();
                final String[] parts = instruction.split(" ");
                final String type = parts[0].toLowerCase();
                final ArrayList<String> newParts = new ArrayList<>();
                newParts.add(type);
                switch (type) {
                    case "hand":
                        LogUtils.getLogger().log(Level.FINE, "      Found hand type");
                        String item = null;
                        for (final String part : parts) {
                            if (part.startsWith("item:")) {
                                item = part.substring(5);
                            }
                        }
                        if (item == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no item defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(item);
                        }
                        break;
                    case "or":
                    case "and":
                        LogUtils.getLogger().log(Level.FINE, "      Found or/and type");
                        String orAndConditions = null;
                        for (final String part : parts) {
                            if (part.startsWith("conditions:")) {
                                orAndConditions = part.substring(11);
                            }
                        }
                        if (orAndConditions == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There are no conditions defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(orAndConditions);
                        }
                        break;
                    case "location":
                        LogUtils.getLogger().log(Level.FINE, "      Found location type");
                        String location = null;
                        for (final String part : parts) {
                            if (part.startsWith("loc:")) {
                                location = part.substring(4);
                            }
                        }
                        if (location == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no location defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(location);
                        }
                        break;
                    case "health":
                        LogUtils.getLogger().log(Level.FINE, "      Found health type");
                        String health = null;
                        for (final String part : parts) {
                            if (part.startsWith("health:")) {
                                health = part.substring(7);
                            }
                        }
                        if (health == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no health amount defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(health);
                        }
                        break;
                    case "experience":
                        LogUtils.getLogger().log(Level.FINE, "      Found experience type");
                        String exp = null;
                        for (final String part : parts) {
                            if (part.startsWith("exp:")) {
                                exp = part.substring(4);
                            }
                        }
                        if (exp == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no experience level defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(exp);
                        }
                        break;
                    case "permission":
                        LogUtils.getLogger().log(Level.FINE, "      Found permission type");
                        String perm = null;
                        for (final String part : parts) {
                            if (part.contains("perm:")) {
                                perm = part.substring(5);
                            }
                        }
                        if (perm == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no permission defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(perm);
                        }
                        break;
                    case "point":
                        LogUtils.getLogger().log(Level.FINE, "      Found point type");
                        String category = null;
                        String amount = null;
                        for (final String part : parts) {
                            if (part.startsWith("category:")) {
                                category = part.substring(9);
                            } else if (part.startsWith("count:")) {
                                amount = part.substring(6);
                            }
                        }
                        if (category == null || amount == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no category/amount defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(category);
                            newParts.add(amount);
                        }
                        break;
                    case "tag":
                        LogUtils.getLogger().log(Level.FINE, "      Found tag type");
                        String tag = null;
                        for (final String part : parts) {
                            if (part.startsWith("tag:")) {
                                tag = part.substring(4);
                            }
                        }
                        if (tag == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no tag defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(tag);
                        }
                        break;
                    case "armor":
                        LogUtils.getLogger().log(Level.FINE, "      Found armor type");
                        String material = null;
                        String armorType = null;
                        String enchants = null;
                        for (final String part : parts) {
                            if (part.startsWith("material:")) {
                                material = part.substring(9);
                            }
                            if (part.startsWith("type:")) {
                                armorType = part.substring(5);
                            }
                            if (part.startsWith("enchants:")) {
                                enchants = part;
                            }
                        }
                        if (material == null || type == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no armor defined, skipping");
                            continue conditions;
                        } else {
                            Material armor = null;
                            try {
                                armor = Material.matchMaterial(material + "_" + armorType);
                                if (armor == null) {
                                    armor = Material.matchMaterial(material + "_" + armorType, true);
                                }
                            } catch (Exception e) {
                                LogUtils.getLogger().log(Level.WARNING, "      Could not read armor type, skipping");
                                LogUtils.logThrowable(e);
                                continue conditions;
                            }
                            String itemInstruction = armor.toString();
                            if (enchants != null) {
                                itemInstruction = itemInstruction + " " + enchants;
                            }
                            final ConfigAccessor itemsConfig = configHandler.getConfigs().get("items");
                            int index = 0;
                            while (itemsConfig.getConfig().contains("armor" + index)) {
                                index++;
                            }
                            itemsConfig.getConfig().set("armor" + index, itemInstruction);
                            itemsConfig.saveConfig();
                            newParts.add("armor" + index);
                        }
                        break;
                    case "effect":
                        LogUtils.getLogger().log(Level.FINE, "      Found effect type");
                        String effect = null;
                        for (final String part : parts) {
                            if (part.startsWith("type:")) {
                                effect = part.substring(5);
                            }
                        }
                        if (effect == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no effect defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(effect);
                        }
                        break;
                    case "time":
                        LogUtils.getLogger().log(Level.FINE, "      Found time type");
                        String time = null;
                        for (final String part : parts) {
                            if (part.startsWith("time:")) {
                                time = part.substring(5);
                            }
                        }
                        if (time == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no time defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(time);
                        }
                        break;
                    case "weather":
                        LogUtils.getLogger().log(Level.FINE, "      Found weather type");
                        String weather = null;
                        for (final String part : parts) {
                            if (part.startsWith("type:")) {
                                weather = part.substring(5);
                            }
                        }
                        if (weather == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no weather defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(weather);
                        }
                        break;
                    case "height":
                        LogUtils.getLogger().log(Level.FINE, "      Found height type");
                        String height = null;
                        for (final String part : parts) {
                            if (part.startsWith("height:")) {
                                height = part.substring(7);
                            }
                        }
                        if (height == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no height defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(height);
                        }
                        break;
                    case "rating":
                        LogUtils.getLogger().log(Level.FINE, "      Found rating type");
                        String rating = null;
                        for (final String part : parts) {
                            if (part.startsWith("rating:")) {
                                rating = part.substring(7);
                            }
                        }
                        if (rating == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no rating defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(rating);
                        }
                        break;
                    case "random":
                        LogUtils.getLogger().log(Level.FINE, "      Found random type");
                        String random = null;
                        for (final String part : parts) {
                            if (part.startsWith("random:")) {
                                random = part.substring(7);
                            }
                        }
                        if (random == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no random defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(random);
                        }
                        break;
                    case "money":
                        LogUtils.getLogger().log(Level.FINE, "      Found money type");
                        String money = null;
                        for (final String part : parts) {
                            if (part.startsWith("money:")) {
                                money = part.substring(6);
                            }
                        }
                        if (money == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no amount defined, skipping");
                            continue conditions;
                        } else {
                            newParts.add(money);
                        }
                        break;
                    default:
                        LogUtils.getLogger().log(Level.FINE, "      This one does not need updating");
                        continue conditions;
                }
                final StringBuilder builder = new StringBuilder();
                for (final String part : newParts) {
                    builder.append(part);
                    builder.append(' ');
                }
                final String newInstruction = builder.toString().trim();
                LogUtils.getLogger().log(Level.FINE, "      Processing done, instruction: '" + newInstruction + "'");
                conditions.getConfig().set(key, newInstruction);
            }
            LogUtils.getLogger().log(Level.FINE, "  All conditions updated successfully, saving to the file");
            conditions.saveConfig();

            LogUtils.getLogger().log(Level.FINE, "  Updating events");
            final ConfigAccessor events = configHandler.getConfigs().get("events");
            events:
            for (final String key : events.getConfig().getKeys(false)) {
                LogUtils.getLogger().log(Level.FINE, "    Processing " + key + " event");
                final String instruction = events.getConfig().getString(key).trim();
                final String[] parts = instruction.split(" ");
                final String type = parts[0].toLowerCase();
                final ArrayList<String> newParts = new ArrayList<>();
                newParts.add(type);
                switch (type) {
                    case "folder":
                        LogUtils.getLogger().log(Level.FINE, "      Found folder type");
                        String folderEvents = null;
                        String delay = null;
                        String random = null;
                        for (final String part : parts) {
                            if (part.startsWith("events:")) {
                                folderEvents = part.substring(7);
                            }
                            if (part.startsWith("delay:")) {
                                delay = part;
                            }
                            if (part.startsWith("random:")) {
                                random = part;
                            }
                        }
                        if (events == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There are no events defined, skipping");
                            continue events;
                        } else {
                            newParts.add(folderEvents);
                            if (delay != null) {
                                newParts.add(delay);
                            }
                            if (random != null) {
                                newParts.add(random);
                            }
                        }
                        break;
                    case "setblock":
                        LogUtils.getLogger().log(Level.FINE, "      Found setblock type");
                        String block = null;
                        String loc = null;
                        String data = null;
                        for (final String part : parts) {
                            if (part.startsWith("block:")) {
                                block = part.substring(6);
                            }
                            if (part.startsWith("loc:")) {
                                loc = part.substring(4);
                            }
                            if (part.startsWith("data:")) {
                                data = part;
                            }
                        }
                        if (block == null || loc == null) {
                            LogUtils.getLogger().log(Level.FINE, "      There is no block/location defined, skipping");
                            continue events;
                        } else {
                            newParts.add(block);
                            newParts.add(loc);
                            if (data != null) {
                                newParts.add(data);
                            }
                        }
                        break;
                    default:
                        LogUtils.getLogger().log(Level.FINE, "      This one does not need updating");
                        continue events;
                }
                final StringBuilder builder = new StringBuilder();
                for (final String part : newParts) {
                    builder.append(part);
                    builder.append(' ');
                }
                final String newInstruction = builder.toString().trim();
                LogUtils.getLogger().log(Level.FINE, "      Processing done, instruction: '" + newInstruction + "'");
                events.getConfig().set(key, newInstruction);
            }
            LogUtils.getLogger().log(Level.FINE, "  All events updated successfully, saving to the file");
            events.saveConfig();

        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        LogUtils.getLogger().log(Level.INFO, "Made instruction strings more beautiful! Please read the documentation again.");
        config.set("version", "v11");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV9() {
        config.set("combat_delay", "10");
        config.set("notify_pullback", "false");
        LogUtils.getLogger().log(Level.INFO, "Added combat delay and pullback notify options!");
        config.set("version", "v10");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV8() {
        config.set("version", "v9");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV7() {
        final ConfigAccessor messages = configHandler.getConfigs().get("messages");
        messages.getConfig().set("global.date_format", "dd.MM.yyyy HH:mm");
        messages.saveConfig();
        LogUtils.getLogger().log(Level.INFO, "Added date format line to messages.yml");
        config.set("version", "v8");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV6() {
        LogUtils.getLogger().log(Level.INFO, "Added backpacks to the database!");
        config.set("version", "v7");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV5() {
        try {
            // delete isused column from tables objectives and tags
            final Database database = instance.getDB();
            final Connection connection = database.getConnection();
            final String[] tables = {"objectives", "tags"};
            final String prefix = instance.getConfig().getString("mysql.prefix", "");
            if (instance.isMySQLUsed()) {
                connection.prepareStatement("ALTER TABLE " + prefix + "objectives DROP COLUMN isused;").executeUpdate();
                connection.prepareStatement("ALTER TABLE " + prefix + "tags DROP COLUMN isused;").executeUpdate();
            } else {
                // drop column from objectives
                connection.prepareStatement("BEGIN TRANSACTION").executeUpdate();
                connection
                        .prepareStatement("ALTER TABLE " + prefix + "objectives RENAME TO " + prefix + "objectives_old")
                        .executeUpdate();
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix + "objectives"
                        + " (id INTEGER PRIMARY KEY AUTOINCREMENT, playerID VARCHAR(256) NOT NULL, "
                        + "instructions VARCHAR(2048) NOT NULL);").executeUpdate();
                connection.prepareStatement("INSERT INTO " + prefix + "objectives SELECT id, "
                        + "playerID, instructions FROM " + prefix + "objectives_old").executeUpdate();
                connection.prepareStatement("DROP TABLE " + prefix + "objectives_old").executeUpdate();
                connection.prepareStatement("COMMIT").executeUpdate();
                // drop column from tags
                connection.prepareStatement("BEGIN TRANSACTION").executeUpdate();
                connection.prepareStatement("ALTER TABLE " + prefix + "tags RENAME TO " + prefix + "tags_old")
                        .executeUpdate();
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix + "tags"
                        + " (id INTEGER PRIMARY KEY AUTOINCREMENT, playerID VARCHAR(256) NOT NULL, "
                        + "tag TEXT NOT NULL);").executeUpdate();
                connection.prepareStatement(
                        "INSERT INTO " + prefix + "tags SELECT id, playerID, tag FROM " + prefix + "tags_old")
                        .executeUpdate();
                connection.prepareStatement("DROP TABLE " + prefix + "tags_old").executeUpdate();
                connection.prepareStatement("COMMIT").executeUpdate();
            }
            LogUtils.getLogger().log(Level.INFO, "Updated database format to better one.");
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        config.set("version", "v6");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV4() {
        try {
            // update all give/take events and item condition to match new
            // parser
            final ConfigAccessor eventsAccessor = configHandler.getConfigs().get("events");
            final FileConfiguration eventsConfig = eventsAccessor.getConfig();
            LogUtils.getLogger().log(Level.FINE, "Updating events!");
            // check every event in configuration
            for (final String key : eventsConfig.getKeys(false)) {
                LogUtils.getLogger().log(Level.FINE, "  Processing " + key);
                final String instruction = eventsConfig.getString(key);
                // if the event is of type "give" or "take" then proceed
                if (instruction.startsWith("give ") || instruction.startsWith("take ")) {
                    final String[] parts = instruction.split(" ");
                    LogUtils.getLogger().log(Level.FINE, "    Found " + parts[0] + " event");
                    // get item's amount
                    int amount = 1;
                    for (final String part : parts) {
                        if (part.startsWith("amount:")) {
                            amount = Integer.parseInt(part.substring(7));
                            LogUtils.getLogger().log(Level.FINE, "    Amount is set to " + amount);
                        }
                    }
                    // generate new instruction
                    final String newInstruction = parts[0] + " " + parts[1] + (amount == 1 ? "" : ":" + amount);
                    LogUtils.getLogger().log(Level.FINE, "    Saving instruction '" + newInstruction + "'");
                    // save it
                    eventsConfig.set(key, newInstruction);
                }
            }
            // when all events are converted, save the file
            eventsAccessor.saveConfig();
            // update all item conditions
            final ConfigAccessor conditionsAccessor = configHandler.getConfigs().get("conditions");
            final FileConfiguration conditionsConfig = conditionsAccessor.getConfig();
            LogUtils.getLogger().log(Level.FINE, "Updatng conditions!");
            // check every condition in configuration
            for (final String key : conditionsConfig.getKeys(false)) {
                LogUtils.getLogger().log(Level.FINE, "  Processing " + key);
                final String instruction = conditionsConfig.getString(key);
                // if the condition is of type "item" then proceed
                if (instruction.startsWith("item ")) {
                    final String[] parts = instruction.split(" ");
                    LogUtils.getLogger().log(Level.FINE, "    Found item condition");
                    // get item name and amount
                    String name = null;
                    int amount = 1;
                    for (final String part : parts) {
                        if (part.startsWith("item:")) {
                            name = part.substring(5);
                            LogUtils.getLogger().log(Level.FINE, "    Name is " + name);
                        } else if (part.startsWith("amount:")) {
                            amount = Integer.parseInt(part.substring(7));
                            LogUtils.getLogger().log(Level.FINE, "    Amount is " + amount);
                        }
                    }
                    // generate new instruction
                    final String newInstruction = "item " + name + (amount == 1 ? "" : ":" + amount);
                    LogUtils.getLogger().log(Level.FINE, "    Saving instruction '" + newInstruction + "'");
                    // save it
                    conditionsConfig.set(key, newInstruction);
                }
            }
            // when all conditions are converted, save the file
            conditionsAccessor.saveConfig();
            LogUtils.getLogger().log(Level.INFO, "Converted give/take events and item conditions to new format!");
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        config.set("version", "v5");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV3() {
        config.set("mysql.prefix", "");
        LogUtils.getLogger().log(Level.INFO, "Added prefix option to MySQL settings!");
        config.set("version", "v4");
        instance.saveConfig();
    }

    @SuppressWarnings("unused")
    private void updateFromV2() {
        try {
            // start time counting, because why not?
            final long time = new Date().getTime();
            // Get all conditions with --inverted tag into the map
            // <name,instruction> without --inverted tag and remove them form
            // config
            final ConfigAccessor conditionsAccessor = configHandler.getConfigs().get("conditions");
            final FileConfiguration conditionsConfig = conditionsAccessor.getConfig();
            // at the beginning trim all conditions, so they won't get
            // confused later on
            for (final String path : conditionsConfig.getKeys(false)) {
                conditionsConfig.set(path, conditionsConfig.getString(path).trim());
            }
            final HashMap<String, String> conditionsInverted = new HashMap<>();
            LogUtils.getLogger().log(Level.FINE, "Extracting conditions to a map");
            // for each condition
            for (final String name : conditionsConfig.getKeys(false)) {
                // get instruction
                String condition = conditionsConfig.getString(name);
                boolean wasInverted = false;
                int index = 1;
                LogUtils.getLogger().log(Level.FINE, "  Checking condition " + name);
                // if it is --inverted
                while (condition.contains("--inverted")) {
                    LogUtils.getLogger().log(Level.FINE, "    Loop " + index);
                    index++;
                    LogUtils.getLogger().log(Level.FINE, "      Instruction: '" + condition + "'");
                    // get starting index of --inverted
                    final int startingIndex = condition.indexOf(" --inverted");
                    LogUtils.getLogger().log(Level.FINE, "      First occurence of --inverted tag: " + startingIndex);
                    // get first half (to cut --inverted)
                    final String firstHalf = condition.substring(0, startingIndex);
                    LogUtils.getLogger().log(Level.FINE, "      First half is '" + firstHalf + "'");
                    // get last half (from the end of --inverted string)
                    final String lastHalf = condition.substring(startingIndex + 11);
                    LogUtils.getLogger().log(Level.FINE, "      Last half is '" + lastHalf + "'");
                    // get new condition string without --inverted tag
                    condition = firstHalf + lastHalf;
                    wasInverted = true;
                    LogUtils.getLogger().log(Level.FINE, "      And the whole new condition is '" + condition + "'");
                }
                if (wasInverted) {
                    LogUtils.getLogger().log(Level.FINE, "  Removing from config and putting into a map!");
                    // remove it from config
                    conditionsConfig.set(name, null);
                    // put it into the map
                    conditionsInverted.put(name, condition);
                }
            }
            // for each, check for duplicates
            LogUtils.getLogger().log(Level.FINE, "Checking for duplicates in config");
            final HashMap<String, String> nameChanging = new HashMap<>();
            for (final String invertedName : conditionsInverted.keySet()) {
                // check every condition from the map
                LogUtils.getLogger().log(Level.FINE, "  Checking condition " + invertedName);
                String duplicateName = null;
                for (final String normalName : conditionsConfig.getKeys(false)) {
                    // against every condition that is still in the config
                    if (conditionsConfig.getString(normalName).equals(conditionsInverted.get(invertedName))) {
                        // if it is the same, then we have a match; we need to
                        // mark it as a duplicate
                        LogUtils.getLogger().log(Level.FINE, "    Found a duplicate: " + normalName);
                        duplicateName = normalName;
                    }
                }
                if (duplicateName == null) {
                    // if it doesn't, put into a map as original and !original,
                    // and reinsert into config
                    LogUtils.getLogger().log(Level.FINE, "    Inserting into name changing map, from " + invertedName + " to !" + invertedName);
                    LogUtils.getLogger().log(Level.FINE, "    Readding to configuration!");
                    nameChanging.put(invertedName, "!" + invertedName);
                    conditionsConfig.set(invertedName, conditionsInverted.get(invertedName));
                } else {
                    // if it still exists in config, put it into map <old
                    // name, new name> as duplicate and !original
                    LogUtils.getLogger().log(Level.FINE, "    Inserting into name changing map, from " + invertedName + " to !" + duplicateName);
                    nameChanging.put(invertedName, "!" + duplicateName);
                }
            }
            LogUtils.getLogger().log(Level.FINE, "Starting conditions updating!");
            for (final String key : conditionsConfig.getKeys(false)) {
                final String instruction = conditionsConfig.getString(key).trim();
                LogUtils.getLogger().log(Level.FINE, "  Processing condition " + key);
                if (instruction.startsWith("or ") || instruction.startsWith("and ")) {
                    final String type = instruction.substring(0, instruction.indexOf(" "));
                    LogUtils.getLogger().log(Level.FINE, "    Found " + type + " condition!");
                    final int index = instruction.indexOf(" conditions:") + 12;
                    final String firstPart = instruction.substring(0, index);
                    LogUtils.getLogger().log(Level.FINE, "    First part is '" + firstPart + "'");
                    int secondIndex = index + instruction.substring(index).indexOf(" ");
                    if (secondIndex <= index) {
                        secondIndex = instruction.length();
                    }
                    final String conditionList = instruction.substring(index, secondIndex);
                    LogUtils.getLogger().log(Level.FINE, "    List of conditions is '" + conditionList + "'");
                    final String lastPart = instruction.substring(secondIndex);
                    LogUtils.getLogger().log(Level.FINE, "    Last part is '" + lastPart + "'");
                    final String[] parts = conditionList.split(",");
                    for (int i = 0; i < parts.length; i++) {
                        // check each of them if it should be replaced
                        final String replacement = nameChanging.get(parts[i]);
                        if (replacement != null) {
                            LogUtils.getLogger().log(Level.FINE, "        Replacing " + parts[i] + " with " + replacement);
                            parts[i] = replacement;
                        }
                    }
                    final StringBuilder newConditionsList = new StringBuilder();
                    for (final String part : parts) {
                        newConditionsList.append(part + ",");
                    }
                    final String newInstruction = firstPart
                            + newConditionsList.toString().substring(0, newConditionsList.length() - 1) + lastPart;
                    LogUtils.getLogger().log(Level.FINE, "    New instruction is '" + newInstruction + "'");
                    conditionsConfig.set(key, newInstruction);
                }
            }
            // save conditions so the changes persist
            conditionsAccessor.saveConfig();
            // now we have a map with names which need to be changed across all
            // configuration; for each conversation, for each NPC option and
            // player option, replace old names from the map with new names
            LogUtils.getLogger().log(Level.FINE, "Starting conversation updating");
            // get every conversation accessor
            final HashMap<String, ConfigAccessor> conversations = configHandler.getConversations();
            for (final String conversationName : conversations.keySet()) {
                LogUtils.getLogger().log(Level.FINE, "  Processing conversation " + conversationName);
                final ConfigAccessor conversation = conversations.get(conversationName);
                // this list will store every path to condition list in this
                // conversation
                final List<String> paths = new ArrayList<>();
                // for every npc option, check if it contains conditions
                // variable and add it to the list
                LogUtils.getLogger().log(Level.FINE, "    Extracting conditions from NPC options");
                final ConfigurationSection npcOptions = conversation.getConfig().getConfigurationSection("NPC_options");
                for (final String npcPath : npcOptions.getKeys(false)) {
                    final String conditionPath = "NPC_options." + npcPath + ".conditions";
                    if (conversation.getConfig().isSet(conditionPath)
                            && !conversation.getConfig().getString(conditionPath).equals("")) {
                        LogUtils.getLogger().log(Level.FINE, "      Adding " + conditionPath + " to the list");
                        paths.add(conditionPath);
                    }
                }
                // for every player option, check if it contains conditions
                // variable and add it to the list
                LogUtils.getLogger().log(Level.FINE, "    Extracting conditions from player options");
                final ConfigurationSection playerOptions = conversation.getConfig().getConfigurationSection("player_options");
                for (final String playerPath : playerOptions.getKeys(false)) {
                    final String conditionPath = "player_options." + playerPath + ".conditions";
                    if (conversation.getConfig().isSet(conditionPath)
                            && !conversation.getConfig().getString(conditionPath).equals("")) {
                        LogUtils.getLogger().log(Level.FINE, "      Adding " + conditionPath + " to the list");
                        paths.add(conditionPath);
                    }
                }
                // now we have a list of valid paths to condition variables
                // in this conversation
                for (final String path : paths) {
                    LogUtils.getLogger().log(Level.FINE, "    Processing path " + path);
                    // get the list of conditions (as a single string, separated
                    // by commas)
                    final String list = conversation.getConfig().getString(path);
                    LogUtils.getLogger().log(Level.FINE, "      Original conditions list is: " + list);
                    // split it into an array
                    final String[] conditionArr = list.split(",");
                    for (int i = 0; i < conditionArr.length; i++) {
                        // for every condition name in array check if it should
                        // be replaced
                        final String replacement = nameChanging.get(conditionArr[i]);
                        if (replacement != null) {
                            // and replace it
                            LogUtils.getLogger().log(Level.FINE, "      Replacing " + conditionArr[i] + " with " + replacement);
                            conditionArr[i] = replacement;
                        }
                    }
                    // now when everything is replaced generate new list (as a
                    // single string)
                    final StringBuilder newListBuilder = new StringBuilder();
                    for (final String condition : conditionArr) {
                        newListBuilder.append(condition + ",");
                    }
                    final String newList = newListBuilder.toString().substring(0, newListBuilder.length() - 1);
                    LogUtils.getLogger().log(Level.FINE, "      Saving new list: " + newList);
                    // and set it
                    conversation.getConfig().set(path, newList);
                }
                // save conversation so the changes persist
                conversation.saveConfig();
            }
            // now every conversation is processed, time for events
            // for each event_conditions: and conditions: in events.yml, replace
            // old names from the map with new names
            LogUtils.getLogger().log(Level.FINE, "Starting events updating");
            final ConfigAccessor eventsAccessor = configHandler.getConfigs().get("events");
            for (final String eventName : eventsAccessor.getConfig().getKeys(false)) {
                LogUtils.getLogger().log(Level.FINE, "  Processing event " + eventName);
                // extract event's instruction
                String instruction = eventsAccessor.getConfig().getString(eventName);
                // check if it contains event conditions
                if (instruction.contains(" event_conditions:")) {
                    LogUtils.getLogger().log(Level.FINE, "    Found event conditions!");
                    // extract first half (to the start of condition list
                    final int index = instruction.indexOf(" event_conditions:") + 18;
                    final String firstHalf = instruction.substring(0, index);
                    LogUtils.getLogger().log(Level.FINE, "      First half is '" + firstHalf + "'");
                    // extract condition list
                    int secondIndex = index + instruction.substring(index).indexOf(" ");
                    if (secondIndex <= index) {
                        secondIndex = instruction.length();
                    }
                    final String conditionList = instruction.substring(index, secondIndex);
                    LogUtils.getLogger().log(Level.FINE, "      Condition list is '" + conditionList + "'");
                    // extract last half (from the end of condition list)
                    final String lastHalf = instruction.substring(secondIndex);
                    LogUtils.getLogger().log(Level.FINE, "      Last half is '" + lastHalf + "'");
                    // split conditions into an array
                    final String[] parts = conditionList.split(",");
                    for (int i = 0; i < parts.length; i++) {
                        // check each of them if it should be replaced
                        final String replacement = nameChanging.get(parts[i]);
                        if (replacement != null) {
                            LogUtils.getLogger().log(Level.FINE, "        Replacing " + parts[i] + " with " + replacement);
                            parts[i] = replacement;
                        }
                    }
                    // put it all together
                    final StringBuilder newListBuilder = new StringBuilder();
                    for (final String part : parts) {
                        newListBuilder.append(part + ",");
                    }
                    final String newList = newListBuilder.toString().substring(0, newListBuilder.length() - 1);
                    LogUtils.getLogger().log(Level.FINE, "      New condition list is '" + newList + "'");
                    // put the event together and save it
                    final String newEvent = firstHalf + newList + lastHalf;
                    LogUtils.getLogger().log(Level.FINE, "      Saving instruction '" + newEvent + "'");
                    eventsAccessor.getConfig().set(eventName, newEvent);
                }
                // read the instruction again, it could've changed
                instruction = eventsAccessor.getConfig().getString(eventName);
                // check if it containt objective conditions
                if (instruction.contains(" conditions:")) {
                    LogUtils.getLogger().log(Level.FINE, "    Found objective conditions!");
                    // extract first half (to the start of condition list
                    final int index = instruction.indexOf(" conditions:") + 12;
                    final String firstHalf = instruction.substring(0, index);
                    LogUtils.getLogger().log(Level.FINE, "      First half is '" + firstHalf + "'");
                    // extract condition list
                    final int secondIndex = index + instruction.substring(index).indexOf(" ");
                    final String conditionList = instruction.substring(index, secondIndex);
                    LogUtils.getLogger().log(Level.FINE, "      Condition list is '" + conditionList + "'");
                    // extract last half (from the end of condition list)
                    final String lastHalf = instruction.substring(secondIndex);
                    LogUtils.getLogger().log(Level.FINE, "      Last half is '" + lastHalf + "'");
                    // split conditions into an array
                    final String[] parts = conditionList.split(",");
                    for (int i = 0; i < parts.length; i++) {
                        // check each of them if it should be replaced
                        final String replacement = nameChanging.get(parts[i]);
                        if (replacement != null) {
                            LogUtils.getLogger().log(Level.FINE, "        Replacing " + parts[i] + " with " + replacement);
                            parts[i] = replacement;
                        }
                    }
                    // put it all together
                    final StringBuilder newListBuilder = new StringBuilder();
                    for (final String part : parts) {
                        newListBuilder.append(part + ",");
                    }
                    final String newList = newListBuilder.toString().substring(0, newListBuilder.length() - 1);
                    LogUtils.getLogger().log(Level.FINE, "      New condition list is '" + newList + "'");
                    // put the event together and save it
                    final String newEvent = firstHalf + newList + lastHalf;
                    LogUtils.getLogger().log(Level.FINE, "      Saving instruction '" + newEvent + "'");
                    eventsAccessor.getConfig().set(eventName, newEvent);
                }
                // at this point we finished modifying this one event
            }
            // at this point we finished modifying every event, need to save
            // events
            eventsAccessor.saveConfig();
            // every place where conditions are is now updated, finished!
            LogUtils.getLogger().log(Level.INFO, "Converted inverted conditions to a new format using exclamation marks!");
            LogUtils.getLogger().log(Level.FINE, "Converting took " + (new Date().getTime() - time) + "ms");
        } catch (Exception e) {
            // try-catch block is required - if there is some exception,
            // the version wouldn't get changed and updater would fall into
            // an infinite loop of endless exceptiorns
            LogUtils.getLogger().log(Level.WARNING, ERROR);
            LogUtils.logThrowable(e);
        }
        // set v3 version
        config.set("version", "v3");
        instance.saveConfig();
        // done
    }

    @SuppressWarnings("unused")
    private void updateFromV1() {
        config.set("debug", "false");
        LogUtils.getLogger().log(Level.INFO, "Added debug option to configuration!");
        config.set("version", "v2");
        instance.saveConfig();
    }

    private void updateTo1dot6() {
        config.set("version", "v1");
        instance.saveConfig();
        performUpdate();
    }

    private void updateTo1dot5dot3() {
        // nothing to update
        config.set("version", "1.5.3");
        updateTo1dot6();
    }

    private void updateTo1dot5dot2() {
        // nothing to update
        config.set("version", "1.5.2");
        updateTo1dot5dot3();
    }

    private void updateTo1dot5dot1() {
        // nothing to update
        config.set("version", "1.5.1");
        updateTo1dot5dot2();
    }

    private void updateTo1dot5() {
        LogUtils.getLogger().log(Level.INFO, "Started converting configuration files from v1.4 to v1.5!");
        // add sound settings
        final String[] array1 = {"start", "end", "journal", "update", "full"};
        for (final String string : array1) {
            config.set("sounds." + string, config.getDefaults().getString("sounds." + string));
        }
        LogUtils.getLogger().log(Level.INFO, "Added new sound options!");
        // add colors for journal
        final String[] array2 = {"date.day", "date.hour", "line", "text"};
        for (final String string : array2) {
            config.set("journal_colors." + string, config.getDefaults().getString("journal_colors." + string));
        }
        LogUtils.getLogger().log(Level.INFO, "Added new journal color options!");
        // convert conditions in events to event_condition: format
        LogUtils.getLogger().log(Level.FINE, "Starting updating 'conditions:' argument to 'event_conditions:' in events.yml");
        final ConfigAccessor events = configHandler.getConfigs().get("events");
        for (final String key : events.getConfig().getKeys(false)) {
            LogUtils.getLogger().log(Level.FINE, "  Processing event " + key);
            if (events.getConfig().getString(key).contains("conditions:")) {
                final StringBuilder parts = new StringBuilder();
                for (final String part : events.getConfig().getString(key).split(" ")) {
                    if (part.startsWith("conditions:")) {
                        parts.append("event_conditions:" + part.substring(11) + " ");
                    } else {
                        parts.append(part + " ");
                    }
                }
                LogUtils.getLogger().log(Level.FINE, "    Found 'conditions:' option, replacing!");
                events.getConfig().set(key, parts.substring(0, parts.length() - 1));
            }
        }
        LogUtils.getLogger().log(Level.INFO, "Events now use 'event_conditions:' for conditioning.");
        // convert objectives to new format
        LogUtils.getLogger().log(Level.FINE, "Converting objectives to new format...");
        final ConfigAccessor objectives = configHandler.getConfigs().get("objectives");
        for (final String key : events.getConfig().getKeys(false)) {
            LogUtils.getLogger().log(Level.FINE, "  Processing objective " + key);
            if (events.getConfig().getString(key).split(" ")[0].equalsIgnoreCase("objective")) {
                events.getConfig().set(key, "objective "
                        + objectives.getConfig().getString(events.getConfig().getString(key).split(" ")[1]));
                LogUtils.getLogger().log(Level.FINE, "      Event " + key + " converted!");
            }
        }
        LogUtils.getLogger().log(Level.INFO, "Objectives converted to new, event-powered format!");
        // convert global locations
        final String globalLocations = config.getString("global_locations");
        if (globalLocations != null && !globalLocations.equals("")) {
            final StringBuilder configGlobalLocs = new StringBuilder();
            LogUtils.getLogger().log(Level.INFO, "Converting global locations to use events...");
            int index = 0;
            for (final String globalLoc : config.getString("global_locations").split(",")) {
                index++;
                events.getConfig().set("global_location_" + index,
                        "objective " + objectives.getConfig().getString(globalLoc));
                configGlobalLocs.append("global_location_" + index + ",");
                LogUtils.getLogger().log(Level.INFO, "Converted " + globalLoc + " objective.");
            }
            config.set("global_locations", configGlobalLocs.substring(0, configGlobalLocs.length() - 1));
            LogUtils.getLogger().log(Level.INFO, "All " + index + " global locations have been converted.");
        }
        events.saveConfig();
        LogUtils.getLogger().log(Level.INFO, "Removing old file.");
        new File(instance.getDataFolder(), "objectives.yml").delete();
        // convert books to new format
        LogUtils.getLogger().log(Level.INFO, "Converting books to new format!");
        final ConfigAccessor items = configHandler.getConfigs().get("items");
        for (final String key : items.getConfig().getKeys(false)) {
            final String string = items.getConfig().getString(key);
            if (string.split(" ")[0].equalsIgnoreCase("WRITTEN_BOOK")) {
                String text = null;
                final LinkedList<String> parts = new LinkedList<>(Arrays.asList(string.split(" ")));
                for (final Iterator<String> iterator = parts.iterator(); iterator.hasNext(); ) {
                    final String part = iterator.next();
                    if (part.startsWith("text:")) {
                        text = part.substring(5);
                        iterator.remove();
                        break;
                    }
                }
                if (text != null) {
                    final StringBuilder pages = new StringBuilder();
                    for (final String page : Utils.pagesFromString(text.replace("_", " "))) {
                        pages.append(page.replaceAll(" ", "_") + "|");
                    }
                    parts.add("text:" + pages.substring(0, pages.length() - 2));
                    final StringBuilder instruction = new StringBuilder();
                    for (final String part : parts) {
                        instruction.append(part + " ");
                    }
                    items.getConfig().set(key, instruction.toString().trim().replaceAll("\\n", "\\\\n"));
                    LogUtils.getLogger().log(Level.INFO, "Converted book " + key + ".");
                }
            }
        }
        items.saveConfig();
        LogUtils.getLogger().log(Level.INFO, "All books converted!");
        // JournalBook.pagesFromString(questItem.getText(), false);
        config.set("tellraw", "false");
        LogUtils.getLogger().log(Level.INFO, "Tellraw option added to config.yml!");
        config.set("autoupdate", "true");
        LogUtils.getLogger().log(Level.INFO, "AutoUpdater is now enabled by default! You can change this if you"
                + " want and reload the plugin, nothing will be downloaded in that case.");
        // end of update
        config.set("version", "1.5");
        LogUtils.getLogger().log(Level.INFO, "Conversion to v1.5 finished.");
        updateTo1dot5dot1();
    }

    private void updateTo1dot4dot3() {
        // nothing to update
        config.set("version", "1.4.3");
        updateTo1dot5();
    }

    private void updateTo1dot4dot2() {
        // nothing to update
        config.set("version", "1.4.2");
        updateTo1dot4dot3();
    }

    private void updateTo1dot4dot1() {
        // nothing to update
        config.set("version", "1.4.1");
        updateTo1dot4dot2();
    }

    /**
     * Updates language file, so it contains all required messages.
     */
    private void updateLanguages() {
        // add new languages
        boolean isUpdated = false;
        final ConfigAccessor messages = Config.getMessages();
        // check every language if it exists
        for (final String path : messages.getConfig().getDefaultSection().getKeys(false)) {
            if (messages.getConfig().isSet(path)) {
                // if it exists check every message if it exists
                for (final String messageNode : messages.getConfig().getDefaults().getConfigurationSection(path)
                        .getKeys(false)) {
                    if (!messages.getConfig().isSet(path + "." + messageNode)) {
                        // if message doesn't exist then add it from defaults
                        messages.getConfig().set(path + "." + messageNode,
                                messages.getConfig().getDefaults().get(path + "." + messageNode));
                        isUpdated = true;
                    }
                }
            } else {
                // if language does not exist then add every message to it
                for (final String messageNode : messages.getConfig().getDefaults().getConfigurationSection(path)
                        .getKeys(false)) {
                    messages.getConfig().set(path + "." + messageNode,
                            messages.getConfig().getDefaults().get(path + "." + messageNode));
                    isUpdated = true;
                }
            }
        }
        // if we updated config filse then print the message
        if (isUpdated) {
            messages.saveConfig();
            LogUtils.getLogger().log(Level.INFO, "Updated language files!");
        }
    }

    /**
     * As the name says, converts all names to UUID in database
     */
    @SuppressWarnings("deprecation")
    private void convertNamesToUUID() {
        LogUtils.getLogger().log(Level.INFO, "Converting names to UUID...");
        // loop all tables
        final HashMap<String, String> list = new HashMap<>();
        final String[] tables = {"OBJECTIVES", "TAGS", "POINTS", "JOURNAL", "BACKPACK"};
        final Connector con = new Connector();
        for (final String table : tables) {
            final ResultSet res = con.querySQL(QueryType.valueOf("SELECT_PLAYERS_" + table), new String[]{});
            try {
                while (res.next()) {
                    // and extract from them list of player names
                    final String playerID = res.getString("playerID");
                    if (!list.containsKey(playerID)) {
                        list.put(playerID, Bukkit.getOfflinePlayer(playerID).getUniqueId().toString());
                    }
                }
            } catch (SQLException e) {
                LogUtils.getLogger().log(Level.WARNING, "Could not convert name to UUID");
                LogUtils.logThrowable(e);
            }
        }
        // convert all player names in all tables
        for (final String table : tables) {
            for (final String playerID : list.keySet()) {
                con.updateSQL(UpdateType.valueOf("UPDATE_PLAYERS_" + table),
                        new String[]{list.get(playerID), playerID});
            }
        }
        LogUtils.getLogger().log(Level.INFO, "Names conversion finished!");
    }

    /**
     * Adds the changelog file.
     */
    private void addChangelog() {
        try {
            final File changelog = new File(BetonQuest.getInstance().getDataFolder(), "CHANGELOG.md");
            if (changelog.exists()) {
                changelog.delete();
            }
            Files.copy(BetonQuest.getInstance().getResource("CHANGELOG.md"), changelog.toPath());
            LogUtils.getLogger().log(Level.INFO, "Changelog added!");
        } catch (IOException e) {
            LogUtils.getLogger().log(Level.WARNING, "Couldn't add a Changelog file:" + e.getMessage());
            LogUtils.logThrowable(e);
        }

    }

    private String convertObjective(final String obj) {
        final StringBuilder builder = new StringBuilder();
        for (final String part : obj.split(" ")) {
            if (part.startsWith("tag:")) {
                builder.append("label:" + part.substring(4));
            } else {
                builder.append(part);
            }
            builder.append(' ');
        }
        return builder.toString().trim();
    }

    /**
     * Deprecated config handler, used only for configuration updating process
     *
     * @author Jakub Sapalski
     */
    private class ConfigHandler {

        /**
         * Map containing accessors for every conversation.
         */
        private HashMap<String, ConfigAccessor> conversationsMap = new HashMap<>();
        /**
         * Deprecated accessor for single conversations file, used only for
         * updating configuration.
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
         * Legacy configuration handler, only used for updating purposes. Do not
         * use!!!
         */
        public ConfigHandler() {
            // put config accesors in fields
            conversations = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "conversations.yml"), "conversations.yml", AccessorType.CONVERSATION);
            objectives = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "objectives.yml"), "objectives.yml", AccessorType.OBJECTIVES);
            conditions = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "conditions.yml"), "conditions.yml", AccessorType.CONDITIONS);
            events = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "events.yml"), "events.yml", AccessorType.EVENTS);
            npcs = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "npcs.yml"), "npcs.yml", AccessorType.MAIN);
            journal = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "journal.yml"), "journal.yml", AccessorType.JOURNAL);
            items = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "items.yml"), "items.yml", AccessorType.ITEMS);
            messages = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "messages.yml"), "messages.yml", AccessorType.OTHER);
            if (new File(BetonQuest.getInstance().getDataFolder(), "conversations").exists()) {
                // put conversations accessors in the hashmap
                for (final File file : new File(BetonQuest.getInstance().getDataFolder(), "conversations").listFiles()) {
                    conversationsMap.put(file.getName().substring(0, file.getName().indexOf(".")),
                            new ConfigAccessor(file, file.getName(), AccessorType.CONVERSATION));
                }
            }
        }

        /**
         * Retireves from configuration the string at supplied path. The path
         * should follow this syntax:
         * "filename.branch.(moreBranches).branch.variable". For example getting
         * color for day in journal date would be
         * "config.journal_colors.date.day". Everything should be handled as a
         * string for simplicity's sake.
         *
         * @param rawPath path for the variable
         * @return the String object representing requested variable
         */
        @SuppressWarnings("unused")
        public String getString(final String rawPath) {

            // get parts of path
            final String[] parts = rawPath.split("\\.");
            final String first = parts[0];
            final String path = rawPath.substring(first.length() + 1);
            String object;
            // for every possible file try to access the path and return String
            // object
            switch (first) {
                case "config":
                    object = BetonQuest.getInstance().getConfig().getString(path);
                    if (object == null) {
                        // if object is null then there is no such variable at
                        // specified path
                        LogUtils.getLogger().log(Level.FINE, "Error while accessing path: " + rawPath);
                    }
                    return object;
                case "conversations":
                    object = null;
                    // conversations should be handled with one more level, as they
                    // are in
                    // multiple files
                    final String conversationID = path.split("\\.")[0];
                    final String rest = path.substring(path.indexOf(".") + 1);
                    if (conversationsMap.get(conversationID) != null) {
                        object = conversationsMap.get(conversationID).getConfig().getString(rest);
                    }
                    if (object == null) {
                        LogUtils.getLogger().log(Level.FINE, "Error while accessing path: " + rawPath);
                    }
                    return object;
                case "objectives":
                    object = objectives.getConfig().getString(path);
                    if (object == null) {
                        LogUtils.getLogger().log(Level.FINE, "Error while accessing path: " + rawPath);
                    }
                    return object;
                case "conditions":
                    object = conditions.getConfig().getString(path);
                    if (object == null) {
                        LogUtils.getLogger().log(Level.FINE, "Error while accessing path: " + rawPath);
                    }
                    return object;
                case "events":
                    object = events.getConfig().getString(path);
                    if (object == null) {
                        LogUtils.getLogger().log(Level.FINE, "Error while accessing path: " + rawPath);
                    }
                    return object;
                case "messages":
                    object = messages.getConfig().getString(path);
                    if (object == null) {
                        LogUtils.getLogger().log(Level.FINE, "Error while accessing path: " + rawPath);
                    }
                    return object;
                case "npcs":
                    object = npcs.getConfig().getString(path);
                    return object;
                case "journal":
                    object = journal.getConfig().getString(path);
                    if (object == null) {
                        LogUtils.getLogger().log(Level.FINE, "Error while accessing path: " + rawPath);
                    }
                    return object;
                case "items":
                    object = items.getConfig().getString(path);
                    if (object == null) {
                        LogUtils.getLogger().log(Level.FINE, "Error while accessing path: " + rawPath);
                    }
                    return object;
                default:
                    LogUtils.getLogger().log(Level.FINE, "Fatal error while accessing path: " + rawPath + " (there is no such file)");
                    return null;
            }
        }

        /**
         * Retrieves a map containing all config accessors. Should be used for
         * more advanced tasks than simply getting a String. Note that
         * conversations are not included in this map. See
         * {@link #getConversations() getConversations} method for that.
         * Conversations accessor included in this map is just a deprecated old
         * conversations file. The same situation is with unused objectives
         * accessor.
         *
         * @return HashMap containing all config accessors
         */
        public HashMap<String, ConfigAccessor> getConfigs() {
            final HashMap<String, ConfigAccessor> map = new HashMap<>();
            map.put("conversations", conversations);
            map.put("conditions", conditions);
            map.put("events", events);
            map.put("objectives", objectives);
            map.put("journal", journal);
            map.put("messages", messages);
            map.put("npcs", npcs);
            map.put("items", items);
            return map;
        }

        /**
         * Retrieves map containing all conversation accessors.
         *
         * @return HashMap containing conversation accessors
         */
        public HashMap<String, ConfigAccessor> getConversations() {
            return conversationsMap;
        }
    }
}
