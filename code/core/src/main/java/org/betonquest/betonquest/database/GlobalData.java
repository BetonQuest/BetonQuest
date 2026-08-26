package org.betonquest.betonquest.database;

import org.betonquest.betonquest.api.data.PersistentDataHolder;
import org.betonquest.betonquest.api.data.PointHolder;
import org.betonquest.betonquest.api.data.TagHolder;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.database.Saver.Record;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents an object storing all player-related data, which can load and save it.
 */
public class GlobalData implements PersistentDataHolder {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The database saver for player data.
     */
    private final Saver saver;

    /**
     * The set global tags.
     */
    private final Set<String> globalTags = new HashSet<>();

    /**
     * The set global points.
     */
    private final Map<String, Integer> globalPoints = new HashMap<>();

    /**
     * Loads all global data from the database.
     *
     * @param log       the custom logger for this class
     * @param saver     the saver for player data
     * @param connector the connector for database access
     */
    public GlobalData(final BetonQuestLogger log, final Saver saver, final Connector connector) {
        this.log = log;
        this.saver = saver;
        loadAllGlobalData(connector);
    }

    /**
     * Loads all data for the player and puts it in appropriate lists.
     *
     * @param connector the connector for database access
     */
    public final void loadAllGlobalData(final Connector connector) {
        log.debug("Loading all global data from database...");
        connector.querySQL(QueryType.LOAD_ALL_GLOBAL_TAGS, new Arguments(), resultSet -> {
            while (resultSet.next()) {
                this.globalTags.add(resultSet.getString("tag"));
            }
            log.debug("Loaded %d global tags from database".formatted(this.globalTags.size()));
        }, "Could not load global tags.");
        connector.querySQL(QueryType.LOAD_ALL_GLOBAL_POINTS, new Arguments(), resultSet -> {
            while (resultSet.next()) {
                final String category = resultSet.getString("category");
                this.globalPoints.put(category, resultSet.getInt("count"));
            }
            log.debug("Loaded %d global points from database".formatted(this.globalPoints.size()));
        }, "Could not load global points.");
    }

    /**
     * Purges all global tags from the database and from this object.
     */
    public void purgeTags() {
        log.debug("Purging all global tags...");
        // clear all lists
        globalTags.clear();
        // clear the database
        saver.add(new Record(UpdateType.DELETE_GLOBAL_TAGS));
    }

    /**
     * Purges all global points from the database and from this object.
     */
    public void purgePoints() {
        log.debug("Purging all global points...");
        // clear all lists
        globalPoints.clear();
        // clear the database
        saver.add(new Record(UpdateType.DELETE_GLOBAL_POINTS));
    }

    @Override
    public PointHolder points() {
        return new GlobalDataPointHolder();
    }

    @Override
    public TagHolder tags() {
        return new GlobalDataTagHolder();
    }

    /**
     * An implementation of {@link TagHolder} for {@link GlobalData}.
     */
    private final class GlobalDataTagHolder implements TagHolder {

        /**
         * Creates a new instance of GlobalDataTagHolder.
         */
        private GlobalDataTagHolder() {
        }

        @Override
        public Set<String> get() {
            return globalTags;
        }

        @Override
        public boolean has(final String tag) {
            return globalTags.contains(tag);
        }

        @Override
        public void add(final String tag) {
            if (globalTags.add(tag)) {
                log.debug("Adding global tag '%s'".formatted(tag));
                saver.add(new Record(UpdateType.ADD_GLOBAL_TAGS, tag));
            }
        }

        @Override
        public void remove(final String tag) {
            log.debug("Removing global tag '%s'".formatted(tag));
            globalTags.remove(tag);
            saver.add(new Record(UpdateType.REMOVE_GLOBAL_TAGS, tag));
        }
    }

    /**
     * An implementation of {@link PointHolder} for {@link GlobalData}.
     */
    private final class GlobalDataPointHolder implements PointHolder {

        /**
         * Creates a new instance of GlobalDataPointHolder.
         */
        private GlobalDataPointHolder() {
        }

        @Override
        public Map<String, Integer> get() {
            return globalPoints;
        }

        @Override
        public boolean has(final String category) {
            return globalPoints.containsKey(category);
        }

        @Override
        public Optional<Integer> get(final String category) {
            return Optional.ofNullable(globalPoints.get(category));
        }

        @Override
        public void set(final String category, final int points) {
            log.debug("Setting global points in category '%s' to %d".formatted(category, points));
            saver.add(new Record(UpdateType.REMOVE_GLOBAL_POINTS, category));
            globalPoints.put(category, points);
            saver.add(new Record(UpdateType.ADD_GLOBAL_POINTS, category, String.valueOf(points)));
        }

        @Override
        public void add(final String category, final int points) {
            log.debug("Adding %d global points in category '%s'".formatted(points, category));
            saver.add(new Record(UpdateType.REMOVE_GLOBAL_POINTS, category));
            final Integer newPoints = globalPoints.compute(category, (key, value) -> (value == null ? 0 : value) + points);
            saver.add(new Record(UpdateType.ADD_GLOBAL_POINTS, category, String.valueOf(newPoints)));
        }

        @Override
        public void remove(final String category) {
            log.debug("Removing global points category '%s'".formatted(category));
            globalPoints.remove(category);
            saver.add(new Record(UpdateType.REMOVE_GLOBAL_POINTS, category));
        }
    }
}
