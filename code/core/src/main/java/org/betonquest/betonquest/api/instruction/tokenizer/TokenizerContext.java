package org.betonquest.betonquest.api.instruction.tokenizer;

/**
 * State machine context for the {@link TokenizerState}.
 */
public interface TokenizerContext {

    /**
     * Append a code point to the word that is currently being collected.
     *
     * @param codePoint code point to append to the current word
     */
    void appendCodePoint(int codePoint);

    /**
     * End the word that is currently being collected.
     */
    void endWord();
}
