package org.betonquest.betonquest.api.instruction.tokenizer;

/**
 * Tokenizer settings to define symbols used in the instruction string.
 */
public interface TokenizerSettings {

    /**
     * Default tokenizer settings with single quotes and spaces as separators. Does not support nested quotes.
     */
    TokenizerSettings DEFAULT = new TokenizerSettings() {

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
}
