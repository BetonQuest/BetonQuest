package org.betonquest.betonquest.compatibility.holograms.lines;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.QueryType;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Holds data of a ranking.
 */
public class TopXObject {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Number of lines stored in maximum.
     */
    private final int limit;

    /**
     * Name of BetonQuest point.
     */
    private final String category;

    /**
     * Direction after which the scores are ordered.
     */
    private final OrderType orderType;

    /**
     * Entries of last database request. Will not exceed {@link #limit}.
     */
    private final List<TopXLine> entries = new ArrayList<>();

    /**
     * Creates new instance of TopXObject.
     *
     * @param log       the logger that will be used for logging
     * @param limit     Number of lines
     * @param category  Name of point
     * @param orderType Direction of order
     */
    public TopXObject(final BetonQuestLogger log, final int limit, final String category, final OrderType orderType) {
        this.log = log;
        this.category = category;
        this.limit = limit;
        this.orderType = orderType;
    }

    /**
     * Returns the entries of the last database request.
     *
     * @return List of {@link TopXLine} entries.
     */
    public List<TopXLine> getEntries() {
        return entries;
    }

    /**
     * Updates the currently stored entries with data from the database.
     */
    public void queryDB() {
        entries.clear();
        final Connector con = new Connector();

        try (ResultSet resultSet = con.querySQL(orderType.getType(), statement -> {
            statement.setString(1, category);
            statement.setInt(2, limit);
        })) {
            while (resultSet.next()) {
                final String playerName = Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("playerID"))).getName();
                entries.add(new TopXLine(playerName, resultSet.getLong("count")));
            }
        } catch (final SQLException e) {
            log.error("There was an SQL exception while querying the top " + limit, e);
        }
    }

    /**
     * Returns the number of lines actually retrieved from the database. May differ from {@link #limit} when not enough
     * players have had alterations to that point made. Can be 0 under the following circumstances:
     * <ul>
     *     <li>{@link #queryDB()} has not been called on this object</li>
     *     <li>No player has had alterations to the specified point</li>
     *     <li>Specified point does not exist</li>
     * </ul>
     *
     * @return Amount of lines
     */
    public int getLineCount() {
        return entries.size();
    }

    /**
     * Order in which the scores are sorted by {@link #queryDB()}.
     */
    public enum OrderType {
        /**
         * From largest to smallest. Default.
         */
        DESCENDING(QueryType.LOAD_TOP_X_POINTS_DESC),

        /**
         * From smallest to largest.
         */
        ASCENDING(QueryType.LOAD_TOP_X_POINTS_ASC);

        /**
         * The {@link QueryType} for descending or ascending order.
         */
        private final QueryType type;

        /**
         * Creates a new instance of OrderType.
         *
         * @param type The {@link QueryType} for descending or ascending order.
         */
        OrderType(final QueryType type) {
            this.type = type;
        }

        /**
         * Returns the {@link QueryType}.
         *
         * @return The {@link QueryType} for descending or ascending order.
         */
        public QueryType getType() {
            return type;
        }
    }
}
