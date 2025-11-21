package org.betonquest.betonquest.versioning;

/**
 * Represent different strategies to select which versions are valid to update to.
 * These strategies should fit for versions that fulfil semantic versioning.
 */
public enum UpdateStrategy {
    /**
     * The first number of a semantic version.
     */
    MAJOR(),
    /**
     * The second number of a semantic version.
     */
    MINOR(),
    /**
     * The third number of a semantic version.
     */
    PATCH(),
}
