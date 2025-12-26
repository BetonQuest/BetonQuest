package org.betonquest.betonquest.api.instruction;

/**
 * Describes the state of a flag argument.
 *
 * @see org.betonquest.betonquest.api.instruction.chain.ChainableInstruction
 */
public enum FlagState {

    /**
     * If the flag is absent meaning neither the flag nor its value is defined.
     */
    ABSENT,
    /**
     * If the flag is undefined meaning the flag is present but its value is not defined.
     */
    UNDEFINED,
    /**
     * if the flag is defined meaning the flag is present and its value is defined.
     */
    DEFINED
}
