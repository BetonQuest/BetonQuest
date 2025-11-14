package org.betonquest.betonquest.database;

import org.betonquest.betonquest.Point;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.database.Saver.Record;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents an object storing all player-related data, which can load and save it.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class GlobalData implements TagData, PointData {
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
    private final List<String> globalTags = new ArrayList<>();

    /**
     * The set global points.
     */
    private final Map<String, Point> globalPoints = new HashMap<>();

    /**
     * Loads all global data from the database.
     *
     * @param log   the custom logger for this class
     * @param saver the saver for player data
     */
    public GlobalData(final BetonQuestLogger log, final Saver saver) {
        this.log = log;
        this.saver = saver;
        loadAllGlobalData();
    }

    /**
     * Loads all data for the player and puts it in appropriate lists.
     */
    public final void loadAllGlobalData() {
        final Connector con = new Connector();
        try (ResultSet globalTags = con.querySQL(QueryType.LOAD_ALL_GLOBAL_TAGS);
             ResultSet globalPoints = con.querySQL(QueryType.LOAD_ALL_GLOBAL_POINTS)) {
            while (globalTags.next()) {
                this.globalTags.add(globalTags.getString("tag"));
            }
            while (globalPoints.next()) {
                final String category = globalPoints.getString("category");
                this.globalPoints.put(category, new Point(category, globalPoints.getInt("count")));
            }
            log.debug("There are " + this.globalTags.size() + " global_tags and " + this.globalPoints.size()
                    + " global_points loaded");
        } catch (final SQLException e) {
            log.error("There was an exception with SQL", e);
        }
    }

    @Override
    public List<String> getTags() {
        return globalTags;
    }

    @Override
    public boolean hasTag(final String tag) {
        return globalTags.contains(tag);
    }

    @Override
    public void addTag(final String tag) {
        if (!globalTags.contains(tag)) {
            globalTags.add(tag);
            saver.add(new Record(UpdateType.ADD_GLOBAL_TAGS, tag));
        }
    }

    @Override
    public void removeTag(final String tag) {
        globalTags.remove(tag);
        saver.add(new Record(UpdateType.REMOVE_GLOBAL_TAGS, tag));
    }

    @Override
    public List<Point> getPoints() {
        return List.copyOf(globalPoints.values());
    }

    @Override
    public Optional<Integer> getPointsFromCategory(final String category) {
        final Point point = globalPoints.get(category);
        if (point != null) {
            return Optional.of(point.getCount());
        }
        return Optional.empty();
    }

    @Override
    public void modifyPoints(final String category, final int count) {
        saver.add(new Record(UpdateType.REMOVE_GLOBAL_POINTS, category));
        final Point point = globalPoints.computeIfAbsent(category, cat -> new Point(category, 0));
        point.addPoints(count);
        saver.add(new Record(UpdateType.ADD_GLOBAL_POINTS, category, String.valueOf(point.getCount())));
    }

    @Override
    public void setPoints(final String category, final int count) {
        saver.add(new Record(UpdateType.REMOVE_GLOBAL_POINTS, category));
        globalPoints.put(category, new Point(category, count));
        saver.add(new Record(UpdateType.ADD_GLOBAL_POINTS, category, String.valueOf(count)));
    }

    @Override
    public void removePointsCategory(final String category) {
        globalPoints.remove(category);
        saver.add(new Record(UpdateType.REMOVE_GLOBAL_POINTS, category));
    }

    /**
     * Purges all global data from the database and from this object.
     */
    public void purge() {
        // clear all lists
        globalTags.clear();
        globalPoints.clear();
        // clear the database
        saver.add(new Record(UpdateType.DELETE_GLOBAL_POINTS));
        saver.add(new Record(UpdateType.DELETE_GLOBAL_TAGS));
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
}
