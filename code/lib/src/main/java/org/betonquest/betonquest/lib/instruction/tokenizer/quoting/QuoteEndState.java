package org.betonquest.betonquest.lib.instruction.tokenizer.quoting;

import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerState;

/**
 * The state of a just finished quoted word. A whitespace character is expected.
 */
public class QuoteEndState implements TokenizerState<QuotingTokenizerContext> {

    /**
     * The quoting level of the word.
     */
    private final int quotingLevel;

    /**
     * Create the quoted word end state.
     *
     * @param quotingLevel the quoting level of the word
     */
    public QuoteEndState(final int quotingLevel) {
        this.quotingLevel = quotingLevel;
    }

    @Override
    public TokenizerState<QuotingTokenizerContext> parseNext(final QuotingTokenizerContext ctx, final int codePoint) throws TokenizerException {
        if (ctx.settings().isSeparator(codePoint)) {
            return new NoWordState();
        }
        if (ctx.settings().isBeginQuote(codePoint)) {
            ctx.beginWord();
            return new QuotedWordState(quotingLevel);
        }
        if (ctx.settings().isEndQuote(codePoint)) {
            if (quotingLevel == QuotedWordState.INITIAL_QUOTING_LEVEL) {
                throw new TokenizerException("Unexpected closing quote.");
            }
            ctx.endWord();
            return new QuoteEndState(quotingLevel - 1);
        }
        if (quotingLevel > QuotedWordState.INITIAL_QUOTING_LEVEL) {
            ctx.appendCodePoint(codePoint);
            return new QuotedWordState(quotingLevel - 1);
        }
        throw new TokenizerException("Expected separator or nothing but got: '%s'".formatted(Character.toString(codePoint)));
    }

    @Override
    public void parseEnd(final QuotingTokenizerContext ctx) throws TokenizerException {
        if (quotingLevel > QuotedWordState.INITIAL_QUOTING_LEVEL) {
            throw new TokenizerException("Expected quoted string to end but reached end of data.");
        }
    }
}
