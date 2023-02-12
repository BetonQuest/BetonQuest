package org.betonquest.betonquest.utils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.Database;
import org.betonquest.betonquest.database.QueryType;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.modules.config.Zipper;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various utilities.
 */
@SuppressWarnings({"PMD.ClassNamingConventions", "PMD.GodClass", "PMD.CommentRequired", "PMD.AvoidDuplicateLiterals",
        "PMD.TooManyMethods", "PMD.CyclomaticComplexity"})
@CustomLog
public final class Utils {

    private Utils() {
    }

    /**
     * Does a full configuration backup.
     */
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public static void backup() {
        LOG.info("Backing up!");
        final long time = new Date().getTime();
        final BetonQuest instance = BetonQuest.getInstance();
        if (!backupDatabase(new File(instance.getDataFolder(), "database-backup.yml"))) {
            LOG.warn("There was an error during backing up the database! This does not affect"
                    + " the configuration backup, nor damage your database. You should backup"
                    + " the database maually if you want to be extra safe, but it's not necessary if"
                    + " you don't want to downgrade later.");
        }
        // create Backups folder if it does not exist
        final File backupFolder = new File(instance.getDataFolder(), "Backups");
        if (!backupFolder.isDirectory()) {
            backupFolder.mkdir();
        }
        // zip all the files
        final String outputPath = backupFolder.getAbsolutePath() + File.separator + "backup-"
                + instance.getPluginConfig().getString("version", null);

        Zipper.zip(instance.getDataFolder(), outputPath, "^backup.*", "^database\\.db$", "^changelog\\.txt$", "^logs$");
        // delete database backup so it doesn't make a mess later on
        new File(instance.getDataFolder(), "database-backup.yml").delete();
        // done
        LOG.debug("Done in " + (new Date().getTime() - time) + "ms");
        LOG.info("Done, you can find the backup in 'Backups' directory.");
    }

