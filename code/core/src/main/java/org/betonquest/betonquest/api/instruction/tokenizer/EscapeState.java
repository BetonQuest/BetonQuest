package org.betonquest.betonquest.api.instruction.tokenizer;

/**
 * The state of a preceding escape sequence.
 * Any single character will be accepted as data before returning to the follow-up state.
 */
public class EscapeState implements TokenizerState {

    /**
     * State to transition to after collecting the escaped character.
     */
    private final TokenizerState followUpState;

    /**
     * Create the escape state with a follow-up state.
     *
     * @param followUpState follow-up state to use
     */
    public EscapeState(final TokenizerState followUpState) {
        this.followUpState = followUpState;
    }

    @Override
    public TokenizerState parseNext(final TokenizerContext ctx, final int codePoint) {
        ctx.appendCodePoint(codePoint);
        return followUpState;
    }

    @Override
    public void parseEnd(final TokenizerContext ctx) throws TokenizerException {
        throw new TokenizerException("Expected any character for escape sequence but reached end of data.");
    }
}
