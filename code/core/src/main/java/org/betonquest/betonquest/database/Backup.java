package org.betonquest.betonquest.database;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is responsible for backing up and restoring the database.
 */
public final class Backup {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuest.getInstance().getLoggerFactory().create(Backup.class);

    /**
     * Private constructor to hide the implicit public one.
     */
    private Backup() {
    }

    /**
     * Backs the database up to a specified .yml file (it should not exist)
     *
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     * @param databaseBackupFile    non-existent file where the database should be dumped
     * @return true if the backup was successful, false if there was an error
     */
    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    public static boolean backupDatabase(final ConfigAccessorFactory configAccessorFactory, final File databaseBackupFile) {
        final BetonQuest instance = BetonQuest.getInstance();
        try {
            if (!databaseBackupFile.createNewFile()) {
                LOG.warn("Could not create the backup file!");
                return false;
            }
            boolean done = true;
            final FileConfigAccessor config = configAccessorFactory.create(databaseBackupFile);
            // prepare the database and map
            final Map<String, ResultSet> map = new HashMap<>();
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
                    final ResultSetMetaData result = res.getMetaData();
                    // get the list of column names
                    final List<String> columns = new ArrayList<>();
                    final int columnCount = result.getColumnCount();
                    LOG.debug("  There are " + columnCount + " columns in this ResultSet");
                    for (int i = 1; i <= result.getColumnCount(); i++) {
                        final String columnName = result.getColumnName(i);
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
            config.save();
            return done;
        } catch (final IOException | SQLException | InvalidConfigurationException e) {
            LOG.warn("There was an error during database backup: " + e.getMessage(), e);
            final File brokenFile = new File(instance.getDataFolder(), "database-backup.yml");
            if (brokenFile.exists() && !brokenFile.delete()) {
                LOG.warn("Could not delete the broken backup file!");
            }
            return false;
        }
    }

    /**
     * If the database backup file exists, loads it into the database.
     *
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity", "PMD.NcssCount", "PMD.AvoidDuplicateLiterals"})
    public static void loadDatabaseFromBackup(final ConfigAccessorFactory configAccessorFactory) {
        final BetonQuest instance = BetonQuest.getInstance();
        final File file = new File(instance.getDataFolder(), "database-backup.yml");
        // if the backup doesn't exist then there is nothing to load, return
        if (!file.exists()) {
            return;
        }
        LOG.info("Loading database backup!");
        // backup the database
        final File backupFolder = new File(instance.getDataFolder(), "Backups");
        if (!backupFolder.isDirectory() && !backupFolder.mkdirs()) {
            LOG.warn("Could not create the backup folder!");
            return;
        }
        int backupNumber = 0;
        while (new File(backupFolder, "old-database-" + backupNumber + ".yml").exists()) {
            backupNumber++;
        }
        final String filename = "old-database-" + backupNumber + ".yml";
        LOG.info("Backing up old database!");
        if (!backupDatabase(configAccessorFactory, new File(backupFolder, filename))) {
            LOG.warn("There was an error during old database backup process. This means that"
                    + " if the plugin loaded new database (from backup), the old one would be lost "
                    + "forever. Because of that the loading of backup was aborted!");
            return;
        }
        final ConfigAccessor config;
        try {
            config = configAccessorFactory.create(file);
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            LOG.warn(e.getMessage(), e);
            return;
        }
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
        con.updateSQL(UpdateType.DROP_BACKPACK);
        con.updateSQL(UpdateType.DROP_GLOBAL_POINTS);
        con.updateSQL(UpdateType.DROP_GLOBAL_TAGS);
        con.updateSQL(UpdateType.DROP_MIRGATION);
        con.updateSQL(UpdateType.DROP_PLAYER_PROFILE);
        con.updateSQL(UpdateType.DROP_PLAYER);
        con.updateSQL(UpdateType.DROP_PROFILE);
        // create new tables
        database.createTables();

        final ConfigurationSection profile = config.getConfigurationSection("profile");
        if (profile != null) {
            for (final String key : profile.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_PROFILE,
                        profile.getString(key + ".profileID"));
            }
        }
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
        final ConfigurationSection playerProfile = config.getConfigurationSection("player_profile");
        if (playerProfile != null) {
            for (final String key : playerProfile.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_PLAYER_PROFILE,
                        playerProfile.getString(key + ".playerID"),
                        playerProfile.getString(key + ".profileID"),
                        playerProfile.getString(key + ".name"));
            }
        }
        final ConfigurationSection objectives = config.getConfigurationSection("objectives");
        if (objectives != null) {
            for (final String key : objectives.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_OBJECTIVE,
                        objectives.getString(key + ".profileID"),
                        objectives.getString(key + ".objective"),
                        objectives.getString(key + ".instructions"));
            }
        }
        final ConfigurationSection tags = config.getConfigurationSection("tags");
        if (tags != null) {
            for (final String key : tags.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_TAG,
                        tags.getString(key + ".profileID"),
                        tags.getString(key + ".tag"));
            }
        }
        final ConfigurationSection points = config.getConfigurationSection("points");
        if (points != null) {
            for (final String key : points.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_POINT,
                        points.getString(key + ".profileID"),
                        points.getString(key + ".category"),
                        points.getString(key + ".count"));
            }
        }
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
        final ConfigurationSection backpack = config.getConfigurationSection("backpack");
        if (backpack != null) {
            for (final String key : backpack.getKeys(false)) {
                con.updateSQL(UpdateType.INSERT_BACKPACK,
                        backpack.getString(key + ".id"),
                        backpack.getString(key + ".profileID"),
                        backpack.getString(key + ".serialized"),
                        backpack.getString(key + ".amount"));
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
        if (!file.delete()) {
            LOG.warn("Could not delete the backup file!");
        }
    }
}
