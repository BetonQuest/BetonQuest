package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Connects to and uses a SQLite database
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
public class SQLite extends Database {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(SQLite.class);
    private final String dbLocation;

    /**
     * Creates a new SQLite instance
     *
     * @param plugin     Plugin instance
     * @param dbLocation Location of the Database (Must end in .db)
     */
    public SQLite(final BetonQuest plugin, final String dbLocation) {
        super(plugin);
        this.dbLocation = dbLocation;
    }

    @Override
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public Connection openConnection() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        final File file = new File(plugin.getDataFolder(), dbLocation);
        if (!(file.exists())) {
            try {
                file.createNewFile();
            } catch (final IOException e) {
                LOG.error("Unable to create database!", e);
            }
        }
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager
                    .getConnection("jdbc:sqlite:" + plugin.getDataFolder().toPath() + "/" + dbLocation);
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error("There was an exception with SQL", e);
        }
        return connection;
    }

    @Override
    protected SortedMap<MigrationKey, DatabaseUpdate> getMigrations() {
        final SortedMap<MigrationKey, DatabaseUpdate> migrations = new TreeMap<>();
        migrations.put(new MigrationKey("betonquest", 1), this::migration1);
        migrations.put(new MigrationKey("betonquest", 2), this::migration2);
        return migrations;
    }

    @Override
    protected Set<MigrationKey> queryExecutedMigrations(final Connection connection) throws SQLException {
        final Set<MigrationKey> executedMigrations = new HashSet<>();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "migration (namespace VARCHAR(63) NOT NULL, migration_id INT, " +
                    "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (namespace, migration_id))");
            try (ResultSet result = statement.executeQuery("SELECT namespace, migration_id FROM " + prefix + "migration")) {
                while (result.next()) {
                    executedMigrations.add(new MigrationKey(result.getString("namespace"), result.getInt("migration_id")));
                }
            }
        }
        return executedMigrations;
    }

    @Override
    protected void markMigrationExecuted(final Connection connection, final MigrationKey migrationKey) throws SQLException {
        try (PreparedStatement statement = getConnection().prepareStatement("INSERT INTO " + prefix + "migration (namespace, migration_id) VALUES (?,?)")) {
            statement.setString(1, migrationKey.namespace());
            statement.setInt(2, migrationKey.version());
            statement.executeUpdate();
        }
    }

    /**
     * Executes the first migration.
     *
     * @param connection the connection to the database
     * @throws SQLException if something goes wrong, while executing the query's
     */
    @SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
    private void migration1(final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "objectives (id INTEGER PRIMARY KEY "
                    + "AUTOINCREMENT, playerID VARCHAR(256) NOT NULL, objective VARCHAR(512)"
                    + " NOT NULL, instructions VARCHAR(2048) NOT NULL);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "tags (id INTEGER PRIMARY KEY "
                    + "AUTOINCREMENT, playerID VARCHAR(256) NOT NULL, tag TEXT NOT NULL);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "points (id INTEGER PRIMARY KEY "
                    + "AUTOINCREMENT, playerID VARCHAR(256) NOT NULL, category VARCHAR(256) "
                    + "NOT NULL, count INT NOT NULL);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "journal (id INTEGER PRIMARY KEY "
                    + "AUTOINCREMENT, playerID VARCHAR(256) NOT NULL, pointer "
                    + "VARCHAR(256) NOT NULL, date TIMESTAMP NOT NULL);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "backpack (id INTEGER PRIMARY KEY "
                    + "AUTOINCREMENT, playerID VARCHAR(256) NOT NULL, instruction "
                    + "TEXT NOT NULL, amount INT NOT NULL);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "player (id INTEGER PRIMARY KEY "
                    + "AUTOINCREMENT, playerID VARCHAR(256) NOT NULL, language VARCHAR(16) NOT NULL, "
                    + "conversation VARCHAR(512));");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "global_tags (id INTEGER PRIMARY KEY "
                    + "AUTOINCREMENT, tag TEXT NOT NULL);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "global_points (id INTEGER PRIMARY KEY "
                    + "AUTOINCREMENT, category VARCHAR(256) NOT NULL, count INT NOT NULL);");
        } catch (final SQLException e) {
            LOG.error("There was an exception with SQL", e);
        }
    }

    /**
     * Executes the second migration.
     *
     * @param connection the connection to the database
     * @throws SQLException if something goes wrong, while executing the query's
     */
    @SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
    private void migration2(final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE " + prefix + "profile" +
                    "(profileID CHAR(36) PRIMARY KEY NOT NULL)");
            statement.executeUpdate("INSERT INTO " + prefix + "profile " +
                    "(profileID) SELECT playerID FROM " + prefix + "player");
            statement.executeUpdate("CREATE TABLE " + prefix + "backpack_tmp" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "profileID CHAR(36) NOT NULL, " +
                    "instruction TEXT NOT NULL, " +
                    "amount INTEGER NOT NULL, " +
                    "FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE)");
            statement.executeUpdate("INSERT INTO " + prefix + "backpack_tmp " +
                    "(id, profileID, instruction, amount) " +
                    "SELECT id, playerID, instruction, amount FROM " + prefix + "backpack");
            statement.executeUpdate("DROP TABLE " + prefix + "backpack");
            statement.executeUpdate("ALTER TABLE " + prefix + "backpack_tmp " +
                    "RENAME TO " + prefix + "backpack");
            statement.executeUpdate("CREATE TABLE " + prefix + "journal_tmp" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "profileID CHAR(36) NOT NULL, " +
                    "pointer VARCHAR(255) NOT NULL, " +
                    "date TIMESTAMP NOT NULL, " +
                    "FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE)");
            statement.executeUpdate("INSERT INTO " + prefix + "journal_tmp " +
                    "(id, profileID, pointer, date) SELECT id, playerID, pointer, date FROM " + prefix + "journal");
            statement.executeUpdate("DROP TABLE " + prefix + "journal");
            statement.executeUpdate("ALTER TABLE " + prefix + "journal_tmp " +
                    "RENAME TO " + prefix + "journal");
            statement.executeUpdate("CREATE TABLE " + prefix + "objectives_tmp" +
                    "(profileID CHAR(36) NOT NULL, " +
                    "objective VARCHAR(510) NOT NULL, " +
                    "instructions VARCHAR(2046) NOT NULL, " +
                    "PRIMARY KEY (profileID, objective), " +
                    "FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE)");
            statement.executeUpdate("INSERT OR IGNORE INTO " + prefix + "objectives_tmp " +
                    "(profileID, objective, instructions) " +
                    "SELECT playerID, objective, instructions FROM " + prefix + "objectives");
            statement.executeUpdate("DROP TABLE " + prefix + "objectives");
            statement.executeUpdate("ALTER TABLE " + prefix + "objectives_tmp " +
                    "RENAME TO " + prefix + "objectives");
            statement.executeUpdate("CREATE TABLE " + prefix + "points_tmp" +
                    "(profileID CHAR(36) NOT NULL, " +
                    "category VARCHAR(255) NOT NULL, " +
                    "count INTEGER NOT NULL, " +
                    "PRIMARY KEY (profileID, category), " +
                    "FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE)");
            statement.executeUpdate("INSERT OR IGNORE INTO " + prefix + "points_tmp " +
                    "(profileID, category, count) SELECT playerID, category, count FROM " + prefix + "points");
            statement.executeUpdate("DROP TABLE " + prefix + "points");
            statement.executeUpdate("ALTER TABLE " + prefix + "points_tmp " +
                    "RENAME TO " + prefix + "points");
            statement.executeUpdate("CREATE TABLE " + prefix + "tags_tmp" +
                    "(profileID CHAR(36) NOT NULL, " +
                    "tag TEXT NOT NULL, " +
                    "PRIMARY KEY (profileID, tag), " +
                    "FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE)");
            statement.executeUpdate("INSERT OR IGNORE INTO " + prefix + "tags_tmp " +
                    "(profileID, tag) SELECT playerID, tag FROM " + prefix + "tags");
            statement.executeUpdate("DROP TABLE " + prefix + "tags");
            statement.executeUpdate("ALTER TABLE " + prefix + "tags_tmp " +
                    "RENAME TO " + prefix + "tags");
            statement.executeUpdate("CREATE TABLE " + prefix + "player_tmp" +
                    "(playerID CHAR(36) NOT NULL, " +
                    "language VARCHAR(16) NOT NULL, " +
                    "conversation VARCHAR(510), " +
                    "active_profile CHAR(36) NOT NULL, " +
                    "PRIMARY KEY (playerID), " +
                    "FOREIGN KEY (active_profile) REFERENCES " + prefix + "profile (profileID) ON DELETE RESTRICT)");
            statement.executeUpdate("INSERT OR IGNORE INTO " + prefix + "player_tmp " +
                    "(playerID, language, conversation, active_profile) " +
                    "SELECT playerID, language, conversation, playerID FROM " + prefix + "player");
            statement.executeUpdate("DROP TABLE " + prefix + "player");
            statement.executeUpdate("ALTER TABLE " + prefix + "player_tmp " +
                    "RENAME TO " + prefix + "player");
            statement.executeUpdate("CREATE TABLE " + prefix + "player_profile" +
                    "(playerID CHAR(36) NOT NULL, " +
                    "profileID CHAR(36) NOT NULL, " +
                    "name VARCHAR(63), " +
                    "PRIMARY KEY (playerID, profileID), " +
                    "FOREIGN KEY (playerID) REFERENCES " + prefix + "player (playerID) ON DELETE CASCADE, " +
                    "FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE, " +
                    "UNIQUE (playerID, name))");
            statement.executeUpdate("INSERT OR IGNORE INTO " + prefix + "player_profile " +
                    "(playerID, profileID, name) " +
                    "SELECT playerID, active_profile, NULL FROM " + prefix + "player");
            statement.executeUpdate("CREATE TABLE " + prefix + "gloabl_tags_tmp" +
                    "(tag VARCHAR(510) NOT NULL, " +
                    "PRIMARY KEY (tag))");
            statement.executeUpdate("INSERT INTO " + prefix + "gloabl_tags_tmp " +
                    "(tag) SELECT tag FROM " + prefix + "global_tags");
            statement.executeUpdate("DROP TABLE " + prefix + "global_tags");
            statement.executeUpdate("ALTER TABLE " + prefix + "gloabl_tags_tmp " +
                    "RENAME TO " + prefix + "global_tags");
            statement.executeUpdate("CREATE TABLE " + prefix + "global_points_tmp" +
                    "(category VARCHAR(255) NOT NULL, " +
                    "count INTEGER NOT NULL, " +
                    "PRIMARY KEY (category))");
            statement.executeUpdate("INSERT INTO " + prefix + "global_points_tmp " +
                    "(category, count) SELECT category, count FROM " + prefix + "global_points");
            statement.executeUpdate("DROP TABLE " + prefix + "global_points");
            statement.executeUpdate("ALTER TABLE " + prefix + "global_points_tmp " +
                    "RENAME TO " + prefix + "global_points");
        } catch (final SQLException e) {
            LOG.error("There was an exception with SQL", e);
        }
    }
}
