package org.betonquest.betonquest.database;

import java.util.function.Function;

/**
 * Type of the query.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum QueryType {

    SELECT_OBJECTIVES(prefix -> "SELECT objective, instructions FROM " + prefix + "objectives WHERE profileID = ?;"),
    SELECT_TAGS(prefix -> "SELECT tag FROM " + prefix + "tags WHERE profileID = ?;"),
    SELECT_POINTS(prefix -> "SELECT category, count FROM " + prefix + "points WHERE profileID = ?;"),
    SELECT_JOURNAL(prefix -> "SELECT pointer, date FROM " + prefix + "journal WHERE profileID = ?;"),
    SELECT_BACKPACK(prefix -> "SELECT instruction, amount FROM " + prefix + "backpack WHERE profileID = ?;"),
    SELECT_PLAYER(prefix -> "SELECT language, conversation FROM " + prefix + "player WHERE playerID = ?;"),

    SELECT_PLAYERS_TAGS(prefix -> "SELECT profileID FROM " + prefix + "tags GROUP BY profileID;"),
    SELECT_PLAYERS_JOURNAL(prefix -> "SELECT profileID FROM " + prefix + "journal GROUP BY profileID;"),
    SELECT_PLAYERS_POINTS(prefix -> "SELECT profileID FROM " + prefix + "points GROUP BY profileID;"),
    SELECT_PLAYERS_OBJECTIVES(prefix -> "SELECT profileID FROM " + prefix + "objectives GROUP BY profileID;"),
    SELECT_PLAYERS_BACKPACK(prefix -> "SELECT profileID FROM " + prefix + "backpack GROUP BY profileID;"),

    SELECT_ACTIVE_PROFILE(prefix -> "SELECT active_profile FROM " + prefix + "player WHERE playerID = ?;"),
    SELECT_ALL_PROFILES_FROM_PLAYER(prefix -> "SELECT profileID FROM " + prefix + "player_profile WHERE playerID = ?;"),
    LOAD_ALL_PROFILES(prefix -> "SELECT profileID FROM " + prefix + "profile;"),

    LOAD_ALL_OBJECTIVES(prefix -> "SELECT * FROM " + prefix + "objectives"),
    LOAD_ALL_TAGS(prefix -> "SELECT * FROM " + prefix + "tags"),
    LOAD_ALL_POINTS(prefix -> "SELECT * FROM " + prefix + "points"),
    LOAD_ALL_JOURNALS(prefix -> "SELECT * FROM " + prefix + "journal"),
    LOAD_ALL_BACKPACK(prefix -> "SELECT * FROM " + prefix + "backpack"),
    LOAD_ALL_PLAYER(prefix -> "SELECT * FROM " + prefix + "player"),
    LOAD_ALL_MIGRATION(prefix -> "SELECT * FROM " + prefix + "migration"),
    LOAD_ALL_PLAYER_PROFILE(prefix -> "SELECT * FROM " + prefix + "player_profile"),
    LOAD_ALL_PROFILE(prefix -> "SELECT * FROM " + prefix + "profile"),

    LOAD_ALL_GLOBAL_TAGS(prefix -> "SELECT * FROM " + prefix + "global_tags"),
    LOAD_ALL_GLOBAL_POINTS(prefix -> "SELECT * FROM " + prefix + "global_points"),

    LOAD_TOP_X_POINTS_ASC(prefix -> "SELECT playerID,count FROM " + prefix + "points po join " + prefix + "player pl on po.profileID = pl.active_profile WHERE category = ? ORDER BY count ASC LIMIT ?;"),
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
