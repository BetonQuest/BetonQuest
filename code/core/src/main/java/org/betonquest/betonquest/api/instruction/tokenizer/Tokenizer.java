package org.betonquest.betonquest.api.instruction.tokenizer;

/**
 * Tokenize an instruction into its parts.
 */
@FunctionalInterface
public interface Tokenizer {

    /**
     * Get the tokens for a given instruction string.
     *
     * @param instruction instruction to tokenize
     * @return tokens of the instruction
     * @throws TokenizerException if the instruction cannot be tokenized
     */
    String[] tokens(String instruction) throws TokenizerException;
}
