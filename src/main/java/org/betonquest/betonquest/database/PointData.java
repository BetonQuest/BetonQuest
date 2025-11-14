package org.betonquest.betonquest.database;

import org.betonquest.betonquest.Point;

import java.util.Optional;
import java.util.Set;

/**
 * Provides the ability to have points.
 */
public interface PointData {
    /**
     * Provide list of all defined points.
     *
     * @return Defined points
     */
    Set<Point> getPoints();

    /**
     * Returns the amount of point in specified category.
     *
     * @param category name of the category
     * @return amount of points
     */
    Optional<Integer> getPointsFromCategory(String category);

    /**
     * Adds or subtracts points to/from specified category.
     * If there is no such category it will be created.
     *
     * @param category points will be added to this category
     * @param count    how much points will be added (or subtracted if negative)
     */
    void modifyPoints(String category, int count);

    /**
     * Sets the amount of points in specified category. If there is no such category it will be
     * created.
     *
     * @param category points will be added to this category
     * @param count    how much points will be set
     */
    void setPoints(String category, int count);

    /**
     * Removes the whole category of points.
     *
     * @param category name of a point category
     */
    void removePointsCategory(String category);
}
