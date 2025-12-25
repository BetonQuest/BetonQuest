package org.betonquest.betonquest.api.instruction.tokenizer;

/**
 * The state of a quoted word. Escaping with backslashes is possible.
 * A double quote at the end of the string is required.
 */
public class QuotedWordState implements TokenizerState {

    /**
     * Create the quoted word collection state.
     */
    public QuotedWordState() {
    }

    @Override
    public TokenizerState parseNext(final TokenizerContext ctx, final int codePoint) {
        if (codePoint == QuotingTokenizer.ESCAPE) {
            return new EscapeState(this);
        }
        if (codePoint == QuotingTokenizer.QUOTE) {
            ctx.endWord();
            return new QuoteEndState();
        }
        ctx.appendCodePoint(codePoint);
        return this;
    }

    @Override
    public void parseEnd(final TokenizerContext ctx) throws TokenizerException {
        throw new TokenizerException("Expected quoted string to end but reached end of data.");
    }
}
