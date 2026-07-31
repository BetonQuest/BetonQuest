package org.betonquest.betonquest.lib.instruction.tokenizer.placeholder;

import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerState;

/**
 * The state of a tokenizer when an escape sequence is being parsed.
 */
public class EscapeState implements TokenizerState<PlaceholderExtractorContext> {

    /**
     * State to transition to after collecting the escaped character.
     */
    private final TokenizerState<PlaceholderExtractorContext> followUpState;

    /**
     * If the escape sequence should be skipped.
     */
    private final boolean skip;

    /**
     * Create the escape state.
     *
     * @param followUpState the state to transition to after collecting the escaped character.
     * @param skip          if the escape sequence should be skipped.
     */
    public EscapeState(final TokenizerState<PlaceholderExtractorContext> followUpState, final boolean skip) {
        this.followUpState = followUpState;
        this.skip = skip;
    }

    @Override
    public TokenizerState<PlaceholderExtractorContext> parseNext(final PlaceholderExtractorContext ctx, final int codePoint) throws TokenizerException {
        if (!skip) {
            ctx.appendCodePoint(codePoint);
        }
        return followUpState;
    }

    @Override
    public void parseEnd(final PlaceholderExtractorContext ctx) throws TokenizerException {
        throw new TokenizerException("Expected any character for escape sequence but reached end of data.");
    }
}
