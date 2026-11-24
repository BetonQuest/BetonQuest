package org.betonquest.betonquest.lib.instruction.tokenizer.quoting;

import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerState;

/**
 * The state of a preceding escape sequence.
 * Any single character will be accepted as data before returning to the follow-up state.
 * The escape character itself is kept in front of escaped placeholder markers or if explicitly requested,
 * so they are still recognized as escaped when the placeholder is resolved later.
 */
public class EscapeState implements TokenizerState<QuotingTokenizerContext> {

    /**
     * State to transition to after collecting the escaped character.
     */
    private final TokenizerState<QuotingTokenizerContext> followUpState;

    /**
     * The code point of the escape character that started the escape sequence.
     */
    private final int escapeCodePoint;

    /**
     * Whether the escape character is kept for every escaped character.
     */
    private final boolean keepEscape;

    /**
     * Create the escape state with a follow-up state.
     * The escape character is only kept in front of placeholder markers.
     *
     * @param followUpState   follow-up state to use
     * @param escapeCodePoint the code point of the escape character that started the escape sequence
     */
    public EscapeState(final TokenizerState<QuotingTokenizerContext> followUpState, final int escapeCodePoint) {
        this(followUpState, escapeCodePoint, false);
    }

    /**
     * Create the escape state with a follow-up state.
     *
     * @param followUpState   follow-up state to use
     * @param escapeCodePoint the code point of the escape character that started the escape sequence
     * @param keepEscape      whether the escape character is kept for every escaped character
     */
    public EscapeState(final TokenizerState<QuotingTokenizerContext> followUpState, final int escapeCodePoint, final boolean keepEscape) {
        this.followUpState = followUpState;
        this.escapeCodePoint = escapeCodePoint;
        this.keepEscape = keepEscape;
    }

    @Override
    public TokenizerState<QuotingTokenizerContext> parseNext(final QuotingTokenizerContext ctx, final int codePoint) {
        if (keepEscape || ctx.settings().isPlaceholderMarker(codePoint)) {
            ctx.appendCodePoint(escapeCodePoint);
        }
        ctx.appendCodePoint(codePoint);
        return followUpState;
    }

    @Override
    public void parseEnd(final QuotingTokenizerContext ctx) throws TokenizerException {
        throw new TokenizerException("Expected any character for escape sequence but reached end of data");
    }
}
