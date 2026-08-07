package org.betonquest.betonquest.lib.instruction.tokenizer.placeholder;

import org.betonquest.betonquest.lib.instruction.tokenizer.Token;
import org.betonquest.betonquest.lib.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerState;

/**
 * Extracts placeholders from an instruction string.
 */
public class PlaceholderExtractor implements Tokenizer {

    /**
     * The settings for the placeholder extractor.
     */
    private final PlaceholderExtractorSettings settings;

    /**
     * Creates a new PlaceholderExtractor.
     *
     * @param settings the settings for the placeholder extractor
     */
    public PlaceholderExtractor(final PlaceholderExtractorSettings settings) {
        this.settings = settings;
    }

    @Override
    public Token[] tokens(final String instruction) throws TokenizerException {
        final PlaceholderExtractorContext ctx = new PlaceholderExtractorContext(settings);
        TokenizerState<PlaceholderExtractorContext> state = new NoWordState();
        for (final int character : instruction.codePoints().toArray()) {
            state = state.parseNext(ctx, character);
        }
        state.parseEnd(ctx);
        return ctx.getTokens().toArray(Token[]::new);
    }
}
