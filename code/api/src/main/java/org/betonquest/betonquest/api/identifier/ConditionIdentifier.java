package org.betonquest.betonquest.api.identifier;

/**
 * A condition identifier pointing to a condition with an instruction defined in a quest package.
 */
public interface ConditionIdentifier extends ReadableIdentifier {

    /**
     * If the condition should be interpreted as inverted.
     *
     * @return true if inverted, false otherwise.
     * @deprecated Will be replaced with another method in the future
     */
    @Deprecated
    boolean isInverted();
}
