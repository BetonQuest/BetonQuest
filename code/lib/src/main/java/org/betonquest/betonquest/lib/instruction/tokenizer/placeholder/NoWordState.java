package org.betonquest.betonquest.lib.instruction.tokenizer.placeholder;

import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerState;

/**
 * The state of a tokenizer when there is no word to parse.
 */
public class NoWordState implements TokenizerState<PlaceholderExtractorContext> {

    /**
     * Create the no word state.
     */
    public NoWordState() {
    }

    @Override
    public TokenizerState<PlaceholderExtractorContext> parseNext(final PlaceholderExtractorContext ctx, final int codePoint) throws TokenizerException {
        if (codePoint == ctx.settings().escapeCharacter()) {
            if (ctx.settings().parseNonPlaceholderWords()) {
                ctx.appendCodePoint(codePoint);
                return new EscapeState(this, false);
            }
            return new EscapeState(this, true);
        }
        if (codePoint == ctx.settings().placeholderBrackets()) {
            if (ctx.settings().parseNonPlaceholderWords()) {
                ctx.endWord();
            }
            ctx.appendCodePoint(codePoint);
            return new WordState();
        }
        if (ctx.settings().parseNonPlaceholderWords()) {
            ctx.appendCodePoint(codePoint);
        }
        return this;
    }

    @Override
    public void parseEnd(final PlaceholderExtractorContext ctx) throws TokenizerException {
        if (ctx.settings().parseNonPlaceholderWords()) {
            ctx.endWord();
        }
    }
}
