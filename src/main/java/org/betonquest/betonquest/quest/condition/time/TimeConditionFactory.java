package org.betonquest.betonquest.quest.condition.time;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create test for time conditions from {@link Instruction}s.
 */
public class TimeConditionFactory implements PlayerConditionFactory {

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the test for time condition factory.
     *
     * @param data the data used for checking the condition on the main thread
     */
    public TimeConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final String[] theTime = instruction.next().split("-");
        final int expectedLength = 2;
        if (theTime.length != expectedLength) {
            throw new InstructionParseException("Wrong time format");
        }
        final double timeMin;
        final double timeMax;
        try {
            timeMin = Double.parseDouble(theTime[0]);
            timeMax = Double.parseDouble(theTime[1]);
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Could not parse time", e);
        }
        if (!(isValidTime(timeMin) && isValidTime(timeMax))) {
            throw new InstructionParseException("Time must be between 0 and 24");
        }
        return new PrimaryServerThreadPlayerCondition(new TimeCondition(timeMin, timeMax), data);
    }

    private boolean isValidTime(final double time) {
        return time >= 0 && time <= 24;
    }

}
