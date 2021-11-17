package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Connects to the database and queries it
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
@CustomLog
public class Connector {

    private final String prefix;
    private final Database database;
    private Connection connection;

    /**
     * Opens a new connection to the database
     */
    public Connector() {
        final BetonQuest plugin = BetonQuest.getInstance();
        prefix = plugin.getConfig().getString("mysql.prefix", "");
        database = plugin.getDB();
        connection = database.getConnection();
        refresh();
    }

    /**
     * This method should be used before any other database operations.
     */
    @SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION"})
    public final void refresh() {
        try {
            connection.prepareStatement("SELECT 1").executeQuery().close();
        } catch (final SQLException e) {
            LOG.warning("Reconnecting to the database", e);
            database.closeConnection();
            connection = database.getConnection();
        }
    }

    /**
     * Queries the database with the given type and arguments
     *
     * @param type type of the query
     * @param args arguments
     * @return ResultSet with the requested data
     */
    @SuppressWarnings({"PMD.NcssCount", "PMD.CloseResource"})
    @SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE"})
    public ResultSet querySQL(final QueryType type, final String... args) {
        final String stringStatement;
        switch (type) {
            case SELECT_JOURNAL:
                stringStatement = "SELECT pointer, date FROM " + prefix + "journal WHERE playerID = ?;";
                break;
            case SELECT_POINTS:
                stringStatement = "SELECT category, count FROM " + prefix + "points WHERE playerID = ?;";
                break;
            case SELECT_OBJECTIVES:
                stringStatement = "SELECT objective, instructions FROM " + prefix + "objectives WHERE playerID = ?;";
                break;
            case SELECT_TAGS:
                stringStatement = "SELECT tag FROM " + prefix + "tags WHERE playerID = ?;";
                break;
            case SELECT_BACKPACK:
                stringStatement = "SELECT instruction, amount FROM " + prefix + "backpack WHERE playerID = ?;";
                break;
            case SELECT_PLAYER:
                stringStatement = "SELECT language, conversation FROM " + prefix + "player WHERE playerID = ?;";
                break;
            case SELECT_PLAYERS_TAGS:
                stringStatement = "SELECT playerID FROM " + prefix + "tags GROUP BY playerID;";
                break;
            case SELECT_PLAYERS_JOURNAL:
                stringStatement = "SELECT playerID FROM " + prefix + "journal GROUP BY playerID;";
                break;
            case SELECT_PLAYERS_POINTS:
                stringStatement = "SELECT playerID FROM " + prefix + "points GROUP BY playerID;";
                break;
            case SELECT_PLAYERS_OBJECTIVES:
                stringStatement = "SELECT playerID FROM " + prefix + "objectives GROUP BY playerID;";
                break;
            case SELECT_PLAYERS_BACKPACK:
                stringStatement = "SELECT playerID FROM " + prefix + "backpack GROUP BY playerID;";
                break;
            case LOAD_ALL_JOURNALS:
                stringStatement = "SELECT * FROM " + prefix + "journal";
                break;
            case LOAD_ALL_OBJECTIVES:
                stringStatement = "SELECT * FROM " + prefix + "objectives";
                break;
            case LOAD_ALL_POINTS:
                stringStatement = "SELECT * FROM " + prefix + "points";
                break;
            case LOAD_ALL_GLOBAL_POINTS:
                stringStatement = "SELECT * FROM " + prefix + "global_points";
                break;
            case LOAD_ALL_TAGS:
                stringStatement = "SELECT * FROM " + prefix + "tags";
                break;
            case LOAD_ALL_GLOBAL_TAGS:
                stringStatement = "SELECT * FROM " + prefix + "global_tags";
                break;
            case LOAD_ALL_BACKPACK:
                stringStatement = "SELECT * FROM " + prefix + "backpack";
                break;
            case LOAD_ALL_PLAYER:
                stringStatement = "SELECT * FROM " + prefix + "player";
                break;
            default:
                stringStatement = "SELECT 1";
                break;
        }

        try {
            final PreparedStatement statement = connection.prepareStatement(stringStatement);
            for (int i = 0; i < args.length; i++) {
                statement.setString(i + 1, args[i]);
            }
            return statement.executeQuery();
        } catch (final SQLException e) {
            LOG.warning("There was a exception with SQL", e);
            return null;
        }
    }

