package org.betonquest.betonquest.api.data;

import java.util.Set;

/**
 * Represents all tags attached to something referred to as the holder.
 *
 * @since 3.0.0
 */
public interface TagHolder {

    /**
     * Gets all present tags for the owner.
     *
     * @return all tags
     * @since 3.0.0
     */
    Set<String> get();

    /**
     * Whether the owner has the given tag.
     *
     * @param tag tag to check
     * @return true if the owner has the given tag, false otherwise
     * @since 3.0.0
     */
    boolean has(String tag);

    /**
     * Adds the given tag to the owner.
     *
     * @param tag tag to add
     * @since 3.0.0
     */
    void add(String tag);

    /**
     * Removes the given tag from the owner.
     *
     * @param tag tag to remove
     * @since 3.0.0
     */
    void remove(String tag);
}
