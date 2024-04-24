package org.betonquest.betonquest.instruction;

/**
 * The state of an unquoted word. Escaping with backslashes is possible, quotes need to be escaped.
 */
public class PureWordState implements TokenizerState {
    /**
     * Create the pure word collection state.
     */
    public PureWordState() {
    }

    @Override
    public TokenizerState parseNext(final TokenizerContext ctx, final int codePoint) throws TokenizerException {
        if (Character.isWhitespace(codePoint)) {
            ctx.endWord();
            return new NoWordState();
        }
        if (codePoint == QuotingTokenizer.QUOTE) {
            throw new TokenizerException("Expected any character for escape sequence but reached end of data.");
        }
        if (codePoint == QuotingTokenizer.ESCAPE) {
            return new EscapeState(this);
        }
        ctx.appendCodePoint(codePoint);
        return this;
    }

    @Override
    public void parseEnd(final TokenizerContext ctx) {
        ctx.endWord();
    }
}
