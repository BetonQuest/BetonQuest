package org.betonquest.betonquest.database;

import java.util.function.Function;

/**
 * Type of the update.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum UpdateType {

    /**
     * Add the single objective to the database. PlayerID, objectiveID,
     * instruction.
     */
    ADD_OBJECTIVES(prefix -> "INSERT INTO " + prefix + "objectives (playerID, objective, instructions) VALUES (?, ?, ?);"),
    /**
     * Add the single tag to the database. PlayerID, tag.
     */
    ADD_TAGS(prefix -> "INSERT INTO " + prefix + "tags (playerID, tag) VALUES (?, ?);"),
    /**
     * Add the single global tag to the database. Tag.
     */
    ADD_GLOBAL_TAGS(prefix -> "INSERT INTO " + prefix + "global_tags (tag) VALUES (?);"),
    /**
     * Add single point category to the database. PlayerID, category,
     * amount.
     */
    ADD_POINTS(prefix -> "INSERT INTO " + prefix + "points (playerID, category, count) VALUES (?, ?, ?);"),
    /**
     * Add single global point category to the database. Category, amount.
     */
    ADD_GLOBAL_POINTS(prefix -> "INSERT INTO " + prefix + "global_points (category, count) VALUES (?, ?);"),
    /**
     * Add single journal entry to the database. PlayerID, pointer, date.
     */
    ADD_JOURNAL(prefix -> "INSERT INTO " + prefix + "journal (playerID, pointer, date) VALUES (?, ?, ?);"),
    /**
     * Add single itemstack to the database. PlayerID, instruction, amount.
     */
    ADD_BACKPACK(prefix -> "INSERT INTO " + prefix + "backpack (playerID, instruction, amount) VALUES (?, ?, ?);"),
    /**
     * Add single player to the database. PlayerID, language.
     */
    ADD_PLAYER(prefix -> "INSERT INTO " + prefix + "player (playerID, language) VALUES (?, ?);"),
    /**
     * Removes the single objective from the database. PlayerID,
     * objectiveID.
     */
    REMOVE_OBJECTIVES(prefix -> "DELETE FROM " + prefix + "objectives WHERE playerID = ? AND objective = ?;"),
    /**
     * Removes the single tag from the database. PlayerID, tag.
     */
    REMOVE_TAGS(prefix -> "DELETE FROM " + prefix + "tags WHERE playerID = ? AND tag = ?;"),
    /**
     * Removes the single global tag from the database. Tag.
     */
    REMOVE_GLOBAL_TAGS(prefix -> "DELETE FROM " + prefix + "global_tags WHERE tag = ?;"),
    /**
     * Removes single point category from the database. PlayerID, category.
     */
    REMOVE_POINTS(prefix -> "DELETE FROM " + prefix + "points WHERE playerID = ? AND category = ?;"),
    /**
     * Removes single global point category from the database. Category.
     */
    REMOVE_GLOBAL_POINTS(prefix -> "DELETE FROM " + prefix + "global_points WHERE category = ?;"),
    /**
     * Removes single journal entry from the database. PlayerID, pointer,
     * date.
     */
    REMOVE_JOURNAL(prefix -> "DELETE FROM " + prefix + "journal WHERE playerID = ? AND pointer = ? AND date = ?;"),

    DELETE_OBJECTIVES(prefix -> "DELETE FROM " + prefix + "objectives WHERE playerID = ?;"),
    DELETE_TAGS(prefix -> "DELETE FROM " + prefix + "tags WHERE playerID = ?;"),
    DELETE_POINTS(prefix -> "DELETE FROM " + prefix + "points WHERE playerID = ?;"),
    DELETE_JOURNAL(prefix -> "DELETE FROM " + prefix + "journal WHERE playerID = ?;"),
    DELETE_BACKPACK(prefix -> "DELETE FROM " + prefix + "backpack WHERE playerID = ?;"),
    DELETE_PLAYER(prefix -> "DELETE FROM " + prefix + "player WHERE playerID = ?;"),

    DELETE_GLOBAL_TAGS(prefix -> "DELETE FROM " + prefix + "global_tags"),
    DELETE_GLOBAL_POINTS(prefix -> "DELETE FROM " + prefix + "global_points"),

    UPDATE_PLAYERS_OBJECTIVES(prefix -> "UPDATE " + prefix + "objectives SET playerID = ? WHERE playerID = ?;"),
    UPDATE_PLAYERS_TAGS(prefix -> "UPDATE " + prefix + "tags SET playerID = ? WHERE playerID = ?;"),
    UPDATE_PLAYERS_POINTS(prefix -> "UPDATE " + prefix + "points SET playerID = ? WHERE playerID = ?;"),
    UPDATE_PLAYERS_JOURNAL(prefix -> "UPDATE " + prefix + "journal SET playerID = ? WHERE playerID = ?;"),
    UPDATE_PLAYERS_BACKPACK(prefix -> "UPDATE " + prefix + "backpack SET playerID = ? WHERE playerID = ?;"),

    DROP_OBJECTIVES(prefix -> "DROP TABLE " + prefix + "objectives"),
    DROP_TAGS(prefix -> "DROP TABLE " + prefix + "tags"),
    DROP_POINTS(prefix -> "DROP TABLE " + prefix + "points"),
    DROP_JOURNALS(prefix -> "DROP TABLE " + prefix + "journal"),
    DROP_BACKPACK(prefix -> "DROP TABLE " + prefix + "backpack"),
    DROP_PLAYER(prefix -> "DROP TABLE " + prefix + "player"),

    DROP_GLOBAL_TAGS(prefix -> "DROP TABLE " + prefix + "global_tags"),
    DROP_GLOBAL_POINTS(prefix -> "DROP TABLE " + prefix + "global_points"),

    INSERT_OBJECTIVE(prefix -> "INSERT INTO " + prefix + "objectives VALUES (?,?,?,?)"),
    INSERT_TAG(prefix -> "INSERT INTO " + prefix + "tags VALUES (?,?,?)"),
    INSERT_POINT(prefix -> "INSERT INTO " + prefix + "points VALUES (?,?,?,?)"),
    INSERT_JOURNAL(prefix -> "INSERT INTO " + prefix + "journal VALUES (?,?,?,?)"),
    INSERT_BACKPACK(prefix -> "INSERT INTO " + prefix + "backpack VALUES (?,?,?,?)"),
    INSERT_PLAYER(prefix -> "INSERT INTO " + prefix + "player VALUES (?,?,?,?);"),

    INSERT_GLOBAL_TAG(prefix -> "INSERT INTO " + prefix + "global_tags VALUES (?,?)"),
    INSERT_GLOBAL_POINT(prefix -> "INSERT INTO " + prefix + "global_points VALUES (?,?,?)"),

    UPDATE_CONVERSATION(prefix -> "UPDATE " + prefix + "player SET conversation = ? WHERE playerID = ?"),

    REMOVE_ALL_TAGS(prefix -> "DELETE FROM " + prefix + "tags WHERE tag = ?;"),
    REMOVE_ALL_OBJECTIVES(prefix -> "DELETE FROM " + prefix + "objectives WHERE objective = ?;"),
    REMOVE_ALL_POINTS(prefix -> "DELETE FROM " + prefix + "points WHERE category = ?;"),
    REMOVE_ALL_ENTRIES(prefix -> "DELETE FROM " + prefix + "journal WHERE pointer = ?;"),
    RENAME_ALL_TAGS(prefix -> "UPDATE " + prefix + "tags SET tag = ? WHERE tag = ?;"),
    RENAME_ALL_OBJECTIVES(prefix -> "UPDATE " + prefix + "objectives SET objective = ? WHERE objective = ?;"),
    RENAME_ALL_POINTS(prefix -> "UPDATE " + prefix + "points SET category = ? WHERE category = ?;"),
    RENAME_ALL_GLOBAL_POINTS(prefix -> "UPDATE " + prefix + "global_points SET category = ? WHERE category = ?;"),
    RENAME_ALL_ENTRIES(prefix -> "UPDATE " + prefix + "journal SET pointer = ? WHERE pointer = ?;"),
    ;

    /**
     * Function to create the SQL code from a prefix.
     */
    private final Function<String, String> statementCreator;

    UpdateType(final Function<String, String> sqlTemplate) {
        this.statementCreator = sqlTemplate;
    }

    /**
     * Create the SQL code for the given table prefix.
     *
     * @param tablePrefix table prefix to use
     * @return SQL-code for the update
     */
    public String createSql(final String tablePrefix) {
        return statementCreator.apply(tablePrefix);
    }
}
