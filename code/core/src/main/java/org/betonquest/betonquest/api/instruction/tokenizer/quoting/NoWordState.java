package org.betonquest.betonquest.api.instruction.tokenizer.quoting;

import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerState;

/**
 * The state in between words. Whitespace is ignored,
 * double quotes start quoted strings and everything else starts unquoted strings,
 */
public class NoWordState implements TokenizerState<QuotingTokenizerContext> {

    /**
     * Create the in between words state.
     */
    public NoWordState() {
    }

    @Override
    public TokenizerState<QuotingTokenizerContext> parseNext(final QuotingTokenizerContext ctx, final int codePoint) throws TokenizerException {
        if (ctx.settings().isSeparator(codePoint)) {
            return this;
        }
        if (ctx.settings().isBeginQuote(codePoint)) {
            ctx.beginWord();
            return new QuotedWordState();
        }
        if (ctx.settings().isEndQuote(codePoint)) {
            throw new TokenizerException("Unexpected closing quote.");
        }
        ctx.appendCodePoint(codePoint);
        return new PureWordState();
    }

    @Override
    public void parseEnd(final QuotingTokenizerContext ctx) {
        // no action required
    }
}
