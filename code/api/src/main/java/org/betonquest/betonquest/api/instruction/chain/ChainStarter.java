package org.betonquest.betonquest.api.instruction.chain;

/**
 * In the first step of the instruction chain, the targeted argument or argument key is decided.
 * At this stage only the underlying instruction is known.
 *
 * @see PlainChainParser
 * @see ChainRetriever
 */
public interface ChainStarter {

    /**
     * Starts the next step of the chain with the next keyless argument in the instruction.
     *
     * @return a new {@link PlainChainParser} carrying the settings
     */
    PlainChainParser next();

    /**
     * Starts the next step of the chain with the given key for an optional argument in the instruction.
     *
     * @param argument the argument key
     * @return a new {@link OptionalChainParser} carrying the settings
     */
    OptionalChainParser next(String argument);
}
