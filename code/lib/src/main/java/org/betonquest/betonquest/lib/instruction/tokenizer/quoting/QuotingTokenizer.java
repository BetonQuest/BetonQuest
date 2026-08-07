package org.betonquest.betonquest.lib.instruction.tokenizer.quoting;

import org.betonquest.betonquest.lib.instruction.tokenizer.Token;
import org.betonquest.betonquest.lib.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerState;

/**
 * Instruction string tokenizer that splits on spaces but honors quoting and escaping.
 */
public class QuotingTokenizer implements Tokenizer {

    /**
     * The tokenizer settings.
     */
    private final QuotingTokenizerSettings tokenizerSettings;

    /**
     * Create a new quote respecting instruction string tokenizer.
     *
     * @param tokenizerSettings the tokenizer settings
     */
    public QuotingTokenizer(final QuotingTokenizerSettings tokenizerSettings) {
        this.tokenizerSettings = tokenizerSettings;
    }

    /**
     * Tokenize a raw instruction string into an array of instruction parts. Quotes will be respected.
     * Whitespace and quotes and backslashes can be escaped with a preceding backslash.
     *
     * @param instruction instruction string to tokenize
     * @return the instruction string's parts
     * @throws TokenizerException if the instruction string is invalid
     */
    @Override
    public Token[] tokens(final String instruction) throws TokenizerException {
        final QuotingTokenizerContext ctx = new QuotingTokenizerContext(tokenizerSettings);
        TokenizerState<QuotingTokenizerContext> state = new NoWordState();
        for (final int character : instruction.codePoints().toArray()) {
            state = state.parseNext(ctx, character);
        }
        state.parseEnd(ctx);
        return ctx.getParent().children().toArray(Token[]::new);
    }
}
