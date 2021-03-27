package pl.betoncraft.betonquest.utils.math.tokens;

/**
 * Token that is just any number
 *
 * @deprecated This should be replaced in BQ 2.0 with a real expression parsing lib like
 * https://github.com/fasseg/exp4j
 */
@Deprecated
public class Number implements Token {

    /**
     * The value
     */
    private final double value;

    /**
     * Creates a new number
     *
     * @param value value of the number
     */
    public Number(final double value) {
        this.value = value;
    }

    @Override
    public double resolve(final String playerID) {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
