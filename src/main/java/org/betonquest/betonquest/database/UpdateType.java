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
     * Add single itemstack to the database. ProfileID, serialized item, amount.
     */
    ADD_BACKPACK(prefix -> "INSERT INTO " + prefix + "backpack (profileID, serialized, amount) VALUES (?, ?, ?);"),
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

    /**
     * Deletes all objectives from the database for a given profile. ProfileID.
     */
    DELETE_OBJECTIVES(prefix -> "DELETE FROM " + prefix + "objectives WHERE profileID = ?;"),
    /**
     * Deletes all tags from the database for a given profile. ProfileID.
     */
    DELETE_TAGS(prefix -> "DELETE FROM " + prefix + "tags WHERE profileID = ?;"),
    /**
     * Deletes all points from the database for a given profile. ProfileID.
     */
    DELETE_POINTS(prefix -> "DELETE FROM " + prefix + "points WHERE profileID = ?;"),
    /**
     * Deletes all journal entries from the database for a given profile. ProfileID.
     */
    DELETE_JOURNAL(prefix -> "DELETE FROM " + prefix + "journal WHERE profileID = ?;"),
    /**
     * Deletes all backpack items from the database for a given profile. ProfileID.
     */
    DELETE_BACKPACK(prefix -> "DELETE FROM " + prefix + "backpack WHERE profileID = ?;"),
    /**
     * Deletes the player from the database. PlayerID.
     */
    DELETE_PLAYER(prefix -> "DELETE FROM " + prefix + "player WHERE playerID = ?;"),

    /**
     * Deletes all global tags from the database.
     */
    DELETE_GLOBAL_TAGS(prefix -> "DELETE FROM " + prefix + "global_tags"),
    /**
     * Deletes all global points from the database.
     */
    DELETE_GLOBAL_POINTS(prefix -> "DELETE FROM " + prefix + "global_points"),

    /**
     * Updates the profileID of all objectives in the database for a given profile. ProfileID, ProfileID.
     */
    UPDATE_PLAYERS_OBJECTIVES(prefix -> "UPDATE " + prefix + "objectives SET profileID = ? WHERE profileID = ?;"),
    /**
     * Updates the profileID of all tags in the database for a given profile. ProfileID, ProfileID.
     */
    UPDATE_PLAYERS_TAGS(prefix -> "UPDATE " + prefix + "tags SET profileID = ? WHERE profileID = ?;"),
    /**
     * Updates the profileID of all points in the database for a given profile. ProfileID, ProfileID.
     */
    UPDATE_PLAYERS_POINTS(prefix -> "UPDATE " + prefix + "points SET profileID = ? WHERE profileID = ?;"),
    /**
     * Updates the profileID of all journal entries in the database for a given profile. ProfileID, ProfileID.
     */
    UPDATE_PLAYERS_JOURNAL(prefix -> "UPDATE " + prefix + "journal SET profileID = ? WHERE profileID = ?;"),
    /**
     * Updates the profileID of all backpack items in the database for a given profile. ProfileID, ProfileID.
     */
    UPDATE_PLAYERS_BACKPACK(prefix -> "UPDATE " + prefix + "backpack SET profileID = ? WHERE profileID = ?;"),
    /**
     * Updates the profileID's name in the database for a given profile. Name, ProfileID.
     */
    UPDATE_PROFILE_NAME(prefix -> "UPDATE " + prefix + "player_profile SET name = ? WHERE profileID = ?;"),
    /**
     * Updates the profileID's language in the database for a given profile. Language, ProfileID.
     */
    UPDATE_PLAYER_LANGUAGE(prefix -> "UPDATE " + prefix + "player SET language = ? WHERE playerID = ?;"),

    /**
     * Drops the objectives table.
     */
    DROP_OBJECTIVES(prefix -> "DROP TABLE " + prefix + "objectives"),
    /**
     * Drops the tags table.
     */
    DROP_TAGS(prefix -> "DROP TABLE " + prefix + "tags"),
    /**
     * Drops the points table.
     */
    DROP_POINTS(prefix -> "DROP TABLE " + prefix + "points"),
    /**
     * Drops the journal table.
     */
    DROP_JOURNALS(prefix -> "DROP TABLE " + prefix + "journal"),
    /**
     * Drops the backpack table.
     */
    DROP_BACKPACK(prefix -> "DROP TABLE " + prefix + "backpack"),
    /**
     * Drops the player table.
     */
    DROP_PLAYER(prefix -> "DROP TABLE " + prefix + "player"),
    /**
     * Drops the player profile table.
     */
    DROP_PLAYER_PROFILE(prefix -> "DROP TABLE " + prefix + "player_profile"),
    /**
     * Drops the profile table.
     */
    DROP_PROFILE(prefix -> "DROP TABLE " + prefix + "profile"),
    /**
     * Drops the migration table.
     */
    DROP_MIGRATION(prefix -> "DROP TABLE " + prefix + "migration"),

    /**
     * Drops the global tags table.
     */
    DROP_GLOBAL_TAGS(prefix -> "DROP TABLE " + prefix + "global_tags"),
    /**
     * Drops the global points table.
     */
    DROP_GLOBAL_POINTS(prefix -> "DROP TABLE " + prefix + "global_points"),

    /**
     * Inserts a new objective into the database. ProfileID, objective, instructions.
     */
    INSERT_OBJECTIVE(prefix -> "INSERT INTO " + prefix + "objectives (profileID, objective, instructions) VALUES (?,?,?)"),
    /**
     * Inserts a new tag into the database. ProfileID, tag.
     */
    INSERT_TAG(prefix -> "INSERT INTO " + prefix + "tags (profileID, tag) VALUES (?,?)"),
    /**
     * Inserts a new point into the database. ProfileID, category, count.
     */
    INSERT_POINT(prefix -> "INSERT INTO " + prefix + "points (profileID, category, count) VALUES (?,?,?)"),
    /**
     * Inserts a new journal entry into the database. ProfileID, pointer, date.
     */
    INSERT_JOURNAL(prefix -> "INSERT INTO " + prefix + "journal (id, profileID, pointer, date) VALUES (?,?,?,?)"),
    /**
     * Inserts a new backpack item into the database. ProfileID, serialized item, amount.
     */
    INSERT_BACKPACK(prefix -> "INSERT INTO " + prefix + "backpack (id, profileID, serialized, amount) VALUES (?,?,?,?)"),
    /**
     * Inserts a new player into the database. PlayerID, active_profile, language, conversation.
     */
    INSERT_PLAYER(prefix -> "INSERT INTO " + prefix + "player (playerID, active_profile, language, conversation) VALUES (?,?,?,?);"),
    /**
     * Inserts a new profile into the database. ProfileID.
     */
    INSERT_PROFILE(prefix -> "INSERT INTO " + prefix + "profile (profileID) VALUES (?);"),
    /**
     * Inserts a new player profile into the database. PlayerID, profileID, name.
     */
    INSERT_PLAYER_PROFILE(prefix -> "INSERT INTO " + prefix + "player_profile (playerID, profileID, name) VALUES (?,?,?);"),

    /**
     * Inserts a new global tag into the database. Tag.
     */
    INSERT_GLOBAL_TAG(prefix -> "INSERT INTO " + prefix + "global_tags (tag) VALUES (?)"),
    /**
     * Inserts a new global point into the database. Category, count.
     */
    INSERT_GLOBAL_POINT(prefix -> "INSERT INTO " + prefix + "global_points (category,count) VALUES (?,?)"),

    /**
     * Updates the conversation of a player in the database. Conversation, PlayerID.
     */
    UPDATE_CONVERSATION(prefix -> "UPDATE " + prefix + "player SET conversation = ? WHERE playerID = ?"),

    /**
     * Removes all tags from the database for a given tag. Tag.
     */
    REMOVE_ALL_TAGS(prefix -> "DELETE FROM " + prefix + "tags WHERE tag = ?;"),
    /**
     * Removes all objectives from the database for a given objective. Objective.
     */
    REMOVE_ALL_OBJECTIVES(prefix -> "DELETE FROM " + prefix + "objectives WHERE objective = ?;"),
    /**
     * Removes all points from the database for a given category. Category.
     */
    REMOVE_ALL_POINTS(prefix -> "DELETE FROM " + prefix + "points WHERE category = ?;"),
    /**
     * Removes all journal entries from the database for a given pointer. Pointer.
     */
    REMOVE_ALL_ENTRIES(prefix -> "DELETE FROM " + prefix + "journal WHERE pointer = ?;"),
    /**
     * Removes all tags from the database for a given global tag. Tag.
     */
    RENAME_ALL_TAGS(prefix -> "UPDATE " + prefix + "tags SET tag = ? WHERE tag = ?;"),
    /**
     * Renames all objectives in the database for a given objective. Objective, Objective.
     */
    RENAME_ALL_OBJECTIVES(prefix -> "UPDATE " + prefix + "objectives SET objective = ? WHERE objective = ?;"),
    /**
     * Renames all points in the database for a given category. Category, Category.
     */
    RENAME_ALL_POINTS(prefix -> "UPDATE " + prefix + "points SET category = ? WHERE category = ?;"),
    /**
     * Renames all global tags in the database for a given tag. Tag, Tag.
     */
    RENAME_ALL_GLOBAL_POINTS(prefix -> "UPDATE " + prefix + "global_points SET category = ? WHERE category = ?;"),
    /**
     * Renames all journal entries in the database for a given pointer. Pointer, Pointer.
     */
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
