package org.betonquest.betonquest.api.data;

import java.util.Map;
import java.util.Optional;

/**
 * Represents all points attached to a player or a global instance referred to as the holder.
 */
public interface PointHolder {

    /**
     * Gets all points for all categories of the holder.
     *
     * @return all points
     */
    Map<String, Integer> get();

    /**
     * Whether the holder has points in the given category.
     *
     * @param category the category to check
     * @return true if the holder has points in the given category, false otherwise
     */
    boolean has(String category);

    /**
     * Gets the current number of points the holder has in the given category.
     * If the holder does not have any points in the given category, an empty optional will be returned.
     *
     * @param category the category to get the points from
     * @return the number of points the holder currently has or an empty optional
     * if the holder does not have any points in the given category
     */
    Optional<Integer> get(String category);

    /**
     * Sets the number of points for the given category.
     *
     * @param category the category to set the points for
     * @param points   the number of points to set
     */
    void set(String category, int points);

    /**
     * Adds the given number of points to the holder's points in the given category.
     * The number of points to be added may be positive or negative.
     *
     * @param category the category to add the points to
     * @param points   the number of points to add
     */
    void add(String category, int points);

    /**
     * Removes all points from the holder in the given category as well as the category itself.
     * The difference is in not having 0 points in the category but the category itself being absent.
     *
     * @param category the category to remove
     */
    void remove(String category);
}
