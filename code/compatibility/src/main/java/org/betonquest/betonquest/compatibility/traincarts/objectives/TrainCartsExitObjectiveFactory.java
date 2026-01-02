package org.betonquest.betonquest.compatibility.traincarts.objectives;

import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<String> name = instruction.string().get("name", "");
        final TrainCartsExitObjective objective = new TrainCartsExitObjective(instruction, name);
        service.request(MemberSeatExitEvent.class).handler(objective::onMemberSeatExit, this::fromEvent).subscribe(false);
        return objective;
    }

    @Nullable
    private Player fromEvent(final MemberSeatExitEvent event) {
        return event.getEntity() instanceof final Player player ? player : null;
    }
}
