package org.betonquest.betonquest.instruction.argument.types;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.ValueChecker;
import org.betonquest.betonquest.instruction.argument.Argument;

/**
 * Parses a string to a number.
 */
public class NumberParser implements Argument<Number> {
    /**
     * The used {@link ValueChecker} for the number.
     */
    private final ValueChecker<Number> valueChecker;

    /**
     * Creates a new parser for numbers.
     */
    public NumberParser() {
        this(value -> {
        });
    }

    /**
     * Creates a new parser for numbers with a custom {@link ValueChecker}.
     *
     * @param valueChecker the {@link ValueChecker} to use for the number
     */
    public NumberParser(final ValueChecker<Number> valueChecker) {
        this.valueChecker = valueChecker;
    }

    /**
     * Parses the given value to a number.
     *
     * @param value the value to parse
     * @return the parsed number
     * @throws QuestException if the value could not be parsed
     */
    public static Number parse(final String value) throws QuestException {
        try {
            return Double.parseDouble(value);
        } catch (final NumberFormatException e) {
            throw new QuestException("Could not parse number: " + value, e);
        }
    }

    @Override
    public Number apply(final String string) throws QuestException {
        final Number parse = parse(string);
        valueChecker.check(parse);
        return parse;
    }
}
