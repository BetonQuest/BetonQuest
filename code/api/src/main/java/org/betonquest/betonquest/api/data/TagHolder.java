package org.betonquest.betonquest.api.data;

import java.util.Set;

/**
 * Represents all tags attached to a player or a global instance referred to as the owner of this {@link TagHolder}.
 */
public interface TagHolder {

    /**
     * Gets all present tags for the owner.
     *
     * @return all tags
     */
    Set<String> get();

    /**
     * Whether the owner has the given tag.
     *
     * @param tag tag to check
     * @return true if the owner has the given tag, false otherwise
     */
    boolean has(String tag);

    /**
     * Adds the given tag to the owner.
     *
     * @param tag tag to add
     */
    void add(String tag);

    /**
     * Removes the given tag from the owner.
     *
     * @param tag tag to remove
     */
    void remove(String tag);
}
