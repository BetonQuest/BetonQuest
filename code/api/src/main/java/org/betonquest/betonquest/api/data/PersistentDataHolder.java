package org.betonquest.betonquest.api.data;

/**
 * Represents a persistent data holder.
 *
 * @since 3.0.0
 */
public interface PersistentDataHolder {

    /**
     * Access to the points of this holder.
     *
     * @return the points
     * @since 3.0.0
     */
    PointHolder points();

    /**
     * Access to the tags of this holder.
     *
     * @return the tags
     * @since 3.0.0
     */
    TagHolder tags();
}
