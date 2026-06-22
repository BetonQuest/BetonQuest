package org.betonquest.betonquest.api.identifier;

/**
 * A compass identifier pointing to a quest compass defined in a quest package.
 *
 * @since 3.0.0
 */
public interface CompassIdentifier extends Identifier {

    /**
     * Get the full path of the tag to indicate a quest compass should be shown.
     *
     * @return the compass tag
     * @since 3.0.0
     * @deprecated Will be replaced with another method in the future
     */
    @Deprecated
    String getTag();
}
