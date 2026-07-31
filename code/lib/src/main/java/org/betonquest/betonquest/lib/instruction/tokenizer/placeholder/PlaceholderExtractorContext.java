package org.betonquest.betonquest.lib.instruction.tokenizer.placeholder;

import org.betonquest.betonquest.lib.instruction.tokenizer.Token;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerContext;

import java.util.ArrayList;
import java.util.List;

/**
 * The context for the placeholder extractor.
 */
public class PlaceholderExtractorContext implements TokenizerContext<PlaceholderExtractorSettings> {

    /**
     * The tokens that have been extracted.
     */
    private final List<Token> tokens = new ArrayList<>();

    /**
     * The word that is currently being collected.
     */
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private StringBuilder word = new StringBuilder();

    @Override
    public void appendCodePoint(final int codePoint) {
        word.appendCodePoint(codePoint);
    }

    @Override
    public void beginWord() {
        // Empty
    }

    @Override
    public void endWord() {
        tokens.add(new Token(null, word.toString()));
        word = new StringBuilder();
    }

    @Override
    public PlaceholderExtractorSettings settings() {
        return PlaceholderExtractorSettings.DEFAULT;
    }

    /**
     * Get the tokens that have been extracted.
     *
     * @return the tokens
     */
    public List<Token> getTokens() {
        return tokens;
    }
}
