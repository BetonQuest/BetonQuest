package org.betonquest.betonquest.lib.instruction.tokenizer.quoting;

import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerState;

/**
 * The state of an unquoted word. Escaping with backslashes is possible, quotes need to be escaped.
 */
public class PureWordState implements TokenizerState<QuotingTokenizerContext> {

    /**
     * Create the pure word collection state.
     */
    public PureWordState() {
    }

    @Override
    public TokenizerState<QuotingTokenizerContext> parseNext(final QuotingTokenizerContext ctx, final int codePoint) {
        if (ctx.settings().isSeparator(codePoint)) {
            ctx.endWord();
            return new NoWordState();
        }
        ctx.appendCodePoint(codePoint);
        return this;
    }

    @Override
    public void parseEnd(final QuotingTokenizerContext ctx) {
        ctx.endWord();
    }
}
