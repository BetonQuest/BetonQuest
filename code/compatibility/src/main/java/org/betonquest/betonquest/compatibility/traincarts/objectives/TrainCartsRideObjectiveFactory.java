package org.betonquest.betonquest.compatibility.traincarts.objectives;

import com.bergerkiller.bukkit.tc.events.seat.MemberSeatEnterEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Factory for creating {@link TrainCartsRideObjective} instances from {@link Instruction}s.
 */
public class TrainCartsRideObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the TrainCartsRideObjectiveFactory.
     */
    public TrainCartsRideObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<String> name = instruction.string().get("name", "");
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get("amount", 1);
        final TrainCartsRideObjective objective = new TrainCartsRideObjective(instruction, targetAmount, name);
        service.request(MemberSeatEnterEvent.class).handler(objective::onMemberSeatEnter, MemberSeatEnterEvent::getEntity).subscribe(false);
        service.request(MemberSeatExitEvent.class).handler(objective::onMemberSeatExit, MemberSeatExitEvent::getEntity).subscribe(false);
        service.request(PlayerQuitEvent.class).priority(EventPriority.LOWEST)
                .handler(objective::onQuit, PlayerQuitEvent::getPlayer).subscribe(true);
        return objective;
    }
}
