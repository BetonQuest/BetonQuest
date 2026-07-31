package org.betonquest.betonquest.api.instruction.tokenizer.placeholder;

import org.betonquest.betonquest.api.instruction.tokenizer.Token;
import org.betonquest.betonquest.api.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerState;

/**
 * Extracts placeholders from an instruction string.
 */
public class PlaceholderExtractor implements Tokenizer {

    @Override
    public Token[] tokens(final String instruction) throws TokenizerException {
        final PlaceholderExtractorContext ctx = new PlaceholderExtractorContext();
        TokenizerState<PlaceholderExtractorContext> state = new NoWordState();
        for (final int character : instruction.codePoints().toArray()) {
            state = state.parseNext(ctx, character);
        }
        state.parseEnd(ctx);
        return ctx.getTokens().toArray(Token[]::new);
    }
}
