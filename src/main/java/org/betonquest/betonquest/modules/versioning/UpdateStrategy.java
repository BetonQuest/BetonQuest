package org.betonquest.betonquest.modules.versioning;

/**
 * Represent different strategies to select which versions are valid to update to.
 */
public enum UpdateStrategy {
    MAJOR(),
    MINOR(),
    PATCH(),
}