    /**
     * Updates the database with the given type and arguments
     *
     * @param type type of the update
     * @param args arguments
     */
    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NcssCount", "PMD.NPathComplexity"})
    public void updateSQL(final UpdateType type, final String... args) {
        final String stringStatement;
        switch (type) {
            case ADD_OBJECTIVES:
                stringStatement = "INSERT INTO " + prefix + "objectives (playerID, objective, instructions) VALUES (?, ?, ?);";
                break;
            case ADD_TAGS:
                stringStatement = "INSERT INTO " + prefix + "tags (playerID, tag) VALUES (?, ?);";
                break;
            case ADD_GLOBAL_TAGS:
                stringStatement = "INSERT INTO " + prefix + "global_tags (tag) VALUES (?);";
                break;
            case ADD_POINTS:
                stringStatement = "INSERT INTO " + prefix + "points (playerID, category, count) VALUES (?, ?, ?);";
                break;
            case ADD_GLOBAL_POINTS:
                stringStatement = "INSERT INTO " + prefix + "global_points (category, count) VALUES (?, ?);";
                break;
            case ADD_JOURNAL:
                stringStatement = "INSERT INTO " + prefix + "journal (playerID, pointer, date) VALUES (?, ?, ?);";
                break;
            case ADD_BACKPACK:
                stringStatement = "INSERT INTO " + prefix + "backpack (playerID, instruction, amount) VALUES (?, ?, ?);";
                break;
            case ADD_PLAYER:
                stringStatement = "INSERT INTO " + prefix + "player (playerID, language) VALUES (?, ?);";
                break;
            case REMOVE_OBJECTIVES:
                stringStatement = "DELETE FROM " + prefix + "objectives WHERE playerID = ? AND objective = ?;";
                break;
            case REMOVE_TAGS:
                stringStatement = "DELETE FROM " + prefix + "tags WHERE playerID = ? AND tag = ?;";
                break;
            case REMOVE_GLOBAL_TAGS:
                stringStatement = "DELETE FROM " + prefix + "global_tags WHERE tag = ?;";
                break;
            case REMOVE_POINTS:
                stringStatement = "DELETE FROM " + prefix + "points WHERE playerID = ? AND category = ?;";
                break;
            case REMOVE_GLOBAL_POINTS:
                stringStatement = "DELETE FROM " + prefix + "global_points WHERE category = ?;";
                break;
            case REMOVE_JOURNAL:
                stringStatement = "DELETE FROM " + prefix + "journal WHERE playerID = ? AND pointer = ? AND date = ?;";
                break;
            case DELETE_OBJECTIVES:
                stringStatement = "DELETE FROM " + prefix + "objectives WHERE playerID = ?;";
                break;
            case DELETE_TAGS:
                stringStatement = "DELETE FROM " + prefix + "tags WHERE playerID = ?;";
                break;
            case DELETE_GLOBAL_TAGS:
                stringStatement = "DELETE FROM " + prefix + "global_tags";
                break;
            case DELETE_POINTS:
                stringStatement = "DELETE FROM " + prefix + "points WHERE playerID = ?;";
                break;
            case DELETE_GLOBAL_POINTS:
                stringStatement = "DELETE FROM " + prefix + "global_points";
                break;
            case DELETE_JOURNAL:
                stringStatement = "DELETE FROM " + prefix + "journal WHERE playerID = ?;";
                break;
            case DELETE_BACKPACK:
                stringStatement = "DELETE FROM " + prefix + "backpack WHERE playerID = ?;";
                break;
            case DELETE_PLAYER:
                stringStatement = "DELETE FROM " + prefix + "player WHERE playerID = ?;";
                break;
            case UPDATE_PLAYERS_OBJECTIVES:
                stringStatement = "UPDATE " + prefix + "objectives SET playerID = ? WHERE playerID = ?;";
                break;
            case UPDATE_PLAYERS_TAGS:
                stringStatement = "UPDATE " + prefix + "tags SET playerID = ? WHERE playerID = ?;";
                break;
            case UPDATE_PLAYERS_POINTS:
                stringStatement = "UPDATE " + prefix + "points SET playerID = ? WHERE playerID = ?;";
                break;
            case UPDATE_PLAYERS_JOURNAL:
                stringStatement = "UPDATE " + prefix + "journal SET playerID = ? WHERE playerID = ?;";
                break;
            case UPDATE_PLAYERS_BACKPACK:
                stringStatement = "UPDATE " + prefix + "backpack SET playerID = ? WHERE playerID = ?;";
                break;
            case DROP_OBJECTIVES:
                stringStatement = "DROP TABLE " + prefix + "objectives";
                break;
            case DROP_TAGS:
                stringStatement = "DROP TABLE " + prefix + "tags";
                break;
            case DROP_GLOBAL_TAGS:
                stringStatement = "DROP TABLE " + prefix + "global_tags";
                break;
            case DROP_POINTS:
                stringStatement = "DROP TABLE " + prefix + "points";
                break;
            case DROP_GLOBAL_POINTS:
                stringStatement = "DROP TABLE " + prefix + "global_points";
                break;
            case DROP_JOURNALS:
                stringStatement = "DROP TABLE " + prefix + "journal";
                break;
            case DROP_BACKPACK:
                stringStatement = "DROP TABLE " + prefix + "backpack";
                break;
            case DROP_PLAYER:
                stringStatement = "DROP TABLE " + prefix + "player";
                break;
            case INSERT_OBJECTIVE:
                stringStatement = "INSERT INTO " + prefix + "objectives VALUES (?,?,?,?)";
                break;
            case INSERT_TAG:
                stringStatement = "INSERT INTO " + prefix + "tags VALUES (?,?,?)";
                break;
            case INSERT_GLOBAL_TAG:
                stringStatement = "INSERT INTO " + prefix + "global_tags VALUES (?,?)";
                break;
            case INSERT_POINT:
                stringStatement = "INSERT INTO " + prefix + "points VALUES (?,?,?,?)";
                break;
            case INSERT_GLOBAL_POINT:
                stringStatement = "INSERT INTO " + prefix + "global_points VALUES (?,?,?)";
                break;
            case INSERT_JOURNAL:
                stringStatement = "INSERT INTO " + prefix + "journal VALUES (?,?,?,?)";
                break;
            case INSERT_BACKPACK:
                stringStatement = "INSERT INTO " + prefix + "backpack VALUES (?,?,?,?)";
                break;
            case INSERT_PLAYER:
                stringStatement = "INSERT INTO " + prefix + "player VALUES (?,?,?,?);";
                break;
            case UPDATE_CONVERSATION:
                stringStatement = "UPDATE " + prefix + "player SET conversation = ? WHERE playerID = ?";
                break;
            case REMOVE_ALL_TAGS:
                stringStatement = "DELETE FROM " + prefix + "tags WHERE tag = ?;";
                break;
            case REMOVE_ALL_POINTS:
                stringStatement = "DELETE FROM " + prefix + "points WHERE category = ?;";
                break;
            case REMOVE_ALL_OBJECTIVES:
                stringStatement = "DELETE FROM " + prefix + "objectives WHERE objective = ?;";
                break;
            case REMOVE_ALL_ENTRIES:
                stringStatement = "DELETE FROM " + prefix + "journal WHERE pointer = ?;";
                break;
            case RENAME_ALL_TAGS:
                stringStatement = "UPDATE " + prefix + "tags SET tag = ? WHERE tag = ?;";
                break;
            case RENAME_ALL_POINTS:
                stringStatement = "UPDATE " + prefix + "points SET category = ? WHERE category = ?;";
                break;
            case RENAME_ALL_GLOBAL_POINTS:
                stringStatement = "UPDATE " + prefix + "global_points SET category = ? WHERE category = ?;";
                break;
            case RENAME_ALL_OBJECTIVES:
                stringStatement = "UPDATE " + prefix + "objectives SET objective = ? WHERE objective = ?;";
                break;
            case RENAME_ALL_ENTRIES:
                stringStatement = "UPDATE " + prefix + "journal SET pointer = ? WHERE pointer = ?;";
                break;
            default:
                stringStatement = "SELECT 1";
                break;
        }

        try (PreparedStatement statement = connection.prepareStatement(stringStatement)) {
            for (int i = 0; i < args.length; i++) {
                statement.setString(i + 1, args[i]);
            }
            statement.executeUpdate();
        } catch (final SQLException e) {
            LOG.error("There was an exception with SQL", e);
        }
    }

