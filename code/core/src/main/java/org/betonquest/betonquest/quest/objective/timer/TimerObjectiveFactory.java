package org.betonquest.betonquest.quest.objective.timer;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.Collections;
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
        final Argument<Number> targetAmount = instruction.number().get("amount", Integer.MAX_VALUE);
        final Argument<String> name = instruction.string().get("name", "");
        final Argument<Number> interval = instruction.number().get("interval", 1);
        final Argument<List<EventID>> doneEvents = instruction.parse(EventID::new).getList("done", Collections.emptyList());
        return new TimerObjective(instruction, targetAmount, questTypeApi, name, interval, doneEvents);
    }
}
