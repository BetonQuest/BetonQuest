package pl.betoncraft.betonquest.utils.math.tokens;

import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

/**
 * Returns the absolute value (see {@link Math#abs(double)}) of a token
 *
 * @deprecated This should be replaced in BQ 2.0 with a real expression parsing lib like
 * https://github.com/fasseg/exp4j
 */
@Deprecated
public class AbsoluteValue implements Token {

    /**
     * Token that is inside
     */
    private final Token inside;

    /**
     * Constructs a new absolute value
     *
     * @param inside token that is inside
     */
    public AbsoluteValue(final Token inside) {
        this.inside = inside;
    }

    @Override
    public double resolve(final String playerID) throws QuestRuntimeException {
        return Math.abs(inside.resolve(playerID));
    }

    @Override
    public String toString() {
        return '|' + inside.toString() + '|';
    }
}
