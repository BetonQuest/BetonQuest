package org.betonquest.betonquest.compatibility.traincarts.objectives;

import com.bergerkiller.bukkit.tc.events.seat.MemberSeatEnterEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link TrainCartsRideObjective} instances from {@link Instruction}s.
 */
public class TrainCartsRideObjectiveFactory implements ObjectiveFactory {

    /**
     * Plugin used for scheduling.
     */
    private final Plugin plugin;

    /**
     * Creates a new instance of the TrainCartsRideObjectiveFactory.
     *
     * @param plugin the plugin used for scheduling
     */
    public TrainCartsRideObjectiveFactory(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<String> name = instruction.string().get("name", "");
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get("amount", 1);
        final TrainCartsRideObjective objective = new TrainCartsRideObjective(service, plugin, targetAmount, name);
        service.request(MemberSeatEnterEvent.class).onlineHandler(objective::onMemberSeatEnter)
                .entity(MemberSeatEnterEvent::getEntity).ignoreConditions().subscribe(false);
        service.request(MemberSeatExitEvent.class).onlineHandler(objective::onMemberSeatExit)
                .entity(MemberSeatExitEvent::getEntity).ignoreConditions().subscribe(false);
        service.request(PlayerQuitEvent.class).priority(EventPriority.LOWEST).onlineHandler(objective::onQuit)
                .player(PlayerQuitEvent::getPlayer).ignoreConditions().subscribe(true);
        service.request(PlayerObjectiveChangeEvent.class).handler(objective::onStop)
                .profile(PlayerObjectiveChangeEvent::getProfile).ignoreConditions().subscribe(false);
        return objective;
    }
}
