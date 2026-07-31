package org.betonquest.betonquest.api.instruction.tokenizer.quoting;

import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerState;

/**
 * The state of a quoted word. Escaping with backslashes is possible.
 * A double quote at the end of the string is required.
 */
public class QuotedWordState implements TokenizerState<QuotingTokenizerContext> {

    /**
     * The initial quoting level of a quoted word.
     */
    public static final int INITIAL_QUOTING_LEVEL = 0;

    /**
     * The quoting level of the word.
     */
    private final int quotingLevel;

    /**
     * Create the quoted word collection state with the initial quoting level.
     */
    public QuotedWordState() {
        this.quotingLevel = INITIAL_QUOTING_LEVEL;
    }

    /**
     * Create the quoted word collection state.
     *
     * @param quotingLevel the quoting level of the word
     */
    public QuotedWordState(final int quotingLevel) {
        this.quotingLevel = quotingLevel;
    }

    @Override
    public TokenizerState<QuotingTokenizerContext> parseNext(final QuotingTokenizerContext ctx, final int codePoint) {
        if (ctx.settings().isEscape(codePoint)) {
            return new EscapeState(this);
        }
        if (ctx.settings().isEndQuote(codePoint)) {
            ctx.endWord();
            return new QuoteEndState(quotingLevel);
        }
        if (ctx.settings().isBeginQuote(codePoint)) {
            ctx.beginWord();
            return new QuotedWordState(quotingLevel + 1);
        }
        ctx.appendCodePoint(codePoint);
        return this;
    }

    @Override
    public void parseEnd(final QuotingTokenizerContext ctx) throws TokenizerException {
        throw new TokenizerException("Expected quoted string to end but reached end of data.");
    }
}
