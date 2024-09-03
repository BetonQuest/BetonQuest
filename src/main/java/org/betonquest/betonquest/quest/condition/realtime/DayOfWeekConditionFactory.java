package org.betonquest.betonquest.quest.condition.realtime;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.util.Locale;

/**
 * Factory to create day of weeks conditions from {@link Instruction}s.
 */
public class DayOfWeekConditionFactory implements PlayerlessConditionFactory {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Create the day of week condition factory.
     *
     * @param log the logger to use when parsing the day
     */
    public DayOfWeekConditionFactory(final BetonQuestLogger log) {
        this.log = log;
    }

    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.LocalVariableCouldBeFinal"})
    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        final String dayString = instruction.next();
        DayOfWeek dayOfWeek;
        try {
            dayOfWeek = DayOfWeek.of(Integer.parseInt(dayString));
        } catch (final DateTimeException e) {
            throw new InstructionParseException(dayString + " is not a valid day of a week", e);
        } catch (final NumberFormatException e) {
            log.debug(instruction.getPackage(), "Could not parse number!", e);
            try {
                dayOfWeek = DayOfWeek.valueOf(dayString.toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException iae) {
                throw new InstructionParseException(dayString + " is not a valid day of a week", iae);
            }
        }
        return new DayOfWeekCondition(dayOfWeek);
    }
}
