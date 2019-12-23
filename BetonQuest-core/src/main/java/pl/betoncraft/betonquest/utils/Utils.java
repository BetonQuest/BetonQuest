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
package pl.betoncraft.betonquest.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigAccessor;
import pl.betoncraft.betonquest.config.ConfigAccessor.AccessorType;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.config.Zipper;
import pl.betoncraft.betonquest.database.Connector;
import pl.betoncraft.betonquest.database.Connector.QueryType;
import pl.betoncraft.betonquest.database.Connector.UpdateType;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.database.Database;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various utilities.
 *
 * @author Jakub Sapalski
 */
public class Utils {

    /**
     * Does a full configuration backup.
     */
    public static void backup() {
        LogUtils.getLogger().log(Level.INFO, "Backing up!");
        long time = new Date().getTime();
        BetonQuest instance = BetonQuest.getInstance();
        if (!backupDatabase(new File(instance.getDataFolder(), "database-backup.yml"))) {
            LogUtils.getLogger().log(Level.WARNING, "There was an error during backing up the database! This does not affect"
                    + " the configuration backup, nor damage your database. You should backup"
                    + " the database maually if you want to be extra safe, but it's not necessary if"
                    + " you don't want to downgrade later.");
        }
        // create backups folder if it does not exist
        File backupFolder = new File(instance.getDataFolder(), "backups");
        if (!backupFolder.isDirectory()) {
            backupFolder.mkdir();
        }
        // zip all the files
        String outputPath = backupFolder.getAbsolutePath() + File.separator + "backup-"
                + instance.getConfig().getString("version", null);
        new Zipper(instance.getDataFolder().getAbsolutePath(), outputPath);
        // delete database backup so it doesn't make a mess later on
        new File(instance.getDataFolder(), "database-backup.yml").delete();
        // done
        LogUtils.getLogger().log(Level.FINE, "Done in " + (new Date().getTime() - time) + "ms");
        LogUtils.getLogger().log(Level.INFO, "Done, you can find the backup in \"backups\" directory.");
    }

