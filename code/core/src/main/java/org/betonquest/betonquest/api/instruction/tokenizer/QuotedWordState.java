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
        if (ctx.settings().isEscape(codePoint)) {
            return new EscapeState(this);
        }
        if (ctx.settings().isEndQuote(codePoint)) {
            ctx.endWord();
            return new QuoteEndState();
        }
        if (ctx.settings().isBeginQuote(codePoint)) {
            ctx.beginWord();
            return new QuotedWordState();
        }
        ctx.appendCodePoint(codePoint);
        return this;
    }

    @Override
    public void parseEnd(final TokenizerContext ctx) throws TokenizerException {
        throw new TokenizerException("Expected quoted string to end but reached end of data.");
    }
}