    /**
     * Type of the query
     */
    public enum QueryType {

        SELECT_OBJECTIVES, SELECT_TAGS, SELECT_POINTS, SELECT_JOURNAL, SELECT_BACKPACK, SELECT_PLAYER,

        SELECT_PLAYERS_TAGS, SELECT_PLAYERS_JOURNAL, SELECT_PLAYERS_POINTS, SELECT_PLAYERS_OBJECTIVES, SELECT_PLAYERS_BACKPACK,

        LOAD_ALL_OBJECTIVES, LOAD_ALL_TAGS, LOAD_ALL_POINTS, LOAD_ALL_JOURNALS, LOAD_ALL_BACKPACK, LOAD_ALL_PLAYER,

        LOAD_ALL_GLOBAL_TAGS, LOAD_ALL_GLOBAL_POINTS
    }

    /**
     * Type of the update
     */
    public enum UpdateType {

        /**
         * Add the single objective to the database. PlayerID, objectiveID,
         * instruction.
         */
        ADD_OBJECTIVES,
        /**
         * Add the single tag to the database. PlayerID, tag.
         */
        ADD_TAGS,
        /**
         * Add the single global tag to the database. Tag.
         */
        ADD_GLOBAL_TAGS,
        /**
         * Add single point category to the database. PlayerID, category,
         * amount.
         */
        ADD_POINTS,
        /**
         * Add single global point category to the database. Category, amount.
         */
        ADD_GLOBAL_POINTS,
        /**
         * Add single journal entry to the database. PlayerID, pointer, date.
         */
        ADD_JOURNAL,
        /**
         * Add single itemstack to the database. PlayerID, instruction, amount.
         */
        ADD_BACKPACK,
        /**
         * Add single player to the database. PlayerID, language.
         */
        ADD_PLAYER,
        /**
         * Removes the single objective from the database. PlayerID,
         * objectiveID.
         */
        REMOVE_OBJECTIVES,
        /**
         * Removes the single tag from the database. PlayerID, tag.
         */
        REMOVE_TAGS,
        /**
         * Removes the single global tag from the database. Tag.
         */
        REMOVE_GLOBAL_TAGS,
        /**
         * Removes single point category from the database. PlayerID, category.
         */
        REMOVE_POINTS,
        /**
         * Removes single global point category from the database. Category.
         */
        REMOVE_GLOBAL_POINTS,
        /**
         * Removes single journal entry from the database. PlayerID, pointer,
         * date.
         */
        REMOVE_JOURNAL,

