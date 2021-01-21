package org.betonquest.betonquest.database;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Point;
import org.betonquest.betonquest.database.Connector.QueryType;
import org.betonquest.betonquest.database.Connector.UpdateType;
import org.betonquest.betonquest.database.Saver.Record;
import org.betonquest.betonquest.utils.LogUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Represents an object storing all player-related data, which can load and save it.
 */
@SuppressWarnings("PMD.CommentRequired")
public class GlobalData {

    @SuppressWarnings("PMD.DoNotUseThreads")
    private final Saver saver = BetonQuest.getInstance().getSaver();

    private final List<String> globalTags = new ArrayList<>();
    private final List<Point> globalPoints = new ArrayList<>();

    public GlobalData() {
        loadAllGlobalData();
    }

    /**
     * Loads all data for the player and puts it in appropriate lists.
     */
    public final void loadAllGlobalData() {
        try {
            // get connection to the database
            final Connector con = new Connector();
            try (ResultSet res2 = con.querySQL(QueryType.LOAD_ALL_GLOBAL_TAGS);
                 ResultSet res4 = con.querySQL(QueryType.LOAD_ALL_GLOBAL_POINTS);) {
                // put them into the list
                while (res2.next()) {
                    globalTags.add(res2.getString("tag"));
                }
                // put them into the list
                while (res4.next()) {
                    globalPoints.add(new Point(res4.getString("category"), res4.getInt("count")));
                }
                // log data to debugger
                LogUtils.getLogger().log(Level.FINE, "There are " + globalTags.size() + " global_tags and " + globalPoints.size()
                        + " global_points loaded");
            }
        } catch (SQLException e) {
            LogUtils.getLogger().log(Level.SEVERE, "There was an exception with SQL");
            LogUtils.logThrowable(e);
        }
    }

    /**
     * Returns the List of Tags
     *
     * @return the List of Tags
     */
    public List<String> getTags() {
        return globalTags;
    }

    /**
     * Checks if the there is a global tag set
     *
     * @param tag tag to check
     * @return true if the tag is set
     */
    public boolean hasTag(final String tag) {
        return globalTags.contains(tag);
    }

    /**
     * Adds the specified tag to global list. It won't double it however.
     *
     * @param tag tag to add
     */
    public void addTag(final String tag) {
        if (!globalTags.contains(tag)) {
            globalTags.add(tag);
            saver.add(new Record(UpdateType.ADD_GLOBAL_TAGS, new String[]{tag}));
        }
    }

    /**
     * Removes the specified tag from global list. If there is no tag, nothing
     * happens.
     *
     * @param tag tag to remove
     */
    public void removeTag(final String tag) {
        globalTags.remove(tag);
        saver.add(new Record(UpdateType.REMOVE_GLOBAL_TAGS, new String[]{tag}));
    }

    /**
     * Returns the List of Points.
     *
     * @return the List of Points
     */
    public List<Point> getPoints() {
        return globalPoints;
    }

    /**
     * Returns the amount of point the in specified category. If the
     * category does not exist, it will return 0.
     *
     * @param category name of the category
     * @return amount of global_points
     */
    @SuppressWarnings("PMD.LinguisticNaming")
    public int hasPointsFromCategory(final String category) {
        for (final Point p : globalPoints) {
            if (p.getCategory().equals(category)) {
                return p.getCount();
            }
        }
        return 0;
    }

    /**
     * Adds or subtracts global_points to/from specified category. If there is no such category it will
     * be created.
     *
     * @param category global_points will be added to this category
     * @param count    how much global_points will be added (or subtracted if negative)
     */
    public void modifyPoints(final String category, final int count) {
        saver.add(new Record(UpdateType.REMOVE_GLOBAL_POINTS, new String[]{category}));
        // check if the category already exists
        for (final Point point : globalPoints) {
            if (point.getCategory().equalsIgnoreCase(category)) {
                // if it does, add global_points to it
                saver.add(new Record(UpdateType.ADD_GLOBAL_POINTS,
                        new String[]{category, String.valueOf(point.getCount() + count)}));
                point.addPoints(count);
                return;
            }
        }
        // if not then create new point category with given amount of global_points
        globalPoints.add(new Point(category, count));
        saver.add(new Record(UpdateType.ADD_GLOBAL_POINTS, new String[]{category, String.valueOf(count)}));
    }

    /**
     * Removes the whole category of global_points.
     *
     * @param category name of a point category
     */
    public void removePointsCategory(final String category) {
        Point pointToRemove = null;
        for (final Point point : globalPoints) {
            if (point.getCategory().equalsIgnoreCase(category)) {
                pointToRemove = point;
            }
        }
        if (pointToRemove != null) {
            globalPoints.remove(pointToRemove);
        }
        saver.add(new Record(UpdateType.REMOVE_GLOBAL_POINTS, new String[]{category}));
    }

    /**
     * Purges all global data from the database and from this object.
     */
    public void purge() {
        // clear all lists
        globalTags.clear();
        globalPoints.clear();
        // clear the database
        saver.add(new Record(UpdateType.DELETE_GLOBAL_POINTS, new String[0]));
        saver.add(new Record(UpdateType.DELETE_GLOBAL_TAGS, new String[0]));
    }

    /**
     * Purges all global tags from the database and from this object
     */
    public void purgeTags() {
        // clear all lists
        globalTags.clear();
        // clear the database
        saver.add(new Record(UpdateType.DELETE_GLOBAL_TAGS, new String[0]));
    }

    /**
     * Purges all global points from the database and from this object
     */
    public void purgePoints() {
        // clear all lists
        globalPoints.clear();
        // clear the database
        saver.add(new Record(UpdateType.DELETE_GLOBAL_POINTS, new String[0]));
    }
}
