package org.betonquest.betonquest.api.instruction.tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Instruction string tokenizer that splits on spaces but honors quoting and escaping.
 */
public class QuotingTokenizer implements Tokenizer {
    /**
     * Character that is used to quote words.
     */
    public static final char QUOTE = '"';

    /**
     * Character that is used to escape characters.
     */
    public static final char ESCAPE = '\\';

    /**
     * Create a new quote respecting instruction string tokenizer.
     */
    public QuotingTokenizer() {
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
    public String[] tokens(final String instruction) throws TokenizerException {
        final Context ctx = new Context();
        TokenizerState state = new NoWordState();
        for (final int character : instruction.codePoints().toArray()) {
            state = state.parseNext(ctx, character);
        }
        state.parseEnd(ctx);
        return ctx.words.toArray(String[]::new);
    }

    /**
     * State machine context implementation of this tokenizer.
     */
    private static final class Context implements TokenizerContext {

        /**
         * The list of already collected words.
         */
        private final List<String> words = new ArrayList<>();

        /**
         * The word that is currently being collected.
         */
        @SuppressWarnings("PMD.AvoidStringBufferField")
        private StringBuilder currentWord = new StringBuilder();

        @Override
        public void endWord() {
            words.add(currentWord.toString());
            currentWord = new StringBuilder();
        }

        @Override
        public void appendCodePoint(final int codePoint) {
            currentWord.appendCodePoint(codePoint);
        }
    }
}