    /**
     * Backs the database up to a specified .yml file (it should not exist)
     *
     * @param databaseBackupFile non-existent file where the database should be dumped
     * @return true if the backup was successful, false if there was an error
     */
    public static boolean backupDatabase(File databaseBackupFile) {
        BetonQuest instance = BetonQuest.getInstance();
        try {
            boolean done = true;
            // prepare the config file
            databaseBackupFile.createNewFile();
            ConfigAccessor accessor = new ConfigAccessor(databaseBackupFile, databaseBackupFile.getName(), AccessorType.OTHER);
            FileConfiguration config = accessor.getConfig();
            // prepare the database and map
            HashMap<String, ResultSet> map = new HashMap<>();
            String[] tables = new String[]{"objectives", "tags", "points", "journals", "player"};
            // open database connection
            Connector database = new Connector();
            // load resultsets into the map
            for (String table : tables) {
                LogUtils.getLogger().log(Level.FINE, "Loading " + table);
                String enumName = ("LOAD_ALL_" + table).toUpperCase();
                map.put(table, database.querySQL(QueryType.valueOf(enumName), new String[]{}));
            }
            // extract data from resultsets into the config file
            for (String key : map.keySet()) {
                LogUtils.getLogger().log(Level.FINE, "Saving " + key + " to the backup file");
                // prepare resultset and meta
                ResultSet res = map.get(key);
                ResultSetMetaData rsmd = res.getMetaData();
                // get the list of column names
                List<String> columns = new ArrayList<>();
                int columnCount = rsmd.getColumnCount();
                LogUtils.getLogger().log(Level.FINE, "  There are " + columnCount + " columns in this ResultSet");
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String columnName = rsmd.getColumnName(i);
                    LogUtils.getLogger().log(Level.FINE, "    Adding column " + columnName);
                    columns.add(columnName);
                }
                // counter for counting rows
                int counter = 0;
                while (res.next()) {
                    // for each column add a value to a config
                    for (String columnName : columns) {
                        try {
                            String value = res.getString(columnName);
                            config.set(key + "." + counter + "." + columnName, value);
                        } catch (SQLException e) {
                            LogUtils.getLogger().log(Level.WARNING, "Could not read SQL: " + e.getMessage());
                            LogUtils.logThrowable(e);
                            done = false;
                            // do nothing, as there can be nothing done
                            // error while loading the string means the
                            // database entry is broken
                        }
                    }
                    counter++;
                }
                LogUtils.getLogger().log(Level.FINE, "  Saved " + (counter + 1) + " rows");
            }
            // save the config at the end
            accessor.saveConfig();
            return done;
        } catch (IOException | SQLException e) {
            LogUtils.getLogger().log(Level.WARNING, "There was an error during database backup: " + e.getMessage());
            LogUtils.logThrowable(e);
            File brokenFile = new File(instance.getDataFolder(), "database-backup.yml");
            if (brokenFile.exists()) {
                brokenFile.delete();
            }
            return false;
        }
    }

    /**
     * Converts string to list of pages for a book.
     *
     * @param string text to convert
     * @return the list of pages for a book
     */
    public static List<String> pagesFromString(String string) {
        List<String> pages = new ArrayList<>();
        String[] bigPages = string.split("\\|");
        for (String bigPage : bigPages) {
            if (Config.getString("config.journal.lines_per_page") != null) {
                final int chars_per_line = Integer.parseInt(Config.getString("config.journal.chars_per_line"));
                final int lines_per_page = Integer.parseInt(Config.getString("config.journal.lines_per_page"));
                StringBuilder page = new StringBuilder();
                int lines = 0;
                for (String line : bigPage.split("((?<!\\\\)\\\\n|\n)")) {
                    StringBuilder line_builder = new StringBuilder();
                    int line__length = line.replaceAll("[&§][A-Ra-r0-9]", "").replaceAll("((?<!\\\\)\\\\n|\n)", "").length();
                    if (line__length <= chars_per_line) {
                        if (++lines > lines_per_page) {
                            pages.add(page.toString());
                            lines = 1;
                            page = new StringBuilder();
                        }
                        page.append(line).append('\n');
                        continue;
                    }
                    for (String word : line.split(" ")) {
                        int word_length = word.replaceAll("[&§][A-Ra-r0-9]", "").replaceAll("((?<!\\\\)\\\\n|\n)", "").length();
                        if (line_builder.length() + word_length > chars_per_line) {
                            if (++lines > lines_per_page) {
                                pages.add(page.toString());
                                lines = 1;
                                page = new StringBuilder();
                            }
                            page.append(line_builder.toString().trim()).append("\n");
                            line_builder = new StringBuilder();
                        }
                        line_builder.append(word).append(' ');
                    }
                    if (++lines > lines_per_page) {
                        pages.add(page.toString());
                        lines = 1;
                        page = new StringBuilder();
                    }
                    page.append(line_builder.toString().trim()).append('\n');
                }
                if (page.length() != 0) pages.add(page.toString());
            } else {
                final int chars_per_page = Integer.parseInt(Config.getString("config.journal.chars_per_page"));
                StringBuilder page = new StringBuilder();
                for (String word : bigPage.split(" ")) {
                    if (page.length() + word.length() + 1 > chars_per_page) {
                        pages.add(page.toString().trim());
                        page = new StringBuilder();
                    }
                    page.append(word + " ");
                }
                pages.add(page.toString().trim().replaceAll("(?<!\\\\)\\\\n", "\n"));
            }
        }
        return pages;
    }

    /**
     * If the database backup file exists, loads it into the database.
     */
    public static void loadDatabaseFromBackup() {
        boolean isOldDatabaseBackedUP = false;
        String filename = null;
        try {
            BetonQuest instance = BetonQuest.getInstance();
            File file = new File(instance.getDataFolder(), "database-backup.yml");
            // if the backup doesn't exist then there is nothing to load, return
            if (!file.exists()) {
                return;
            }
            LogUtils.getLogger().log(Level.INFO, "Loading database backup!");
            // backup the database
            File backupFolder = new File(instance.getDataFolder(), "backups");
            if (!backupFolder.isDirectory()) {
                backupFolder.mkdirs();
            }
            int i = 0;
            while (new File(backupFolder, "old-database-" + i + ".yml").exists()) {
                i++;
            }
            filename = "old-database-" + i + ".yml";
            LogUtils.getLogger().log(Level.INFO, "Backing up old database!");
            if (!(isOldDatabaseBackedUP = backupDatabase(new File(backupFolder, filename)))) {
                LogUtils.getLogger().log(Level.WARNING, "There was an error during old database backup process. This means that"
                        + " if the plugin loaded new database (from backup), the old one would be lost "
                        + "forever. Because of that the loading of backup was aborted!");
                return;
            }
            ConfigAccessor accessor = new ConfigAccessor(file, "database-backup.yml", AccessorType.OTHER);
            FileConfiguration config = accessor.getConfig();
            Database database = instance.getDB();
            // create tables if they don't exist, so we can be 100% sure
            // that we can drop them without an error (should've been done
            // in a different way...)
            database.createTables(instance.isMySQLUsed());
            // drop all tables
            Connector con = new Connector();
            con.updateSQL(UpdateType.DROP_OBJECTIVES, new String[]{});
            con.updateSQL(UpdateType.DROP_TAGS, new String[]{});
            con.updateSQL(UpdateType.DROP_POINTS, new String[]{});
            con.updateSQL(UpdateType.DROP_JOURNALS, new String[]{});
            con.updateSQL(UpdateType.DROP_PLAYER, new String[]{});
            // create new tables
            database.createTables(instance.isMySQLUsed());
            // load objectives
            ConfigurationSection objectives = config.getConfigurationSection("objectives");
            if (objectives != null)
                for (String key : objectives.getKeys(false)) {
                    con.updateSQL(UpdateType.INSERT_OBJECTIVE,
                            new String[]{objectives.getString(key + ".id"), objectives.getString(key + ".playerID"),
                                    objectives.getString(key + ".objective"),
                                    objectives.getString(key + ".instructions"),});
                }
            // load tags
            ConfigurationSection tags = config.getConfigurationSection("tags");
            if (tags != null)
                for (String key : tags.getKeys(false)) {
                    con.updateSQL(UpdateType.INSERT_TAG, new String[]{tags.getString(key + ".id"),
                            tags.getString(key + ".playerID"), tags.getString(key + ".tag"),});
                }
            // load points
            ConfigurationSection points = config.getConfigurationSection("points");
            if (points != null)
                for (String key : points.getKeys(false)) {
                    con.updateSQL(UpdateType.INSERT_POINT,
                            new String[]{points.getString(key + ".id"), points.getString(key + ".playerID"),
                                    points.getString(key + ".category"), points.getString(key + ".count"),});
                }
            // load journals
            ConfigurationSection journals = config.getConfigurationSection("journals");
            if (journals != null)
                for (String key : journals.getKeys(false)) {
                    con.updateSQL(UpdateType.INSERT_JOURNAL,
                            new String[]{journals.getString(key + ".id"), journals.getString(key + ".playerID"),
                                    journals.getString(key + ".pointer"), journals.getString(key + ".date"),});
                }
            // load backpack
            ConfigurationSection backpack = config.getConfigurationSection("backpack");
            if (backpack != null)
                for (String key : backpack.getKeys(false)) {
                    con.updateSQL(UpdateType.INSERT_BACKPACK,
                            new String[]{backpack.getString(key + ".id"), backpack.getString(key + ".playerID"),
                                    backpack.getString(key + ".instruction"), backpack.getString(key + ".amount"),});
                }
            // load player
            ConfigurationSection player = config.getConfigurationSection("player");
            if (player != null)
                for (String key : player.getKeys(false)) {
                    con.updateSQL(UpdateType.INSERT_PLAYER,
                            new String[]{player.getString(key + ".id"), player.getString(key + ".playerID"),
                                    player.getString(key + ".language"), player.getString(key + ".conversation")});
                }
            // delete backup file so it doesn't get loaded again
            file.delete();
        } catch (Exception e) {
            if (isOldDatabaseBackedUP) {
                LogUtils.getLogger().log(Level.WARNING, "Your database probably got corrupted, sorry for that :( The good news"
                        + " is that you have a backup of your old database, you can find it in backups"
                        + " folder, named as " + filename + ". You can try to use it to load the "
                        + "backup, but it will probably have the same effect.");
            } else {
                LogUtils.getLogger().log(Level.WARNING, "There was an error during database loading, but fortunatelly the "
                        + "original database wasn't even touched yet. You can try to load the backup "
                        + "again, and if the problem persists you should contact the developer to find"
                        + " a solution.");
            }
            LogUtils.logThrowableReport(e);
        }
    }

    /**
     * Checks if the ItemStack is a quest item
     *
     * @param item ItemStack to check
     * @return true if the supplied ItemStack is a quest item, false otherwise
     */
    public static boolean isQuestItem(ItemStack item) {
        if (item == null) {
            return false;
        }
        return item.hasItemMeta() && item.getItemMeta().hasLore()
                && item.getItemMeta().getLore().contains(Config.getMessage(Config.getLanguage(), "quest_item"));
    }

    public static ArrayList<String> getParty(String playerID, double range, String pack, ConditionID[] conditions) {
        final ArrayList<String> list = new ArrayList<>();
        Player player = PlayerConverter.getPlayer(playerID);
        Location loc = player.getLocation();
        double squared = range * range;
        for (Player otherPlayer : loc.getWorld().getPlayers()) {
            if (otherPlayer.getLocation().distanceSquared(loc) <= squared) {
                String otherPlayerID = PlayerConverter.getID(otherPlayer);
                boolean meets = true;
                for (ConditionID condition : conditions) {
                    if (!BetonQuest.condition(otherPlayerID, condition)) {
                        meets = false;
                        break;
                    }
                }
                if (meets) {
                    list.add(otherPlayerID);
                }
            }
        }
        return list;
    }

    /**
     * Inserts a package before this string if there is no package, or does
     * nothing if the package is already there.
     *
     * @param pack   the package
     * @param string ID of event/condition/objective/item etc.
     * @return full ID with package prefix
     */
    public static String addPackage(ConfigPackage pack, String string) {
        if (string.contains(".")) {
            return string;
        } else {
            return pack.getName() + "." + string;
        }
    }

    /**
     * Parses the string as RGB or as DyeColor and returns it as Color.
     *
     * @param string string to parse as a Color
     * @return the Color (never null)
     * @throws InstructionParseException when something goes wrong
     */
    public static Color getColor(String string) throws InstructionParseException {
        if (string == null || string.isEmpty()) {
            throw new InstructionParseException("Color is not specified");
        }
        try {
            return Color.fromRGB(Integer.parseInt(string));
        } catch (NumberFormatException e1) {
            LogUtils.logThrowableIgnore(e1);
            // string is not a decimal number
            try {
                return Color.fromRGB(Integer.parseInt(string.replace("#", ""), 16));
            } catch (NumberFormatException e2) {
                LogUtils.logThrowableIgnore(e2);
                // string is not a hexadecimal number, try dye color
                try {
                    return DyeColor.valueOf(string.trim().toUpperCase().replace(' ', '_')).getColor();
                } catch (IllegalArgumentException e3) {
                    // this was not a dye color name
                    throw new InstructionParseException("Dye color does not exist: " + string, e3);
                }
            }
        } catch (IllegalArgumentException e) {
            // string was a number, but incorrect
            throw new InstructionParseException("Incorrect RGB code: " + string, e);
        }
    }

    /**
     * Resets any color resets to def. Also ensures any new lines copy the colours and format from the previous line
     *
     * @param pages multiple pages to process
     * @param def   default color code to use instead of resetting; use null for regular reset code
     * @return the colorful pages ready to split into multiple lines
     */
    public static List<String> multiLineColorCodes(List<String> pages, String def) {
        String lastCodes = "";
        ListIterator<String> i = pages.listIterator();
        List<String> result = new ArrayList<>();

        while (i.hasNext()) {
            String line = i.next();
            result.add(lastCodes + replaceReset(line, def));
            lastCodes = LocalChatPaginator.getLastColors(line);
        }

        return result;
    }

    /**
     * Replace resets with colorcode
     */
    public static String replaceReset(String string, String color) {
        return string.replace(ChatColor.RESET.toString(), ChatColor.RESET + color);
    }

    /**
     * Formats the string by replacing {@code \\n} with {@code \n} and resolving alternate color codes with {@code &}
     * <p>
     * {@code format(string, false, false)} will return the string with no formatting done
     *
     * @param string     the input string
     * @param colorCodes if alternate color codes should be resolved
     * @param lineBreaks if {@code \\n} should be replaced with {@code \n}
     * @return a formatted version of the input string
     */
    public static String format(String string, boolean colorCodes, boolean lineBreaks) {
        if (colorCodes) string = string.replaceAll("&(?=[A-Ra-r0-9])", "§");
        if (lineBreaks) string = string.replaceAll("(?<!\\\\)\\\\n", "\n");
        return string;
    }

    /**
     * Formats the string by replacing {@code \\n} with {@code \n} and resolving alternate color codes with {@code &}
     *
     * @param string the input string
     * @return a formatted version of the input string
     */
    public static String format(String string) {
        return format(string, true, true);
    }

    /**
     * Split a string by white space, except if between quotes
     */
    public static String[] split(String string) {
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("(?:(?:(\\S*)(?:\")([^\"]*?)(?:\"))|(\\S+))\\s*").matcher(string);
        while (m.find()) {
            if (m.group(3) != null) {
                list.add(m.group(3));
            } else {
                list.add(m.group(1) + m.group(2));
            }
        }
        return list.toArray(new String[0]);
    }
}
