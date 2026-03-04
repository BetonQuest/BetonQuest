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
import java.util.stream.Collectors;

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
    private final Map<String, Point> globalPoints = new HashMap<>();

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
        connector.querySQL(QueryType.LOAD_ALL_GLOBAL_TAGS, new Arguments(), resultSet -> {
            while (resultSet.next()) {
                this.globalTags.add(resultSet.getString("tag"));
            }
        }, "Could not load global tags.");
        connector.querySQL(QueryType.LOAD_ALL_GLOBAL_POINTS, new Arguments(), resultSet -> {
            while (resultSet.next()) {
                final String category = resultSet.getString("category");
                this.globalPoints.put(category, new Point(category, resultSet.getInt("count")));
            }
        }, "Could not load global points.");

        log.debug("There are " + this.globalTags.size() + " global_tags and " + this.globalPoints.size()
                + " global_points loaded");
    }

    /**
     * Purges all global tags from the database and from this object.
     */
    public void purgeTags() {
        // clear all lists
        globalTags.clear();
        // clear the database
        saver.add(new Record(UpdateType.DELETE_GLOBAL_TAGS));
    }

    /**
     * Purges all global points from the database and from this object.
     */
    public void purgePoints() {
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
    private class GlobalDataTagHolder implements TagHolder {

        /**
         * Creates a new instance of GlobalDataTagHolder.
         */
        public GlobalDataTagHolder() {
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
                saver.add(new Record(UpdateType.ADD_GLOBAL_TAGS, tag));
            }
        }

        @Override
        public void remove(final String tag) {
            globalTags.remove(tag);
            saver.add(new Record(UpdateType.REMOVE_GLOBAL_TAGS, tag));
        }
    }

    /**
     * An implementation of {@link PointHolder} for {@link GlobalData}.
     */
    private class GlobalDataPointHolder implements PointHolder {

        /**
         * Creates a new instance of GlobalDataPointHolder.
         */
        private GlobalDataPointHolder() {
        }

        @Override
        public Map<String, Integer> get() {
            return globalPoints.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getCount()));
        }

        @Override
        public boolean has(final String category) {
            return get(category).isPresent();
        }

        @Override
        public Optional<Integer> get(final String category) {
            final Point point = globalPoints.get(category);
            if (point != null) {
                return Optional.of(point.getCount());
            }
            return Optional.empty();
        }

        @Override
        public void set(final String category, final int points) {
            saver.add(new Record(UpdateType.REMOVE_GLOBAL_POINTS, category));
            globalPoints.put(category, new Point(category, points));
            saver.add(new Record(UpdateType.ADD_GLOBAL_POINTS, category, String.valueOf(points)));
        }

        @Override
        public void add(final String category, final int points) {
            saver.add(new Record(UpdateType.REMOVE_GLOBAL_POINTS, category));
            final Point point = globalPoints.computeIfAbsent(category, cat -> new Point(category, 0));
            point.addPoints(points);
            saver.add(new Record(UpdateType.ADD_GLOBAL_POINTS, category, String.valueOf(point.getCount())));
        }

        @Override
        public void remove(final String category) {
            globalPoints.remove(category);
            saver.add(new Record(UpdateType.REMOVE_GLOBAL_POINTS, category));
        }
    }
}
