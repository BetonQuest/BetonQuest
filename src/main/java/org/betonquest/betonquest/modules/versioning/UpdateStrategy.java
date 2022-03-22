package org.betonquest.betonquest.modules.versioning;

/**
 * Represent different strategies to select which versions are valid to update to.
 * These strategies should fit for versions that fulfil semantic versioning.
 */
public enum UpdateStrategy {
    /**
     * The first digit of a semantic version.
     */
    MAJOR(),
    /**
     * The second digit of a semantic version.
     */
    MINOR(),
    /**
     * The third digit of a semantic version.
     */
    PATCH(),
}
