package org.betonquest.betonquest.database;

import java.util.function.Function;

/**
 * Type of the update.
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.ExcessivePublicCount"})
public enum UpdateType {

    /**
     * Add the single objective to the database. ProfileID, objectiveID,
     * instruction.
     */
    ADD_OBJECTIVES(prefix -> "INSERT INTO " + prefix + "objectives (profileID, objective, instructions) VALUES (?, ?, ?);"),
    /**
     * Add the single tag to the database. ProfileID, tag.
     */
    ADD_TAGS(prefix -> "INSERT INTO " + prefix + "tags (profileID, tag) VALUES (?, ?);"),
    /**
     * Add the single global tag to the database. Tag.
     */
    ADD_GLOBAL_TAGS(prefix -> "INSERT INTO " + prefix + "global_tags (tag) VALUES (?);"),
    /**
     * Add single point category to the database. ProfileID, category,
     * amount.
     */
    ADD_POINTS(prefix -> "INSERT INTO " + prefix + "points (profileID, category, count) VALUES (?, ?, ?);"),
    /**
     * Add single global point category to the database. Category, amount.
     */
    ADD_GLOBAL_POINTS(prefix -> "INSERT INTO " + prefix + "global_points (category, count) VALUES (?, ?);"),
    /**
     * Add single journal entry to the database. ProfileID, pointer, date.
     */
    ADD_JOURNAL(prefix -> "INSERT INTO " + prefix + "journal (profileID, pointer, date) VALUES (?, ?, ?);"),
    /**
     * Add single itemstack to the database. ProfileID, instruction, amount.
     */
    ADD_BACKPACK(prefix -> "INSERT INTO " + prefix + "backpack (profileID, instruction, amount) VALUES (?, ?, ?);"),
    /**
     * Add single player to the database. PlayerID, active_profile, language.
     */
    ADD_PLAYER(prefix -> "INSERT INTO " + prefix + "player (playerID, active_profile, language) VALUES (?, ?, ?);"),
    /**
     * Add single profile to the database. ProfileID.
     */
    ADD_PROFILE(prefix -> "INSERT INTO " + prefix + "profile (profileID) VALUES (?);"),
    /**
     * Add single player profile to the database. PlayerID, profileID, name.
     */
    ADD_PLAYER_PROFILE(prefix -> "INSERT INTO " + prefix + "player_profile (playerID, profileID, name) VALUES (?, ?, ?);"),
    /**
     * Removes the single objective from the database. ProfileID,
     * objectiveID.
     */
    REMOVE_OBJECTIVES(prefix -> "DELETE FROM " + prefix + "objectives WHERE profileID = ? AND objective = ?;"),
    /**
     * Removes the single tag from the database. ProfileID, tag.
     */
    REMOVE_TAGS(prefix -> "DELETE FROM " + prefix + "tags WHERE profileID = ? AND tag = ?;"),
    /**
     * Removes the single global tag from the database. Tag.
     */
    REMOVE_GLOBAL_TAGS(prefix -> "DELETE FROM " + prefix + "global_tags WHERE tag = ?;"),
    /**
     * Removes single point category from the database. ProfileID, category.
     */
    REMOVE_POINTS(prefix -> "DELETE FROM " + prefix + "points WHERE profileID = ? AND category = ?;"),
    /**
     * Removes single global point category from the database. Category.
     */
    REMOVE_GLOBAL_POINTS(prefix -> "DELETE FROM " + prefix + "global_points WHERE category = ?;"),
    /**
     * Removes single journal entry from the database. ProfileID, pointer,
     * date.
     */
    REMOVE_JOURNAL(prefix -> "DELETE FROM " + prefix + "journal WHERE profileID = ? AND pointer = ? AND date = ?;"),
    /**
     * Removes single profile from the database. ProfileID.
     */
    REMOVE_PROFILE(prefix -> "DELETE FROM " + prefix + "profile WHERE profileID = ?;"),
    /**
     * Removes single player profile from the database. ProfileID.
     */
    REMOVE_PLAYER_PROFILE(prefix -> "DELETE FROM " + prefix + "player_profile WHERE profileID = ?;"),

    DELETE_OBJECTIVES(prefix -> "DELETE FROM " + prefix + "objectives WHERE profileID = ?;"),
    DELETE_TAGS(prefix -> "DELETE FROM " + prefix + "tags WHERE profileID = ?;"),
    DELETE_POINTS(prefix -> "DELETE FROM " + prefix + "points WHERE profileID = ?;"),
    DELETE_JOURNAL(prefix -> "DELETE FROM " + prefix + "journal WHERE profileID = ?;"),
    DELETE_BACKPACK(prefix -> "DELETE FROM " + prefix + "backpack WHERE profileID = ?;"),
    DELETE_PLAYER(prefix -> "DELETE FROM " + prefix + "player WHERE playerID = ?;"),

    DELETE_GLOBAL_TAGS(prefix -> "DELETE FROM " + prefix + "global_tags"),
    DELETE_GLOBAL_POINTS(prefix -> "DELETE FROM " + prefix + "global_points"),

    UPDATE_PLAYERS_OBJECTIVES(prefix -> "UPDATE " + prefix + "objectives SET profileID = ? WHERE profileID = ?;"),
    UPDATE_PLAYERS_TAGS(prefix -> "UPDATE " + prefix + "tags SET profileID = ? WHERE profileID = ?;"),
    UPDATE_PLAYERS_POINTS(prefix -> "UPDATE " + prefix + "points SET profileID = ? WHERE profileID = ?;"),
    UPDATE_PLAYERS_JOURNAL(prefix -> "UPDATE " + prefix + "journal SET profileID = ? WHERE profileID = ?;"),
    UPDATE_PLAYERS_BACKPACK(prefix -> "UPDATE " + prefix + "backpack SET profileID = ? WHERE profileID = ?;"),
    UPDATE_PROFILE_NAME(prefix -> "UPDATE " + prefix + "player_profile SET name = ? WHERE profileID = ?;"),
    UPDATE_PLAYER_LANGUAGE(prefix -> "UPDATE " + prefix + "player SET language = ? WHERE playerID = ?;"),

    DROP_OBJECTIVES(prefix -> "DROP TABLE " + prefix + "objectives"),
    DROP_TAGS(prefix -> "DROP TABLE " + prefix + "tags"),
    DROP_POINTS(prefix -> "DROP TABLE " + prefix + "points"),
    DROP_JOURNALS(prefix -> "DROP TABLE " + prefix + "journal"),
    DROP_BACKPACK(prefix -> "DROP TABLE " + prefix + "backpack"),
    DROP_PLAYER(prefix -> "DROP TABLE " + prefix + "player"),
    DROP_PLAYER_PROFILE(prefix -> "DROP TABLE " + prefix + "player_profile"),
    DROP_PROFILE(prefix -> "DROP TABLE " + prefix + "profile"),
    DROP_MIRGATION(prefix -> "DROP TABLE " + prefix + "migration"),

    DROP_GLOBAL_TAGS(prefix -> "DROP TABLE " + prefix + "global_tags"),
    DROP_GLOBAL_POINTS(prefix -> "DROP TABLE " + prefix + "global_points"),

    INSERT_OBJECTIVE(prefix -> "INSERT INTO " + prefix + "objectives (profileID, objective, instructions) VALUES (?,?,?)"),
    INSERT_TAG(prefix -> "INSERT INTO " + prefix + "tags (profileID, tag) VALUES (?,?)"),
    INSERT_POINT(prefix -> "INSERT INTO " + prefix + "points (profileID, category, count) VALUES (?,?,?)"),
    INSERT_JOURNAL(prefix -> "INSERT INTO " + prefix + "journal (id, profileID, pointer, date) VALUES (?,?,?,?)"),
    INSERT_BACKPACK(prefix -> "INSERT INTO " + prefix + "backpack (id, profileID, instruction, amount) VALUES (?,?,?,?)"),
    INSERT_PLAYER(prefix -> "INSERT INTO " + prefix + "player (playerID, active_profile, language, conversation) VALUES (?,?,?,?);"),
    INSERT_PROFILE(prefix -> "INSERT INTO " + prefix + "profile (profileID) VALUES (?);"),
    INSERT_PLAYER_PROFILE(prefix -> "INSERT INTO " + prefix + "player_profile (playerID, profileID, name) VALUES (?,?,?);"),

    INSERT_GLOBAL_TAG(prefix -> "INSERT INTO " + prefix + "global_tags (tag) VALUES (?)"),
    INSERT_GLOBAL_POINT(prefix -> "INSERT INTO " + prefix + "global_points (category,count) VALUES (?,?)"),

    UPDATE_CONVERSATION(prefix -> "UPDATE " + prefix + "player SET conversation = ? WHERE playerID = ?"),

    REMOVE_ALL_TAGS(prefix -> "DELETE FROM " + prefix + "tags WHERE tag = ?;"),
    REMOVE_ALL_OBJECTIVES(prefix -> "DELETE FROM " + prefix + "objectives WHERE objective = ?;"),
    REMOVE_ALL_POINTS(prefix -> "DELETE FROM " + prefix + "points WHERE category = ?;"),
    REMOVE_ALL_ENTRIES(prefix -> "DELETE FROM " + prefix + "journal WHERE pointer = ?;"),
    RENAME_ALL_TAGS(prefix -> "UPDATE " + prefix + "tags SET tag = ? WHERE tag = ?;"),
    RENAME_ALL_OBJECTIVES(prefix -> "UPDATE " + prefix + "objectives SET objective = ? WHERE objective = ?;"),
    RENAME_ALL_POINTS(prefix -> "UPDATE " + prefix + "points SET category = ? WHERE category = ?;"),
    RENAME_ALL_GLOBAL_POINTS(prefix -> "UPDATE " + prefix + "global_points SET category = ? WHERE category = ?;"),
    RENAME_ALL_ENTRIES(prefix -> "UPDATE " + prefix + "journal SET pointer = ? WHERE pointer = ?;");

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
