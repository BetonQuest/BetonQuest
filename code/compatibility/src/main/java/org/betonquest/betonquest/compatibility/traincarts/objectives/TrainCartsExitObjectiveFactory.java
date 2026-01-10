package org.betonquest.betonquest.compatibility.traincarts.objectives;

import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

/**
 * Factory for creating {@link TrainCartsExitObjective} instances from {@link Instruction}s.
 */
public class TrainCartsExitObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the TrainCartsExitObjectiveFactory.
     */
    public TrainCartsExitObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<String> name = instruction.string().get("name", "");
        final TrainCartsExitObjective objective = new TrainCartsExitObjective(service, name);
        service.request(MemberSeatExitEvent.class).onlineHandler(objective::onMemberSeatExit)
                .entity(MemberSeatExitEvent::getEntity).subscribe(false);
        return objective;
    }
}
