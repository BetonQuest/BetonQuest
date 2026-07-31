package org.betonquest.betonquest.lib.instruction.tokenizer;

/**
 * State machine context for the {@link TokenizerState}.
 *
 * @param <T> the type of the tokenizer settings
 */
public interface TokenizerContext<T> {

    /**
     * Append a code point to the word that is currently being collected.
     *
     * @param codePoint code point to append to the current word
     */
    void appendCodePoint(int codePoint);

    /**
     * Begin a new word nested in the current word.
     */
    void beginWord();

    /**
     * End the word that is currently being collected.
     */
    void endWord();

    /**
     * Get the settings of the tokenizer.
     *
     * @return the tokenizer settings
     */
    T settings();
}
