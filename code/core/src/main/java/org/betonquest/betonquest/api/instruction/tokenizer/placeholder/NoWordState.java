package org.betonquest.betonquest.api.instruction.tokenizer.placeholder;

import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerState;

/**
 * The state of a tokenizer when there is no word to parse.
 */
public class NoWordState implements TokenizerState<PlaceholderExtractorContext> {

    @Override
    public TokenizerState<PlaceholderExtractorContext> parseNext(final PlaceholderExtractorContext ctx, final int codePoint) throws TokenizerException {
        if (codePoint == ctx.settings().escapeCharacter()) {
            return new EscapeState(this, true);
        }
        if (codePoint == ctx.settings().placeholderBrackets()) {
            ctx.appendCodePoint(codePoint);
            return new WordState();
        }
        return this;
    }

    @Override
    public void parseEnd(final PlaceholderExtractorContext ctx) throws TokenizerException {
        // no action required
    }
}
