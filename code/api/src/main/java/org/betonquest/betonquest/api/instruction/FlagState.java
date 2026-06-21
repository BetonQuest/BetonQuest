package org.betonquest.betonquest.api.instruction;

/**
 * Describes the state of a flag argument.
 *
 * @see org.betonquest.betonquest.api.instruction.chain.ChainableInstruction
 * @since 3.0.0
 */
public enum FlagState {

    /**
     * If the flag is absent, neither the flag nor its value is defined.
     *
     * @since 3.0.0
     */
    ABSENT,
    /**
     * If the flag is undefined, the flag is present, but its value is not defined.
     *
     * @since 3.0.0
     */
    UNDEFINED,
    /**
     * If the flag is defined, the flag is present and its value is defined.
     *
     * @since 3.0.0
     */
    DEFINED
}
