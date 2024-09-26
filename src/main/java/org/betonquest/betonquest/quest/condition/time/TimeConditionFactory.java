package org.betonquest.betonquest.quest.condition.time;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.location.VariableWorld;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * Factory to create test for time conditions from {@link Instruction}s.
 */
public class TimeConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * The variable processor used to process variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create the test for time condition factory.
     *
     * @param data              the data used for checking the condition on the main thread
     * @param variableProcessor the variable processor used to process variables
     */
    public TimeConditionFactory(final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final TimeFrame timeFrame = getTimeFrame(instruction.next());
        final VariableWorld world = new VariableWorld(variableProcessor, instruction.getPackage(), instruction.getOptional("world", "%location.world%"));
        return new PrimaryServerThreadPlayerCondition(
                new NullableConditionAdapter(new TimeCondition(timeFrame, world)), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        final String worldString = instruction.getOptional("world");
        if (worldString == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        final TimeFrame timeFrame = getTimeFrame(instruction.next());
        final VariableWorld world = new VariableWorld(variableProcessor, instruction.getPackage(), worldString);
        return new PrimaryServerThreadPlayerlessCondition(
                new NullableConditionAdapter(new TimeCondition(timeFrame, world)), data);
    }

    @NotNull
    private TimeFrame getTimeFrame(final String time) throws InstructionParseException {
        final String[] theTime = time.split("-");
        final int expectedLength = 2;
        if (theTime.length != expectedLength) {
            throw new InstructionParseException("Wrong time format. Expected format: <time>-<time>");
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
        return new TimeFrame(timeMin, timeMax);
    }

    private boolean isValidTime(final double time) {
        return time >= 0 && time <= 24;
    }
}
