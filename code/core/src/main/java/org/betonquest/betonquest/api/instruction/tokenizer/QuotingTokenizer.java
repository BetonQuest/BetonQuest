package org.betonquest.betonquest.api.instruction.tokenizer;

/**
 * Instruction string tokenizer that splits on spaces but honors quoting and escaping.
 */
public class QuotingTokenizer implements Tokenizer {

    /**
     * The tokenizer settings.
     */
    private final TokenizerSettings tokenizerSettings;

    /**
     * Create a new quote respecting instruction string tokenizer.
     *
     * @param tokenizerSettings the tokenizer settings
     */
    public QuotingTokenizer(final TokenizerSettings tokenizerSettings) {
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
        final Context ctx = new Context(tokenizerSettings);
        TokenizerState state = new NoWordState();
        for (final int character : instruction.codePoints().toArray()) {
            state = state.parseNext(ctx, character);
        }
        state.parseEnd(ctx);
        return ctx.parent.children().toArray(Token[]::new);
    }

    /**
     * State machine context implementation of this tokenizer.
     */
    private static final class Context implements TokenizerContext {

        /**
         * The tokenizer settings.
         */
        private final TokenizerSettings tokenizerSettings;

        /**
         * The word that is currently being collected.
         */
        @SuppressWarnings("PMD.AvoidStringBufferField")
        private StringBuilder currentWord;

        /**
         * The current parent token or null if there is no parent token.
         */
        private Token parent;

        private Context(final TokenizerSettings tokenizerSettings) {
            this.tokenizerSettings = tokenizerSettings;
            this.currentWord = new StringBuilder();
            this.parent = new Token();
        }

        @Override
        public void beginWord() {
            if (!currentWord.isEmpty()) {
                final Token newChild = new Token(parent, currentWord.toString());
                this.parent.addChild(newChild);
            }
            this.parent = new Token(this.parent);
            currentWord = new StringBuilder();
        }

        @Override
        public void endWord() {
            boolean parentUpdated = false;
            if (parent.children().isEmpty() && parent.parent() != null) {
                parent = parent.parent();
                parentUpdated = true;
            }
            parent.addChild(new Token(parent, currentWord.toString()));
            currentWord = new StringBuilder();
            if (!parentUpdated && parent.parent() != null) {
                parent = parent.parent();
            }
        }

        @Override
        public void appendCodePoint(final int codePoint) {
            currentWord.appendCodePoint(codePoint);
        }

        @Override
        public TokenizerSettings settings() {
            return tokenizerSettings;
        }
    }
}