    /**
     * Backs the database up to a specified .yml file (it should not exist)
     *
     * @param databaseBackupFile non-existent file where the database should be dumped
     * @return true if the backup was successful, false if there was an error
     */
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    @SuppressWarnings({"PMD.CognitiveComplexity"})
    public static boolean backupDatabase(final File databaseBackupFile) {
        final BetonQuest instance = BetonQuest.getInstance();
        try {
            boolean done = true;
            // prepare the config file
            databaseBackupFile.createNewFile();
            final ConfigAccessor accessor = ConfigAccessor.create(databaseBackupFile);
            final FileConfiguration config = accessor.getConfig();
            // prepare the database and map
            final HashMap<String, ResultSet> map = new HashMap<>();
            final String[] tables = {"objectives", "tags", "points", "journals", "player", "backpack", "global_points",
                    "global_tags", "migration", "player_profile", "profile"};
            // open database connection
            final Connector database = new Connector();
            // load resultsets into the map
            for (final String table : tables) {
                LOG.debug("Loading " + table);
                final String enumName = ("LOAD_ALL_" + table).toUpperCase(Locale.ROOT);
                map.put(table, database.querySQL(QueryType.valueOf(enumName)));
            }
            // extract data from resultsets into the config file
            for (final Map.Entry<String, ResultSet> entry : map.entrySet()) {
                LOG.debug("Saving " + entry.getKey() + " to the backup file");
                // prepare resultset and meta
                try (ResultSet res = entry.getValue()) {
                    final ResultSetMetaData rsmd = res.getMetaData();
                    // get the list of column names
                    final List<String> columns = new ArrayList<>();
                    final int columnCount = rsmd.getColumnCount();
                    LOG.debug("  There are " + columnCount + " columns in this ResultSet");
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        final String columnName = rsmd.getColumnName(i);
                        LOG.debug("    Adding column " + columnName);
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
                            } catch (final SQLException e) {
                                LOG.warn("Could not read SQL: " + e.getMessage(), e);
                                done = false;
                                // do nothing, as there can be nothing done
                                // error while loading the string means the
                                // database entry is broken
                            }
                        }
                        counter++;
                    }
                    LOG.debug("  Saved " + (counter + 1) + " rows");
                }
            }
            // save the config at the end
            accessor.save();
            return done;
        } catch (IOException | SQLException | InvalidConfigurationException e) {
            LOG.warn("There was an error during database backup: " + e.getMessage(), e);
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
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
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
                    page.append(word).append(' ');
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
                            page.append(lineBuilder.toString().trim()).append('\n');
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
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity", "PMD.NcssCount"})
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public static void loadDatabaseFromBackup() {
        final BetonQuest instance = BetonQuest.getInstance();
        final File file = new File(instance.getDataFolder(), "database-backup.yml");
        // if the backup doesn't exist then there is nothing to load, return
        if (!file.exists()) {
            return;
        }
        LOG.info("Loading database backup!");
        // backup the database
        final File backupFolder = new File(instance.getDataFolder(), "Backups");
        if (!backupFolder.isDirectory()) {
            backupFolder.mkdirs();
        }
        int backupNumber = 0;
        while (new File(backupFolder, "old-database-" + backupNumber + ".yml").exists()) {
            backupNumber++;
        }
        final String filename = "old-database-" + backupNumber + ".yml";
        LOG.info("Backing up old database!");
        if (!backupDatabase(new File(backupFolder, filename))) {
            LOG.warn("There was an error during old database backup process. This means that"
                    + " if the plugin loaded new database (from backup), the old one would be lost "
                    + "forever. Because of that the loading of backup was aborted!");
            return;
        }
        final ConfigAccessor accessor;
        try {
            accessor = ConfigAccessor.create(file);
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            LOG.warn(e.getMessage(), e);
            return;
        }
        final FileConfiguration config = accessor.getConfig();
        final Database database = instance.getDB();
        // create tables if they don't exist, so we can be 100% sure
        // that we can drop them without an error (should've been done
        // in a different way...)
        database.createTables();
        // drop all tables
        final Connector con = new Connector();
        con.updateSQL(UpdateType.DROP_OBJECTIVES);
        con.updateSQL(UpdateType.DROP_TAGS);
        con.updateSQL(UpdateType.DROP_POINTS);
        con.updateSQL(UpdateType.DROP_JOURNALS);
        con.updateSQL(UpdateType.DROP_PLAYER);
        con.updateSQL(UpdateType.DROP_BACKPACK);
        con.updateSQL(UpdateType.DROP_GLOBAL_POINTS);
        con.updateSQL(UpdateType.DROP_GLOBAL_TAGS);
        con.updateSQL(UpdateType.DROP_MIRGATION);
        con.updateSQL(UpdateType.DROP_PROFILE);
        con.updateSQL(UpdateType.DROP_PLAYER_PROFILE);
        // create new tables
        database.createTables();
        // load objectives
        final ConfigurationSection objectives = config.getConfigurationSection("objectives");
        if (objectives != null) {
            for (final String key : objectives.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_OBJECTIVE,
                        objectives.getString(key + ".profileID"),
                        objectives.getString(key + ".objective"),
                        objectives.getString(key + ".instructions"));
            }
        }
        // load tags
        final ConfigurationSection tags = config.getConfigurationSection("tags");
        if (tags != null) {
            for (final String key : tags.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_TAG,
                        tags.getString(key + ".profileID"),
                        tags.getString(key + ".tag"));
            }
        }
        // load points
        final ConfigurationSection points = config.getConfigurationSection("points");
        if (points != null) {
            for (final String key : points.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_POINT,
                        points.getString(key + ".profileID"),
                        points.getString(key + ".category"),
                        points.getString(key + ".count"));
            }
        }
        // load journals
        final ConfigurationSection journals = config.getConfigurationSection("journals");
        if (journals != null) {
            for (final String key : journals.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_JOURNAL,
                        journals.getString(key + ".id"),
                        journals.getString(key + ".profileID"),
                        journals.getString(key + ".pointer"),
                        journals.getString(key + ".date"));
            }
        }
        // load backpack
        final ConfigurationSection backpack = config.getConfigurationSection("backpack");
        if (backpack != null) {
            for (final String key : backpack.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_BACKPACK,
                        backpack.getString(key + ".id"),
                        backpack.getString(key + ".profileID"),
                        backpack.getString(key + ".instruction"),
                        backpack.getString(key + ".amount"));
            }
        }
        // load player
        final ConfigurationSection player = config.getConfigurationSection("player");
        if (player != null) {
            for (final String key : player.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_PLAYER,
                        player.getString(key + ".playerID"),
                        player.getString(key + ".active_profile"),
                        player.getString(key + ".language"),
                        player.getString(key + ".conversation"));
            }
        }
        final ConfigurationSection globalPoints = config.getConfigurationSection("global_points");
        if (globalPoints != null) {
            for (final String key : globalPoints.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_GLOBAL_POINT,
                        globalPoints.getString(key + ".category"),
                        globalPoints.getString(key + ".count"));
            }
        }
        final ConfigurationSection globalTags = config.getConfigurationSection("global_tags");
        if (globalTags != null) {
            for (final String key : globalTags.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_GLOBAL_TAG,
                        globalTags.getString(key + ".tag"));
            }
        }
        final ConfigurationSection migration = config.getConfigurationSection("migration");
        if (migration != null) {
            for (final String key : migration.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_MIGRATION,
                        migration.getString(key + ".namespace"),
                        migration.getString(key + ".migration_id"),
                        migration.getString(key + ".time"));
            }
        }
        final ConfigurationSection profile = config.getConfigurationSection("profile");
        if (profile != null) {
            for (final String key : profile.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_PROFILE,
                        profile.getString(key + ".profileID"));
            }
        }
        final ConfigurationSection playerProfile = config.getConfigurationSection("player_profile");
        if (playerProfile != null) {
            for (final String key : playerProfile.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_PLAYER_PROFILE,
                        playerProfile.getString(key + ".playerID"),
                        playerProfile.getString(key + ".profileID"),
                        playerProfile.getString(key + ".name"));
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
    public static List<OnlineProfile> getParty(final OnlineProfile onlineProfile, final double range, final String pack, final ConditionID... conditions) throws QuestRuntimeException {
        final Location loc = onlineProfile.getPlayer().getLocation();
        final double squared = range * range;

        return loc.getWorld().getPlayers().stream()
                .filter(player -> player.getLocation().distanceSquared(loc) <= squared)
                .map(PlayerConverter::getID)
                .filter(otherProfile -> BetonQuest.conditions(otherProfile, conditions))
                .toList();
    }

    /**
     * Inserts a package before this string if there is no package, or does
     * nothing if the package is already there.
     *
     * @param pack   the package
     * @param string ID of event/condition/objective/item etc.
     * @return full ID with package prefix
     */
    public static String addPackage(final QuestPackage pack, final String string) {
        if (string.contains(".")) {
            return string;
        } else {
            return pack.getQuestPath() + "." + string;
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
        } catch (final NumberFormatException e1) {
            LOG.debug("Could not parse number!", e1);
            // string is not a decimal number
            try {
                return Color.fromRGB(Integer.parseInt(string.replace("#", ""), 16));
            } catch (final NumberFormatException e2) {
                LOG.debug("Could not parse number!", e2);
                // string is not a hexadecimal number, try dye color
                try {
                    return DyeColor.valueOf(string.trim().toUpperCase(Locale.ROOT).replace(' ', '_')).getColor();
                } catch (final IllegalArgumentException e3) {
                    // this was not a dye color name
                    throw new InstructionParseException("Dye color does not exist: " + string, e3);
                }
            }
        } catch (final IllegalArgumentException e) {
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
            lastCodes = ChatColor.getLastColors(line);
        }

        return result;
    }

    /**
     * Replace resets with colorcode.
     *
     * @param string input string.
     * @param color  default color.
     * @return the formatted string.
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
     *
     * @param string the input string.
     * @return the split string.
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
