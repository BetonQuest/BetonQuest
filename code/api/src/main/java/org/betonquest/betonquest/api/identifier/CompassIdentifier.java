package org.betonquest.betonquest.api.identifier;

/**
 * A compass identifier pointing to a quest compass defined in a quest package.
 */
public interface CompassIdentifier extends Identifier {

    /**
     * Get the full path of the tag to indicate a quest compass should be shown.
     *
     * @return the compass tag
     * @deprecated Will be replaced with another method in the future
     */
    @Deprecated
    String getTag();
}
