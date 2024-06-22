package org.betonquest.betonquest.instruction.tokenizer;

/**
 * State of the instruction string tokenizer.
 */
public interface TokenizerState {
    /**
     * Parse the next code point according to the current state.
     *
     * @param ctx       context object that collects parsing results
     * @param codePoint code point to parse
     * @return the next state to transition to
     * @throws TokenizerException if the code point was invalid for the current state
     */
    TokenizerState parseNext(TokenizerContext ctx, int codePoint) throws TokenizerException;

    /**
     * End the parsing of the instruction string in this state.
     *
     * @param ctx context object that collects parsing results
     * @throws TokenizerException if the current state does not allow the parsing to end
     */
    void parseEnd(TokenizerContext ctx) throws TokenizerException;
}
