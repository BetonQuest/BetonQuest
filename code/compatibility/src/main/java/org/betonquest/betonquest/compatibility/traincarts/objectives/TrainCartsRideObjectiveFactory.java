package org.betonquest.betonquest.compatibility.traincarts.objectives;

import com.bergerkiller.bukkit.tc.events.seat.MemberSeatEnterEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

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
        service.request(MemberSeatEnterEvent.class).handler(objective::onMemberSeatEnter, this::fromEvent).subscribe(false);
        service.request(MemberSeatExitEvent.class).handler(objective::onMemberSeatExit, this::fromEvent).subscribe(false);
        service.request(PlayerQuitEvent.class).priority(EventPriority.LOWEST)
                .handler(objective::onQuit, PlayerQuitEvent::getPlayer).subscribe(true);
        return objective;
    }

    @Nullable
    private Player fromEvent(final MemberSeatEnterEvent event) {
        return event.getEntity() instanceof final Player player ? player : null;
    }

    @Nullable
    private Player fromEvent(final MemberSeatExitEvent event) {
        return event.getEntity() instanceof final Player player ? player : null;
    }
}
