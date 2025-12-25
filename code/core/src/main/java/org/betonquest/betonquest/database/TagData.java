package org.betonquest.betonquest.database;

import java.util.Set;

/**
 * Provides the ability to have tags.
 */
public interface TagData {

    /**
     * Provide set of all defined tags.
     *
     * @return Defined tags
     */
    Set<String> getTags();

    /**
     * Check if a specific tag is added.
     *
     * @param tag tag to look for
     * @return indicator if the tag exists
     */
    boolean hasTag(String tag);

    /**
     * Add a new tag.
     *
     * @param tag tag to add
     */
    void addTag(String tag);

    /**
     * Remove a tag.
     *
     * @param tag tag to remove
     */
    void removeTag(String tag);
}
