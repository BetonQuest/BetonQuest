package org.betonquest.betonquest.api.instruction.tokenizer.placeholder;

import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerState;

/**
 * The state of a tokenizer when a word is being parsed.
 */
public class WordState implements TokenizerState<PlaceholderExtractorContext> {

    @Override
    public TokenizerState<PlaceholderExtractorContext> parseNext(final PlaceholderExtractorContext ctx, final int codePoint) throws TokenizerException {
        ctx.appendCodePoint(codePoint);
        if (codePoint == ctx.settings().escapeCharacter()) {
            return new EscapeState(this, false);
        }
        if (codePoint == ctx.settings().placeholderBrackets()) {
            ctx.endWord();
            return new NoWordState();
        }
        if (codePoint == ctx.settings().openQuote()) {
            return new QuotedWordState();
        }
        return this;
    }

    @Override
    public void parseEnd(final PlaceholderExtractorContext ctx) throws TokenizerException {
        throw new TokenizerException("Expected closing bracket for placeholder but reached end of data.");
    }
}
