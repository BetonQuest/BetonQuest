package org.betonquest.betonquest.utils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.ConfigAccessor;
import org.betonquest.betonquest.config.ConfigAccessor.AccessorType;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.config.Zipper;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.Connector.QueryType;
import org.betonquest.betonquest.database.Connector.UpdateType;
import org.betonquest.betonquest.database.Database;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various utilities.
 */
@SuppressWarnings({"PMD.ClassNamingConventions", "PMD.GodClass", "PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
public final class Utils {

    private Utils() {
    }

    /**
     * Does a full configuration backup.
     */
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public static void backup() {
        LogUtils.getLogger().log(Level.INFO, "Backing up!");
        final long time = new Date().getTime();
        final BetonQuest instance = BetonQuest.getInstance();
        if (!backupDatabase(new File(instance.getDataFolder(), "database-backup.yml"))) {
            LogUtils.getLogger().log(Level.WARNING, "There was an error during backing up the database! This does not affect"
                    + " the configuration backup, nor damage your database. You should backup"
                    + " the database maually if you want to be extra safe, but it's not necessary if"
                    + " you don't want to downgrade later.");
        }
        // create backups folder if it does not exist
        final File backupFolder = new File(instance.getDataFolder(), "backups");
        if (!backupFolder.isDirectory()) {
            backupFolder.mkdir();
        }
        // zip all the files
        final String outputPath = backupFolder.getAbsolutePath() + File.separator + "backup-"
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
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public static boolean backupDatabase(final File databaseBackupFile) {
        final BetonQuest instance = BetonQuest.getInstance();
        try {
            boolean done = true;
            // prepare the config file
            databaseBackupFile.createNewFile();
            final ConfigAccessor accessor = new ConfigAccessor(databaseBackupFile, databaseBackupFile.getName(), AccessorType.OTHER);
            final FileConfiguration config = accessor.getConfig();
            // prepare the database and map
            final HashMap<String, ResultSet> map = new HashMap<>();
            final String[] tables = {"objectives", "tags", "points", "journals", "player"};
            // open database connection
            final Connector database = new Connector();
            // load resultsets into the map
            for (final String table : tables) {
                LogUtils.getLogger().log(Level.FINE, "Loading " + table);
                final String enumName = ("LOAD_ALL_" + table).toUpperCase(Locale.ROOT);
                map.put(table, database.querySQL(QueryType.valueOf(enumName), new String[]{}));
            }
            // extract data from resultsets into the config file
            for (final Map.Entry<String, ResultSet> entry : map.entrySet()) {
                LogUtils.getLogger().log(Level.FINE, "Saving " + entry.getKey() + " to the backup file");
                // prepare resultset and meta
                try (ResultSet res = entry.getValue()) {
                    final ResultSetMetaData rsmd = res.getMetaData();
                    // get the list of column names
                    final List<String> columns = new ArrayList<>();
                    final int columnCount = rsmd.getColumnCount();
                    LogUtils.getLogger().log(Level.FINE, "  There are " + columnCount + " columns in this ResultSet");
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        final String columnName = rsmd.getColumnName(i);
                        LogUtils.getLogger().log(Level.FINE, "    Adding column " + columnName);
                        columns.add(columnName);
                    }
                    // counter for counting rows
                    int counter = 0;
                    while (res.next()) {
                        // for each column add a value to a config
                        for (final String columnName : columns) {
                            try {
                                final String value = res.getString(columnName);
                                config.set(entry.getKey() + "." + counter + "." + columnName, value);
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
            }
            // save the config at the end
            accessor.saveConfig();
            return done;
        } catch (IOException | SQLException e) {
            LogUtils.getLogger().log(Level.WARNING, "There was an error during database backup: " + e.getMessage());
            LogUtils.logThrowable(e);
            final File brokenFile = new File(instance.getDataFolder(), "database-backup.yml");
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
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public static List<String> pagesFromString(final String string) {
        final List<String> pages = new ArrayList<>();
        final String[] bigPages = string.split("\\|");
        for (final String bigPage : bigPages) {
            if (Config.getString("config.journal.lines_per_page") == null) {
                final int charsPerPage = Integer.parseInt(Config.getString("config.journal.chars_per_page"));
                StringBuilder page = new StringBuilder();
                for (final String word : bigPage.split(" ")) {
                    if (page.length() + word.length() + 1 > charsPerPage) {
                        pages.add(page.toString().trim());
                        page = new StringBuilder();
                    }
                    page.append(word).append(" ");
                }
                pages.add(page.toString().trim().replaceAll("(?<!\\\\)\\\\n", "\n"));
            } else {
                final int charsPerLine = Integer.parseInt(Config.getString("config.journal.chars_per_line"));
                final int linesPerPage = Integer.parseInt(Config.getString("config.journal.lines_per_page"));
                StringBuilder page = new StringBuilder();
                int lines = 0;
                for (final String line : bigPage.split("((?<!\\\\)\\\\n|\n)")) {
                    StringBuilder lineBuilder = new StringBuilder();
                    final int lineLength = getStringLength(line);
                    if (lineLength <= charsPerLine) {
                        lines++;
                        if (lines > linesPerPage) {
                            pages.add(page.toString());
                            lines = 1;
                            page = new StringBuilder();
                        }
                        page.append(line).append('\n');
                        continue;
                    }
                    for (final String word : line.split(" ")) {
                        final int stringLength = getStringLength(word);
                        final int lineBuilderLength = getStringLength(lineBuilder.toString());
                        if (lineBuilderLength + stringLength > charsPerLine) {
                            lines++;
                            if (lines > linesPerPage) {
                                pages.add(page.toString());
                                lines = 1;
                                page = new StringBuilder();
                            }
                            page.append(lineBuilder.toString().trim()).append("\n");
                            lineBuilder = new StringBuilder();
                        }
                        lineBuilder.append(word).append(' ');
                    }
                    lines++;
                    if (lines > linesPerPage) {
                        pages.add(page.toString());
                        lines = 1;
                        page = new StringBuilder();
                    }
                    page.append(lineBuilder.toString().trim()).append('\n');
                }
                if (page.length() != 0) {
                    pages.add(page.toString());
                }
            }
        }
        return pages;
    }

    private static int getStringLength(final String string) {
        return string.replaceAll("[&§][A-Ra-r0-9]", "").replaceAll("((?<!\\\\)\\\\n|\n)", "").length();
    }

    /**
     * If the database backup file exists, loads it into the database.
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public static void loadDatabaseFromBackup() {
        final BetonQuest instance = BetonQuest.getInstance();
        final File file = new File(instance.getDataFolder(), "database-backup.yml");
        // if the backup doesn't exist then there is nothing to load, return
        if (!file.exists()) {
            return;
        }
        LogUtils.getLogger().log(Level.INFO, "Loading database backup!");
        // backup the database
        final File backupFolder = new File(instance.getDataFolder(), "backups");
        if (!backupFolder.isDirectory()) {
            backupFolder.mkdirs();
        }
        int backupNumber = 0;
        while (new File(backupFolder, "old-database-" + backupNumber + ".yml").exists()) {
            backupNumber++;
        }
        final String filename = "old-database-" + backupNumber + ".yml";
        LogUtils.getLogger().log(Level.INFO, "Backing up old database!");
        if (!backupDatabase(new File(backupFolder, filename))) {
            LogUtils.getLogger().log(Level.WARNING, "There was an error during old database backup process. This means that"
                    + " if the plugin loaded new database (from backup), the old one would be lost "
                    + "forever. Because of that the loading of backup was aborted!");
            return;
        }
        final ConfigAccessor accessor = new ConfigAccessor(file, "database-backup.yml", AccessorType.OTHER);
        final FileConfiguration config = accessor.getConfig();
        final Database database = instance.getDB();
        // create tables if they don't exist, so we can be 100% sure
        // that we can drop them without an error (should've been done
        // in a different way...)
        database.createTables(instance.isMySQLUsed());
        // drop all tables
        final Connector con = new Connector();
        con.updateSQL(UpdateType.DROP_OBJECTIVES);
        con.updateSQL(UpdateType.DROP_TAGS);
        con.updateSQL(UpdateType.DROP_POINTS);
        con.updateSQL(UpdateType.DROP_JOURNALS);
        con.updateSQL(UpdateType.DROP_PLAYER);
        // create new tables
        database.createTables(instance.isMySQLUsed());
        // load objectives
        final ConfigurationSection objectives = config.getConfigurationSection("objectives");
        if (objectives != null) {
            for (final String key : objectives.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_OBJECTIVE,
                        objectives.getString(key + ".id"), objectives.getString(key + ".playerID"),
                        objectives.getString(key + ".objective"),
                        objectives.getString(key + ".instructions"));
            }
        }
        // load tags
        final ConfigurationSection tags = config.getConfigurationSection("tags");
        if (tags != null) {
            for (final String key : tags.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_TAG, tags.getString(key + ".id"),
                        tags.getString(key + ".playerID"), tags.getString(key + ".tag"));
            }
        }
        // load points
        final ConfigurationSection points = config.getConfigurationSection("points");
        if (points != null) {
            for (final String key : points.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_POINT,
                        points.getString(key + ".id"), points.getString(key + ".playerID"),
                        points.getString(key + ".category"), points.getString(key + ".count"));
            }
        }
        // load journals
        final ConfigurationSection journals = config.getConfigurationSection("journals");
        if (journals != null) {
            for (final String key : journals.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_JOURNAL,
                        journals.getString(key + ".id"), journals.getString(key + ".playerID"),
                        journals.getString(key + ".pointer"), journals.getString(key + ".date"));
            }
        }
        // load backpack
        final ConfigurationSection backpack = config.getConfigurationSection("backpack");
        if (backpack != null) {
            for (final String key : backpack.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_BACKPACK,
                        backpack.getString(key + ".id"), backpack.getString(key + ".playerID"),
                        backpack.getString(key + ".instruction"), backpack.getString(key + ".amount"));
            }
        }
        // load player
        final ConfigurationSection player = config.getConfigurationSection("player");
        if (player != null) {
            for (final String key : player.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_PLAYER,
                        player.getString(key + ".id"), player.getString(key + ".playerID"),
                        player.getString(key + ".language"), player.getString(key + ".conversation"));
            }
        }
        // delete backup file so it doesn't get loaded again
        file.delete();
    }

    /**
     * Checks if the ItemStack is a quest item
     *
     * @param item ItemStack to check
     * @return true if the supplied ItemStack is a quest item, false otherwise
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public static boolean isQuestItem(final ItemStack item) {
        if (item == null) {
            return false;
        }
        return item.hasItemMeta() && item.getItemMeta().hasLore()
                && item.getItemMeta().getLore().contains(Config.getMessage(Config.getLanguage(), "quest_item"));
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public static List<String> getParty(final String playerID, final double range, final String pack, final ConditionID... conditions) {
        final List<String> list = new ArrayList<>();
        final Player player = PlayerConverter.getPlayer(playerID);
        final Location loc = player.getLocation();
        final double squared = range * range;
        for (final Player otherPlayer : loc.getWorld().getPlayers()) {
            if (otherPlayer.getLocation().distanceSquared(loc) <= squared) {
                final String otherPlayerID = PlayerConverter.getID(otherPlayer);
                if (BetonQuest.conditions(otherPlayerID, conditions)) {
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
    public static String addPackage(final ConfigPackage pack, final String string) {
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
    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.CyclomaticComplexity"})
    public static Color getColor(final String string) throws InstructionParseException {
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
                    return DyeColor.valueOf(string.trim().toUpperCase(Locale.ROOT).replace(' ', '_')).getColor();
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
    public static List<String> multiLineColorCodes(final List<String> pages, final String def) {
        String lastCodes = "";
        final ListIterator<String> iterator = pages.listIterator();
        final List<String> result = new ArrayList<>();

        while (iterator.hasNext()) {
            final String line = iterator.next();
            result.add(lastCodes + replaceReset(line, def));
            lastCodes = LocalChatPaginator.getLastColors(line);
        }

        return result;
    }

    /**
     * Replace resets with colorcode
     */
    public static String replaceReset(final String string, final String color) {
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
    public static String format(final String string, final boolean colorCodes, final boolean lineBreaks) {
        String input = string;
        if (colorCodes) {
            input = input.replaceAll("&(?=[A-Ra-r0-9])", "§");
        }
        if (lineBreaks) {
            input = input.replaceAll("(?<!\\\\)\\\\n", "\n");
        }
        return input;
    }

    /**
     * Formats the string by replacing {@code \\n} with {@code \n} and resolving alternate color codes with {@code &}
     *
     * @param string the input string
     * @return a formatted version of the input string
     */
    public static String format(final String string) {
        return format(string, true, true);
    }

    /**
     * Split a string by white space, except if between quotes
     */
    public static String[] split(final String string) {
        final List<String> list = new ArrayList<>();
        final Matcher matcher = Pattern.compile("(?:(?:(\\S*)(?:\")([^\"]*?)(?:\"))|(\\S+))\\s*").matcher(string);
        while (matcher.find()) {
            if (matcher.group(3) == null) {
                list.add(matcher.group(1) + matcher.group(2));
            } else {
                list.add(matcher.group(3));
            }
        }
        return list.toArray(new String[0]);
    }
}
