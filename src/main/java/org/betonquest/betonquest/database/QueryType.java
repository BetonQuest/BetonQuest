package org.betonquest.betonquest.database;

import java.util.function.Function;

/**
 * Type of the query.
 */
public enum QueryType {
    /**
     * Get the objectives of a profile. ProfileID.
     */
    SELECT_OBJECTIVES(prefix -> "SELECT objective, instructions FROM " + prefix + "objectives WHERE profileID = ?;"),
    /**
     * Get the tags of a profile. ProfileID.
     */
    SELECT_TAGS(prefix -> "SELECT tag FROM " + prefix + "tags WHERE profileID = ?;"),
    /**
     * Get the points of a profile. ProfileID.
     */
    SELECT_POINTS(prefix -> "SELECT category, count FROM " + prefix + "points WHERE profileID = ?;"),
    /**
     * Get the journal entries of a profile. ProfileID.
     */
    SELECT_JOURNAL(prefix -> "SELECT pointer, date FROM " + prefix + "journal WHERE profileID = ?;"),
    /**
     * Get the backpack items of a profile. ProfileID.
     */
    SELECT_BACKPACK(prefix -> "SELECT serialized, amount FROM " + prefix + "backpack WHERE profileID = ?;"),
    /**
     * Get the current language and conversation of a profile. ProfileID.
     */
    SELECT_PLAYER(prefix -> "SELECT language, conversation FROM " + prefix + "player WHERE playerID = ?;"),

    /**
     * Get all global tags.
     */
    LOAD_ALL_GLOBAL_TAGS(prefix -> "SELECT * FROM " + prefix + "global_tags"),
    /**
     * Get all global points.
     */
    LOAD_ALL_GLOBAL_POINTS(prefix -> "SELECT * FROM " + prefix + "global_points"),

    /**
     * Get all points of a category ordered ascending with a limit. Category, limit.
     */
    LOAD_TOP_X_POINTS_ASC(prefix -> "SELECT playerID,count FROM " + prefix + "points po join " + prefix + "player pl on po.profileID = pl.active_profile WHERE category = ? ORDER BY count ASC LIMIT ?;"),
    /**
     * Get all points of a category ordered descending with a limit. Category, limit.
     */
    LOAD_TOP_X_POINTS_DESC(prefix -> "SELECT playerID,count FROM " + prefix + "points po join " + prefix + "player pl on po.profileID = pl.active_profile WHERE category = ? ORDER BY count DESC LIMIT ?;");

    /**
     * Function to create the SQL code from a prefix.
     */
    private final Function<String, String> statementCreator;

    QueryType(final Function<String, String> sqlTemplate) {
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
