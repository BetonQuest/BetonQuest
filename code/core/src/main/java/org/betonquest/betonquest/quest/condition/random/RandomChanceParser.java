package org.betonquest.betonquest.quest.condition.random;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;

/**
 * A parser for {@link RandomChance}s.
 */
public class RandomChanceParser implements SimpleArgumentParser<RandomChance> {

    /**
     * The default parser instance for {@link RandomChance}s.
     */
    public static final RandomChanceParser CHANCE = new RandomChanceParser();

    /**
     * The expected number of elements in the string for a random chance.
     */
    private static final int EXPECTED_ELEMENTS = 2;

    /**
     * Creates a new random chance parser.
     */
    public RandomChanceParser() {
    }

    @Override
    public RandomChance apply(final String string) throws QuestException {
        final String[] split = string.split("-");
        if (split.length != EXPECTED_ELEMENTS) {
            throw new QuestException("Wrong randomness format. Use <chance>-<max>");
        }
        final Number pick = NumberParser.parse(split[0]);
        final Number rangeMax = NumberParser.parse(split[1]);
        return new RandomChance(pick, rangeMax);
    }
}
