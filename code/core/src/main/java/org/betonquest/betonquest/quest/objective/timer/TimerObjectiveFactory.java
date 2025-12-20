package org.betonquest.betonquest.quest.objective.timer;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

/**
 * Factory for creating {@link TimerObjective} instances from {@link Instruction}s.
 */
public class TimerObjectiveFactory implements ObjectiveFactory {

    /**
     * The QuestTypeAPI instance.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Constructs a new TimerObjectiveFactory.
     *
     * @param questTypeApi the QuestTypeApi instance
     */
    public TimerObjectiveFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Number> targetAmount = instruction.getValue("amount", Argument.NUMBER, Integer.MAX_VALUE);
        final Variable<String> name = instruction.getValue("name", Argument.STRING, "");
        final Variable<Number> interval = instruction.getValue("interval", Argument.NUMBER, 1);
        final Variable<List<EventID>> doneEvents = instruction.getValueList("done", EventID::new);
        return new TimerObjective(instruction, targetAmount, questTypeApi, name, interval, doneEvents);
    }
}
