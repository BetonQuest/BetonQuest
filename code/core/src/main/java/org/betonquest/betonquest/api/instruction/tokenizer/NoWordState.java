package org.betonquest.betonquest.api.instruction.tokenizer;

/**
 * The state in between words. Whitespace is ignored,
 * double quotes start quoted strings and everything else starts unquoted strings,
 */
public class NoWordState implements TokenizerState {

    /**
     * Create the in between words state.
     */
    public NoWordState() {
    }

    @Override
    public TokenizerState parseNext(final TokenizerContext ctx, final int codePoint) {
        if (Character.isWhitespace(codePoint)) {
            return this;
        }
        if (codePoint == QuotingTokenizer.QUOTE) {
            return new QuotedWordState();
        }
        ctx.appendCodePoint(codePoint);
        return new PureWordState();
    }

    @Override
    public void parseEnd(final TokenizerContext ctx) {
        // no action required
    }
}
