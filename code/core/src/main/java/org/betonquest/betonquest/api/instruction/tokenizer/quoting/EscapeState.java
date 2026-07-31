package org.betonquest.betonquest.api.instruction.tokenizer.quoting;

import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerState;

/**
 * The state of a preceding escape sequence.
 * Any single character will be accepted as data before returning to the follow-up state.
 */
public class EscapeState implements TokenizerState<QuotingTokenizerContext> {

    /**
     * State to transition to after collecting the escaped character.
     */
    private final TokenizerState<QuotingTokenizerContext> followUpState;

    /**
     * Create the escape state with a follow-up state.
     *
     * @param followUpState follow-up state to use
     */
    public EscapeState(final TokenizerState<QuotingTokenizerContext> followUpState) {
        this.followUpState = followUpState;
    }

    @Override
    public TokenizerState<QuotingTokenizerContext> parseNext(final QuotingTokenizerContext ctx, final int codePoint) {
        ctx.appendCodePoint(codePoint);
        return followUpState;
    }

    @Override
    public void parseEnd(final QuotingTokenizerContext ctx) throws TokenizerException {
        throw new TokenizerException("Expected any character for escape sequence but reached end of data.");
    }
}
