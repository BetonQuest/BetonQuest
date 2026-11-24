package org.betonquest.betonquest.lib.instruction.tokenizer.quoting;

import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerState;

/**
 * The state of an unquoted word. Depending on the settings, escaping is processed
 * and quote characters are either treated as regular characters or rejected.
 */
public class PureWordState implements TokenizerState<QuotingTokenizerContext> {

    /**
     * Create the pure word collection state.
     */
    public PureWordState() {
    }

    @Override
    public TokenizerState<QuotingTokenizerContext> parseNext(final QuotingTokenizerContext ctx, final int codePoint) throws TokenizerException {
        final QuotingTokenizerSettings settings = ctx.settings();
        if (settings.isSeparator(codePoint)) {
            ctx.endWord();
            return new NoWordState();
        }
        if (settings.isEscapingInUnquotedWords() && settings.isEscape(codePoint)) {
            return new EscapeState(this, codePoint);
        }
        if (!settings.isQuotingInUnquotedWordsAllowed() && (settings.isBeginQuote(codePoint) || settings.isEndQuote(codePoint))) {
            throw new TokenizerException("Unexpected quote in unquoted word: '%s'".formatted(Character.toString(codePoint)));
        }
        ctx.appendCodePoint(codePoint);
        return this;
    }

    @Override
    public void parseEnd(final QuotingTokenizerContext ctx) {
        ctx.endWord();
    }
}
