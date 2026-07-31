package org.betonquest.betonquest.api.instruction.tokenizer.quoting;

/**
 * Tokenizer settings to define symbols used in the instruction string.
 */
public interface QuotingTokenizerSettings {

    /**
     * Default tokenizer settings with single quotes and spaces as separators. Does not support nested quotes.
     */
    QuotingTokenizerSettings DEFAULT = new QuotingTokenizerSettings() {

        @Override
        public boolean isEscape(final int codePoint) {
            return codePoint == '\\';
        }

        @Override
        public boolean isBeginQuote(final int codePoint) {
            return codePoint == '"';
        }

        @Override
        public boolean isEndQuote(final int codePoint) {
            return isBeginQuote(codePoint);
        }

        @Override
        public boolean isSeparator(final int codePoint) {
            return Character.isWhitespace(codePoint);
        }
    };

    /**
     * Tokenizer settings for placeholders with curly brackets and dots as separators. Supports nested quotes.
     */
    QuotingTokenizerSettings PLACEHOLDER = new QuotingTokenizerSettings() {

        @Override
        public boolean isEscape(final int codePoint) {
            return codePoint == '\\';
        }

        @Override
        public boolean isBeginQuote(final int codePoint) {
            return codePoint == '{';
        }

        @Override
        public boolean isEndQuote(final int codePoint) {
            return codePoint == '}';
        }

        @Override
        public boolean isSeparator(final int codePoint) {
            return codePoint == '.';
        }

        @Override
        public boolean ignoreEmptyQuotedWords() {
            return true;
        }
    };

    /**
     * Check if the given code point is an escape character.
     *
     * @param codePoint the code point to check
     * @return true if the code point is an escape character, false otherwise
     */
    boolean isEscape(int codePoint);

    /**
     * Check if the given code point is a begin-quote character.
     *
     * @param codePoint the code point to check
     * @return true if the code point is a begin-quote character, false otherwise
     */
    boolean isBeginQuote(int codePoint);

    /**
     * Check if the given code point is the end quote character.
     *
     * @param codePoint the code point to check
     * @return true if the code point is the end quote character, false otherwise
     */
    boolean isEndQuote(int codePoint);

    /**
     * Check if the given code point is a separator character.
     *
     * @param codePoint the code point to check
     * @return true if the code point is a separator character, false otherwise
     */
    boolean isSeparator(int codePoint);

    /**
     * If true, empty words will be ignored.
     *
     * @return true if empty words should be ignored, false otherwise
     */
    default boolean ignoreEmptyQuotedWords() {
        return false;
    }
}
