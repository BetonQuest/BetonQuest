package org.betonquest.betonquest.instruction.tokenizer;

/**
 * The state of a just finished quoted word. A whitespace character is expected.
 */
public class QuoteEndState implements TokenizerState {
    /**
     * Create the quoted word end state.
     */
    public QuoteEndState() {
    }

    @Override
    public TokenizerState parseNext(final TokenizerContext ctx, final int codePoint) throws TokenizerException {
        if (Character.isWhitespace(codePoint)) {
            return new NoWordState();
        }
        throw new TokenizerException("Expected whitespace or nothing but got: " + Character.toString(codePoint));
    }

    @Override
    public void parseEnd(final TokenizerContext ctx) {
        // no action required
    }
}
