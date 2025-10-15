package org.betonquest.betonquest.database;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.Zipper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

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
import java.util.Locale;
import java.util.Map;

/**
 * This class is responsible for backing up and restoring the database.
 */
public final class Backup {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Factory that will be used to create {@link ConfigAccessor}s.
     */
    private final ConfigAccessorFactory configAccessorFactory;

    /**
     * Folder to back up its contents.
     */
    private final File root;

    /**
     * Connector to access database.
     */
    private final Connector con;

    /**
     * Single file to store a database backup to.
     */
    private final File databaseBackupFile;

    /**
     * Folder to store a full backup to.
     */
    private final File backupFolder;

    /**
     * Creates a new Object to store and load backups.
     * It will use the "database-backup.yml" for a single database backup and the "Backups" folder for a full backup
     * inside the given root folder.
     *
     * @param log                   the custom {@link BetonQuestLogger} instance for this class
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     * @param root                  the directory to back up and load to
     * @param connector             the connector used for database access
     */
    public Backup(final BetonQuestLogger log, final ConfigAccessorFactory configAccessorFactory, final File root,
                  final Connector connector) {
        this(log, configAccessorFactory, root, connector, new File(root, "database-backup.yml"), new File(root, "Backups"));
    }

    /**
     * Creates a new Object to store and load backups.
     *
     * @param log                   the custom {@link BetonQuestLogger} instance for this class
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     * @param root                  the directory to back up and load to
     * @param connector             the connector used for database access
     * @param databaseBackupFile    the file to store/load a single database backup
     * @param backupFolder          the folder to store/load the full backup
     */
    public Backup(final BetonQuestLogger log, final ConfigAccessorFactory configAccessorFactory, final File root,
                  final Connector connector, final File databaseBackupFile, final File backupFolder) {
        this.log = log;
        this.configAccessorFactory = configAccessorFactory;
        this.root = root;
        this.con = connector;
        this.databaseBackupFile = databaseBackupFile;
        this.backupFolder = backupFolder;
    }

    /**
     * Does a full configuration backup.
     * The backup folder and file are not allowed to exist.
     *
     * @param version the version string to use for the backup zip name
     */
    public void backup(final String version) {
        log.info("Backing up!");
        final long time = new Date().getTime();
        if (!backupDatabase()) {
            log.warn("There was an error during backing up the database! This does not affect"
                    + " the configuration backup, nor damage your database. You should backup"
                    + " the database manually if you want to be extra safe, but it's not necessary if"
                    + " you don't want to downgrade later.");
        }

        if (!backupFolder.isDirectory() && !backupFolder.mkdir()) {
            log.error("Could not create backup folder!");
        }

        final String outputPath = backupFolder.getAbsolutePath() + File.separator + "backup-" + version;
        Zipper.zip(root, outputPath, "^backup.*", "^database\\.db$", "^logs$");
        if (!databaseBackupFile.delete()) {
            log.warn("Could not delete database backup file!");
        }

        log.debug("Done in " + (new Date().getTime() - time) + "ms");
        log.info("Done, you can find the backup in 'Backups' directory.");
    }

