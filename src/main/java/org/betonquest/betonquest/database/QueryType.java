package org.betonquest.betonquest.database;

import java.util.function.Function;

/**
 * Type of the query.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum QueryType {

    SELECT_OBJECTIVES(prefix -> "SELECT objective, instructions FROM " + prefix + "objectives WHERE playerID = ?;"),
    SELECT_TAGS(prefix -> "SELECT tag FROM " + prefix + "tags WHERE playerID = ?;"),
    SELECT_POINTS(prefix -> "SELECT category, count FROM " + prefix + "points WHERE playerID = ?;"),
    SELECT_JOURNAL(prefix -> "SELECT pointer, date FROM " + prefix + "journal WHERE playerID = ?;"),
    SELECT_BACKPACK(prefix -> "SELECT instruction, amount FROM " + prefix + "backpack WHERE playerID = ?;"),
    SELECT_PLAYER(prefix -> "SELECT language, conversation FROM " + prefix + "player WHERE playerID = ?;"),

    SELECT_PLAYERS_TAGS(prefix -> "SELECT playerID FROM " + prefix + "tags GROUP BY playerID;"),
    SELECT_PLAYERS_JOURNAL(prefix -> "SELECT playerID FROM " + prefix + "journal GROUP BY playerID;"),
    SELECT_PLAYERS_POINTS(prefix -> "SELECT playerID FROM " + prefix + "points GROUP BY playerID;"),
    SELECT_PLAYERS_OBJECTIVES(prefix -> "SELECT playerID FROM " + prefix + "objectives GROUP BY playerID;"),
    SELECT_PLAYERS_BACKPACK(prefix -> "SELECT playerID FROM " + prefix + "backpack GROUP BY playerID;"),

    LOAD_ALL_OBJECTIVES(prefix -> "SELECT * FROM " + prefix + "objectives"),
    LOAD_ALL_TAGS(prefix -> "SELECT * FROM " + prefix + "tags"),
    LOAD_ALL_POINTS(prefix -> "SELECT * FROM " + prefix + "points"),
    LOAD_ALL_JOURNALS(prefix -> "SELECT * FROM " + prefix + "journal"),
    LOAD_ALL_BACKPACK(prefix -> "SELECT * FROM " + prefix + "backpack"),
    LOAD_ALL_PLAYER(prefix -> "SELECT * FROM " + prefix + "player"),

    LOAD_ALL_GLOBAL_TAGS(prefix -> "SELECT * FROM " + prefix + "global_tags"),
    LOAD_ALL_GLOBAL_POINTS(prefix -> "SELECT * FROM " + prefix + "global_points"),

    LOAD_TOP_X_POINTS_ASC(prefix -> "SELECT * FROM " + prefix + "points WHERE category = ? ORDER BY count ASC LIMIT ?;"),

    LOAD_TOP_X_POINTS_DESC(prefix -> "SELECT * FROM " + prefix + "points WHERE category = ? ORDER BY count DESC LIMIT ?;");

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
