package org.betonquest.betonquest.quest.objective.timer;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveService;

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
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<Number> targetAmount = instruction.number().get("amount", Integer.MAX_VALUE);
        final Argument<String> name = instruction.string().get("name", "");
        final Argument<Number> interval = instruction.number().get("interval", 1);
        final Argument<List<ActionID>> doneEvents = instruction.parse(ActionID::new).list().get("done", Collections.emptyList());
        final TimerObjective objective = new TimerObjective(service, targetAmount, questTypeApi, name, interval, doneEvents);
        service.request(PlayerObjectiveChangeEvent.class).handler(objective::onPlayerObjectiveChange)
                .profile(PlayerObjectiveChangeEvent::getProfile).subscribe(false);
        return objective;
    }
}
