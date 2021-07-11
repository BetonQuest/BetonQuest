package org.betonquest.betonquest.utils.versioning;

/**
 * Represent different strategies to select which versions are valid to update to.
 */
public enum UpdateStrategy {
    MAJOR(),
    MINOR(),
    PATCH(),
}
