package org.betonquest.betonquest;

/**
 * Represents the point category.
 */
public class Point {

    /**
     * Category of these points.
     */
    private final String category;

    /**
     * Amount of these points.
     */
    private int count;

    /**
     * Creates new instance of the Point object.
     *
     * @param category category of these points
     * @param count    amount of these points
     */
    public Point(final String category, final int count) {
        this.category = category;
        this.count = count;
    }

    /**
     * Returns the category of these points.
     *
     * @return the category of these points
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns the amount of these points.
     *
     * @return the count of these points
     */
    public int getCount() {
        return count;
    }

    /**
     * Adds points in this category.
     *
     * @param add amount of the points to add
     */
    public void addPoints(final int add) {
        this.count = this.count + add;
    }
}
