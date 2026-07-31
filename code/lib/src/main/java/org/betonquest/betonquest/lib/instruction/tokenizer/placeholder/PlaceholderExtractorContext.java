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
     * The context settings.
     */
    private final PlaceholderExtractorSettings settings;

    /**
     * The word that is currently being collected.
     */
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private StringBuilder word = new StringBuilder();

    /**
     * Create a new context.
     *
     * @param settings the context settings
     */
    public PlaceholderExtractorContext(final PlaceholderExtractorSettings settings) {
        this.settings = settings;
    }

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
        if (!word.isEmpty()) {
            tokens.add(new Token(null, word.toString()));
        }
        word = new StringBuilder();
    }

    @Override
    public PlaceholderExtractorSettings settings() {
        return settings;
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
