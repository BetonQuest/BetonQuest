package org.betonquest.betonquest.api.instruction.tokenizer.placeholder;

import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerState;

/**
 * The state of a tokenizer when a quoted word is being parsed.
 */
public class QuotedWordState implements TokenizerState<PlaceholderExtractorContext> {

    /**
     * The initial quote level of a quoted word.
     */
    public static final int INITIAL_QUOTE_LEVEL = 1;

    /**
     * The quote level of the word.
     */
    private final int quoteLevel;

    /**
     * Create a new quoted word state with the initial quote level.
     */
    public QuotedWordState() {
        this(INITIAL_QUOTE_LEVEL);
    }

    /**
     * Create a new quoted word state.
     *
     * @param quoteLevel the quote level of the word
     */
    public QuotedWordState(final int quoteLevel) {
        this.quoteLevel = quoteLevel;
    }

    @Override
    public TokenizerState<PlaceholderExtractorContext> parseNext(final PlaceholderExtractorContext ctx, final int codePoint) throws TokenizerException {
        ctx.appendCodePoint(codePoint);
        if (codePoint == ctx.settings().escapeCharacter()) {
            return new EscapeState(this, false);
        }
        if (codePoint == ctx.settings().openQuote()) {
            return new QuotedWordState(quoteLevel + 1);
        }
        if (codePoint == ctx.settings().closeQuote()) {
            if (quoteLevel == INITIAL_QUOTE_LEVEL) {
                return new WordState();
            }
            return new QuotedWordState(quoteLevel - 1);
        }
        return this;
    }

    @Override
    public void parseEnd(final PlaceholderExtractorContext ctx) throws TokenizerException {
        throw new TokenizerException("Expected closing of quoted placeholder part but reached end of data. (quoting level: %s)".formatted(quoteLevel));
    }
}
