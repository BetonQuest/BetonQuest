package org.betonquest.betonquest.api.identifier;

/**
 * A condition identifier points to a condition defined in a quest package.
 */
public interface ConditionIdentifier extends ReadableIdentifier {

    /**
     * If the condition should be interpreted as inverted.
     *
     * @return true if inverted, false otherwise.
     */
    boolean isInverted();
}