    /**
     * Backs the database up to the {@code databaseBackupFile} if that file does not exist.
     *
     * @return true if the backup was successful, false if there was an error
     */
    public boolean backupDatabase() {
        return backupDatabase(databaseBackupFile);
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    private boolean backupDatabase(final File databaseBackupFile) {
        try {
            if (!databaseBackupFile.createNewFile()) {
                log.warn("Could not create the backup file!");
                return false;
            }
            boolean done = true;
            final FileConfigAccessor config = configAccessorFactory.create(databaseBackupFile);
            // prepare the database and map
            final Map<String, ResultSet> map = new HashMap<>();
            final String[] tables = {"objectives", "tags", "points", "journals", "player", "backpack", "global_points",
                    "global_tags", "migration", "player_profile", "profile"};
            // open database connection
            // load resultsets into the map
            for (final String table : tables) {
                log.debug("Loading " + table);
                final String enumName = ("LOAD_ALL_" + table).toUpperCase(Locale.ROOT);
                map.put(table, con.querySQL(QueryType.valueOf(enumName)));
            }
            // extract data from resultsets into the config file
            for (final Map.Entry<String, ResultSet> entry : map.entrySet()) {
                log.debug("Saving " + entry.getKey() + " to the backup file");
                // prepare resultset and meta
                try (ResultSet res = entry.getValue()) {
                    final ResultSetMetaData result = res.getMetaData();
                    // get the list of column names
                    final List<String> columns = new ArrayList<>();
                    final int columnCount = result.getColumnCount();
                    log.debug("  There are " + columnCount + " columns in this ResultSet");
                    for (int i = 1; i <= result.getColumnCount(); i++) {
                        final String columnName = result.getColumnName(i);
                        log.debug("    Adding column " + columnName);
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
                                log.warn("Could not read SQL: " + e.getMessage(), e);
                                done = false;
                                // do nothing, as there can be nothing done
                                // error while loading the string means the
                                // database entry is broken
                            }
                        }
                        counter++;
                    }
                    log.debug("  Saved " + (counter + 1) + " rows");
                }
            }
            // save the config at the end
            config.save();
            return done;
        } catch (final IOException | SQLException | InvalidConfigurationException e) {
            log.warn("There was an error during database backup: " + e.getMessage(), e);
            if (databaseBackupFile.exists() && !databaseBackupFile.delete()) {
                log.warn("Could not delete the broken backup file!");
            }
            return false;
        }
    }

    /**
     * Loads an existing {@code databaseBackupFile} into the database.
     * The existing database is saved into the {@code backupFolder}.
     */
    public void loadDatabaseFromBackup() {
        if (!databaseBackupFile.exists()) {
            return;
        }
        log.info("Loading database backup!");

        if (!backupFolder.isDirectory() && !backupFolder.mkdirs()) {
            log.warn("Could not create the backup folder!");
            return;
        }
        int backupNumber = 0;
        while (new File(backupFolder, "old-database-" + backupNumber + ".yml").exists()) {
            backupNumber++;
        }
        final String filename = "old-database-" + backupNumber + ".yml";
        log.info("Backing up old database!");
        if (!backupDatabase(new File(backupFolder, filename))) {
            log.warn("There was an error during old database backup process. This means that"
                    + " if the plugin loaded new database (from backup), the old one would be lost "
                    + "forever. Because of that the loading of backup was aborted!");
            return;
        }
        final ConfigAccessor config;
        try {
            config = configAccessorFactory.create(databaseBackupFile);
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            log.warn(e.getMessage(), e);
            return;
        }
        // create tables if they don't exist, so we can be 100% sure
        // that we can drop them without an error (should've been done
        // in a different way...)
        con.getDatabase().createTables();
        // drop all tables
        con.updateSQL(UpdateType.DROP_OBJECTIVES);
        con.updateSQL(UpdateType.DROP_TAGS);
        con.updateSQL(UpdateType.DROP_POINTS);
        con.updateSQL(UpdateType.DROP_JOURNALS);
        con.updateSQL(UpdateType.DROP_BACKPACK);
        con.updateSQL(UpdateType.DROP_GLOBAL_POINTS);
        con.updateSQL(UpdateType.DROP_GLOBAL_TAGS);
        con.updateSQL(UpdateType.DROP_MIGRATION);
        con.updateSQL(UpdateType.DROP_PLAYER_PROFILE);
        con.updateSQL(UpdateType.DROP_PLAYER);
        con.updateSQL(UpdateType.DROP_PROFILE);

        loadDatabaseFromBackup0(config);
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity", "PMD.NPathComplexity", "PMD.AvoidDuplicateLiterals"})
    private void loadDatabaseFromBackup0(final ConfigAccessor config) {
        con.getDatabase().createTables();

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
        if (!databaseBackupFile.delete()) {
            log.warn("Could not delete the backup file!");
        }
    }
}
