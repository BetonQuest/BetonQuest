package org.betonquest.betonquest.util.math.tokens;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Parenthesis around another token.
 *
 * @deprecated This should be replaced in BQ 2.0 with a real expression parsing lib like
 * <a href="https://github.com/fasseg/exp4j">fasseg/exp4j</a>
 */
@Deprecated
public class Parenthesis implements Token {

    /**
     * Token that is inside these parenthesis.
     */
    private final Token inside;

    /**
     * The character used for opening the parenthesis.
     */
    private final char openingSymbol;

    /**
     * The character used for closing the parenthesis.
     */
    private final char closingSymbol;

    /**
     * Creates new parenthesis around a token.
     *
     * @param inside        token that is inside
     * @param openingSymbol opening character
     * @param closingSymbol closing character
     */
    public Parenthesis(final Token inside, final char openingSymbol, final char closingSymbol) {
        this.inside = inside;
        this.openingSymbol = openingSymbol;
        this.closingSymbol = closingSymbol;
    }

    @Override
    public double resolve(@Nullable final Profile profile) throws QuestException {
        return inside.resolve(profile);
    }

    @Override
    public String toString() {
        return openingSymbol + inside.toString() + closingSymbol;
    }
}
