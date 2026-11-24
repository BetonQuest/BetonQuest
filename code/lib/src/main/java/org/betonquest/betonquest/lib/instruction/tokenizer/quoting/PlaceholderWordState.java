package org.betonquest.betonquest.lib.instruction.tokenizer.quoting;

import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerState;

/**
 * The state of a placeholder inside a quoted word.
 * Quote and escape characters are kept as they are, while nested quotes are tracked to find the closing marker.
 * A closing quote without an open nested quote ends the placeholder and is handled by the enclosing quoted word.
 */
public class PlaceholderWordState implements TokenizerState<QuotingTokenizerContext> {

    /**
     * The quoted word state containing the placeholder.
     */
    private final QuotedWordState enclosingState;

    /**
     * The number of open quotes inside the placeholder.
     */
    private final int quotingDepth;

    /**
     * Create the placeholder state.
     *
     * @param enclosingState the quoted word state containing the placeholder
     * @param quotingDepth   the number of open quotes inside the placeholder
     */
    public PlaceholderWordState(final QuotedWordState enclosingState, final int quotingDepth) {
        this.enclosingState = enclosingState;
        this.quotingDepth = quotingDepth;
    }

    @Override
    public TokenizerState<QuotingTokenizerContext> parseNext(final QuotingTokenizerContext ctx, final int codePoint) throws TokenizerException {
        final QuotingTokenizerSettings settings = ctx.settings();
        if (settings.isEscape(codePoint)) {
            return new EscapeState(this, codePoint, true);
        }
        if (settings.isEndQuote(codePoint)) {
            if (quotingDepth == 0) {
                return enclosingState.parseNext(ctx, codePoint);
            }
            ctx.appendCodePoint(codePoint);
            return new PlaceholderWordState(enclosingState, quotingDepth - 1);
        }
        ctx.appendCodePoint(codePoint);
        if (settings.isBeginQuote(codePoint)) {
            return new PlaceholderWordState(enclosingState, quotingDepth + 1);
        }
        if (quotingDepth == 0 && settings.isPlaceholderMarker(codePoint)) {
            return enclosingState;
        }
        return this;
    }

    @Override
    public void parseEnd(final QuotingTokenizerContext ctx) throws TokenizerException {
        throw new TokenizerException("Expected placeholder and quoted string to end but reached end of data.");
    }
}
