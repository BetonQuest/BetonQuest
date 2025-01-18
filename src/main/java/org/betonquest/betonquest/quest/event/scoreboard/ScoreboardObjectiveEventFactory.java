package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

import java.util.Locale;

/**
 * Factory to create scoreboard events from {@link Instruction}s.
 */
public class ScoreboardObjectiveEventFactory implements EventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * The variable processor to use.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create the scoreboard event factory.
     *
     * @param data              the data for primary server thread access
     * @param variableProcessor the variable processor to use
     */
    public ScoreboardObjectiveEventFactory(final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final String objective = instruction.next();
        final String number = instruction.next();
        final String action = instruction.getOptional("action");
        if (action != null) {
            try {
                final ScoreModification type = ScoreModification.valueOf(action.toUpperCase(Locale.ROOT));
                return new PrimaryServerThreadEvent(
                        new ScoreboardObjectiveEvent(objective, new VariableNumber(variableProcessor, instruction.getPackage(), number), type),
                        data);
            } catch (final IllegalArgumentException e) {
                throw new QuestException("Unknown modification action: " + instruction.current(), e);
            }
        }
        if (!number.isEmpty() && number.charAt(0) == '*') {
            return new PrimaryServerThreadEvent(
                    new ScoreboardObjectiveEvent(objective, new VariableNumber(variableProcessor, instruction.getPackage(), number.replace("*", "")), ScoreModification.MULTIPLY),
                    data);
        }
        return new PrimaryServerThreadEvent(
                new ScoreboardObjectiveEvent(objective, new VariableNumber(variableProcessor, instruction.getPackage(), number), ScoreModification.ADD),
                data);
    }
}
