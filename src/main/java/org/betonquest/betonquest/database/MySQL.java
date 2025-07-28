package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.item.SimpleQuestItemFactory;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Connects to and uses a MySQL database.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class MySQL extends Database {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Username for the MySQL user.
     */
    private final String user;

    /**
     * Prefix for the database tables.
     */
    private final String database;

    /**
     * Password for the MySQL user.
     */
    private final String password;

    /**
     * Port number to connect to the MySQL server.
     */
    private final String port;

    /**
     * Name of the host to connect to the MySQL server.
     */
    private final String hostname;

    /**
     * Creates a new MySQL instance
     *
     * @param log      the logger that will be used for logging
     * @param plugin   Plugin instance
     * @param hostname Name of the host
     * @param port     Port number
     * @param database Database name
     * @param username Username
     * @param password Password
     */
    public MySQL(final BetonQuestLogger log, final BetonQuest plugin, final String hostname, final String port, final String database, final String username, final String password) {
        super(log, plugin);
        this.log = log;
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
    }

    @Override
    public Connection openConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?&useSSL=false", this.user, this.password);
            final String connectionClassName = connection.getClass().getName();
            if (!connectionClassName.startsWith("com.mysql.")) {
                log.warn("External source modified or changed the MySQL connector! We can not guarantee that BetonQuest will work correctly with this connector: " + connectionClassName);
            }
        } catch (final ClassNotFoundException | SQLException e) {
            log.warn("MySQL says: " + e.getMessage(), e);
        }
        if (connection == null) {
            throw new IllegalStateException("Not able to create a database connection!");
        }
        return connection;
    }

    @Override
    protected SortedMap<MigrationKey, DatabaseUpdate> getMigrations() {
        final SortedMap<MigrationKey, DatabaseUpdate> migrations = new TreeMap<>();
        migrations.put(new MigrationKey("betonquest", 1), this::migration1);
        migrations.put(new MigrationKey("betonquest", 2), this::migration2);
        migrations.put(new MigrationKey("betonquest", 3), this::migration3);
        migrations.put(new MigrationKey("betonquest", 4), this::migration4);
        return migrations;
    }

    @Override
    @SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
    protected Set<MigrationKey> queryExecutedMigrations(final Connection connection) throws SQLException {
        final Set<MigrationKey> executedMigrations = new HashSet<>();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix
                    + "migration (namespace VARCHAR(63) NOT NULL, migration_id INT, "
                    + "time TIMESTAMP DEFAULT NOW(), PRIMARY KEY (namespace, migration_id))");
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
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "objectives ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "playerID VARCHAR(256) NOT NULL, "
                    + "objective VARCHAR(512)  NOT NULL, "
                    + "instructions VARCHAR(2048) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "tags ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "playerID VARCHAR(256) NOT NULL, "
                    + "tag TEXT NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "points ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "playerID VARCHAR(256) NOT NULL, "
                    + "category VARCHAR(256) NOT NULL, "
                    + "count INT NOT NULL);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "journal ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "playerID VARCHAR(256) NOT NULL, "
                    + "pointer VARCHAR(256) NOT NULL, "
                    + "date TIMESTAMP NOT NULL);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "backpack ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "playerID VARCHAR(256) NOT NULL, "
                    + "instruction TEXT NOT NULL, "
                    + "amount INT NOT NULL);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "player ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "playerID VARCHAR(256) NOT NULL, "
                    + "language VARCHAR(16) NOT NULL, "
                    + "conversation VARCHAR(512));");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "global_tags ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "tag TEXT NOT NULL);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "global_points ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "category VARCHAR(256) NOT NULL, "
                    + "count INT NOT NULL);");
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
            statement.executeUpdate("CREATE TABLE " + prefix + "profile ("
                    + "profileID CHAR(36) PRIMARY KEY NOT NULL)");
            deleteDuplicates(statement, "player", null);
            statement.executeUpdate("INSERT INTO " + prefix + "profile "
                    + "(profileID) SELECT playerID FROM " + prefix + "player");
            deleteOrphaned(statement, "backpack");
            statement.executeUpdate("ALTER TABLE " + prefix + "backpack "
                    + "CHANGE COLUMN playerID profileID CHAR(36) NOT NULL, "
                    + "ADD FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE");
            deleteOrphaned(statement, "journal");
            statement.executeUpdate("ALTER TABLE " + prefix + "journal "
                    + "CHANGE COLUMN playerID profileID CHAR(36) NOT NULL, "
                    + "MODIFY COLUMN pointer VARCHAR(255) NOT NULL, "
                    + "ADD FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE");
            deleteDuplicates(statement, "objectives", "objective");
            deleteOrphaned(statement, "objectives");
            statement.executeUpdate("ALTER TABLE " + prefix + "objectives "
                    + "CHANGE COLUMN playerID profileID CHAR(36) NOT NULL, "
                    + "MODIFY COLUMN objective VARCHAR(510) NOT NULL, "
                    + "MODIFY COLUMN instructions VARCHAR(2046) NOT NULL, "
                    + "DROP PRIMARY KEY, "
                    + "DROP COLUMN id, "
                    + "ADD PRIMARY KEY (profileID, objective), "
                    + "ADD FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE");
            deleteDuplicates(statement, "points", "category");
            deleteOrphaned(statement, "points");
            statement.executeUpdate("ALTER TABLE " + prefix + "points "
                    + "CHANGE COLUMN playerID profileID CHAR(36) NOT NULL, "
                    + "MODIFY COLUMN category VARCHAR(255) NOT NULL, "
                    + "DROP PRIMARY KEY, "
                    + "DROP COLUMN id, "
                    + "ADD PRIMARY KEY (profileID, category), "
                    + "ADD FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE");
            deleteDuplicates(statement, "tags", "tag");
            deleteOrphaned(statement, "tags");
            statement.executeUpdate("ALTER TABLE " + prefix + "tags "
                    + "CHANGE COLUMN playerID profileID CHAR(36) NOT NULL, "
                    + "MODIFY COLUMN tag VARCHAR(510) NOT NULL, "
                    + "DROP PRIMARY KEY, "
                    + "DROP COLUMN id, "
                    + "ADD PRIMARY KEY (profileID, tag), "
                    + "ADD FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE");
            statement.executeUpdate("ALTER TABLE " + prefix + "player "
                    + "MODIFY COLUMN playerID CHAR(36) NOT NULL, "
                    + "MODIFY COLUMN conversation VARCHAR(510), "
                    + "ADD COLUMN active_profile CHAR(36) AFTER playerID");
            statement.executeUpdate("UPDATE " + prefix + "player "
                    + "SET active_profile = playerID");
            statement.executeUpdate("ALTER TABLE " + prefix + "player "
                    + "MODIFY COLUMN active_profile CHAR(36) NOT NULL, "
                    + "DROP PRIMARY KEY, "
                    + "DROP COLUMN id, "
                    + "ADD PRIMARY KEY (playerID), "
                    + "ADD FOREIGN KEY (active_profile) REFERENCES " + prefix + "profile (profileID) ON DELETE RESTRICT");
            statement.executeUpdate("ALTER TABLE " + prefix + "player "
                    + "ALTER COLUMN active_profile DROP DEFAULT ");
            statement.executeUpdate("CREATE TABLE " + prefix + "player_profile ("
                    + "playerID CHAR(36) NOT NULL, "
                    + "profileID CHAR(36) NOT NULL, "
                    + "name VARCHAR(510), " + "PRIMARY KEY (profileID, playerID), "
                    + "FOREIGN KEY (playerID) REFERENCES " + prefix + "player (playerID) ON DELETE CASCADE, "
                    + "FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE, "
                    + "UNIQUE KEY (playerID, name))");
            statement.executeUpdate("INSERT INTO " + prefix + "player_profile "
                    + "(playerID, profileID, name) SELECT playerID, active_profile, NULL FROM " + prefix + "player");
            statement.executeUpdate("ALTER TABLE " + prefix + "global_points "
                    + "DROP PRIMARY KEY, "
                    + "DROP COLUMN id, "
                    + "ADD PRIMARY KEY (category)");
            statement.executeUpdate("ALTER TABLE " + prefix + "global_tags "
                    + "DROP PRIMARY KEY, "
                    + "DROP COLUMN id, "
                    + "MODIFY COLUMN tag VARCHAR(510) NOT NULL, "
                    + "ADD PRIMARY KEY (tag)");
        }
    }

    private void deleteOrphaned(final Statement statement, final String table) throws SQLException {
        statement.executeUpdate("DELETE FROM " + prefix + table + " "
                + "WHERE playerID NOT IN (SELECT playerID FROM " + prefix + "player)");
    }

    @SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
    private void deleteDuplicates(final Statement statement, final String table, @Nullable final String groupBy) throws SQLException {
        final String groupByClause = groupBy == null ? "" : ", " + groupBy;
        statement.executeUpdate("DELETE FROM " + prefix + table + " "
                + "WHERE id NOT IN (SELECT t.min_id FROM ("
                + "SELECT MIN(id) AS min_id FROM " + prefix + table + " GROUP BY playerID" + groupByClause + ") t )");
    }

    private void migration3(final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("UPDATE " + prefix + "player_profile "
                    + "SET name = '" + profileInitialName + "' WHERE name IS NULL");
            statement.executeUpdate("ALTER TABLE " + prefix + "player_profile "
                    + "MODIFY COLUMN name VARCHAR(63) NOT NULL");
        }
    }

    @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
    private void migration4(final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE " + prefix + "backpack_tmp ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "profileID CHAR(36) NOT NULL, "
                    + "serialized TEXT NOT NULL, "
                    + "amount INT NOT NULL, "
                    + "FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE)");
            try (ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM " + prefix + "backpack");
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "INSERT INTO " + prefix + "backpack_tmp (id, profileID, serialized, amount) VALUES (?, ?, ?, ?)")) {
                while (resultSet.next()) {
                    final int rowId = resultSet.getInt("id");
                    final String profileId = resultSet.getString("profileID");
                    final String instruction = resultSet.getString("instruction");
                    final int amount = resultSet.getInt("amount");
                    final byte[] bytes;
                    try {
                        bytes = new SimpleQuestItemFactory().parseInstruction(instruction).generate(1).serializeAsBytes();
                    } catch (final QuestException e) {
                        log.warn("Could not generate QuestItem from Instruction: " + e.getMessage(), e);
                        continue;
                    }
                    final String serialized = Base64.getEncoder().encodeToString(bytes);
                    preparedStatement.setInt(1, rowId);
                    preparedStatement.setString(2, profileId);
                    preparedStatement.setString(3, serialized);
                    preparedStatement.setInt(4, amount);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }
            statement.executeUpdate("DROP TABLE " + prefix + "backpack");
            statement.executeUpdate("ALTER TABLE " + prefix + "backpack_tmp RENAME TO " + prefix + "backpack");
        }
    }
}
