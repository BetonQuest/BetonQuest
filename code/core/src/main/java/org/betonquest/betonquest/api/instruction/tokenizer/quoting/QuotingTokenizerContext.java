package org.betonquest.betonquest.api.instruction.tokenizer.quoting;

import org.betonquest.betonquest.api.instruction.tokenizer.Token;
import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerContext;

/**
 * The tokenizer context for the quoting tokenizer.
 */
public class QuotingTokenizerContext implements TokenizerContext<QuotingTokenizerSettings> {

    /**
     * The tokenizer settings.
     */
    private final QuotingTokenizerSettings tokenizerSettings;

    /**
     * The word that is currently being collected.
     */
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private StringBuilder currentWord;

    /**
     * The current parent token or null if there is no parent token.
     */
    private Token parent;

    /**
     * Create a new tokenizer context.
     *
     * @param tokenizerSettings the tokenizer settings
     */
    public QuotingTokenizerContext(final QuotingTokenizerSettings tokenizerSettings) {
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
        boolean alreadyClosed = false;
        if (parent.parent() != null && parent.children().isEmpty()) {
            closeChild();
            alreadyClosed = true;
        }
        if (!currentWord.isEmpty() || !tokenizerSettings.ignoreEmptyQuotedWords()) {
            parent.addChild(new Token(parent, currentWord.toString()));
        }
        currentWord = new StringBuilder();
        if (!alreadyClosed) {
            closeChild();
        }
    }

    private void closeChild() {
        if (parent.parent() != null) {
            if (!parent.isEmpty()) {
                parent.parent().addChild(parent);
            }
            parent = parent.parent();
        }
    }

    @Override
    public void appendCodePoint(final int codePoint) {
        currentWord.appendCodePoint(codePoint);
    }

    @Override
    public QuotingTokenizerSettings settings() {
        return tokenizerSettings;
    }

    /**
     * Get the parent token.
     *
     * @return the parent token.
     */
    public Token getParent() {
        return parent;
    }
}