        DELETE_OBJECTIVES, DELETE_TAGS, DELETE_POINTS, DELETE_JOURNAL, DELETE_BACKPACK, DELETE_PLAYER,

        DELETE_GLOBAL_TAGS, DELETE_GLOBAL_POINTS,

        UPDATE_PLAYERS_OBJECTIVES, UPDATE_PLAYERS_TAGS, UPDATE_PLAYERS_POINTS, UPDATE_PLAYERS_JOURNAL, UPDATE_PLAYERS_BACKPACK,

        DROP_OBJECTIVES, DROP_TAGS, DROP_POINTS, DROP_JOURNALS, DROP_BACKPACK, DROP_PLAYER,

        DROP_GLOBAL_TAGS, DROP_GLOBAL_POINTS,

        INSERT_OBJECTIVE, INSERT_TAG, INSERT_POINT, INSERT_JOURNAL, INSERT_BACKPACK, INSERT_PLAYER,

        INSERT_GLOBAL_TAG, INSERT_GLOBAL_POINT,

        UPDATE_CONVERSATION, UPDATE_LANGUAGE,

        REMOVE_ALL_TAGS, REMOVE_ALL_OBJECTIVES, REMOVE_ALL_POINTS, REMOVE_ALL_ENTRIES, RENAME_ALL_TAGS, RENAME_ALL_OBJECTIVES, RENAME_ALL_POINTS, RENAME_ALL_GLOBAL_POINTS, RENAME_ALL_ENTRIES,
    }

}
