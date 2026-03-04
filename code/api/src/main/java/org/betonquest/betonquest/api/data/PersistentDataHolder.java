package org.betonquest.betonquest.api.data;

/**
 * Represents a persistent data holder.
 */
public interface PersistentDataHolder {

    /**
     * Access to the points of this holder.
     *
     * @return the points
     */
    PointHolder points();

    /**
     * Access to the tags of this holder.
     *
     * @return the tags
     */
    TagHolder tags();
}
