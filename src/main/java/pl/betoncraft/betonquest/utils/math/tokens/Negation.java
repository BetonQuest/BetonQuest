package pl.betoncraft.betonquest.utils.math.tokens;

import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

/**
 * Negation of another token.
 *
 * @deprecated This should be replaced in BQ 2.0 with a real expression parsing lib like
 * https://github.com/fasseg/exp4j
 */
@Deprecated
public class Negation implements Token {

    /**
     * Token that is inside these parenthesis.
     */
    private final Token inside;

    /**
     * Creates new parenthesis around a token.
     *
     * @param inside token that is inside
     */
    public Negation(final Token inside) {
        this.inside = inside;
    }

    @Override
    public double resolve(final String playerID) throws QuestRuntimeException {
        return -inside.resolve(playerID);
    }

    @Override
    public String toString() {
        return '-' + inside.toString();
    }
}
