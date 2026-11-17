package org.betonquest.betonquest.quest.objective.lectern;

import io.papermc.paper.event.player.PlayerLecternPageChangeEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.Location;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

/**
 * Factory for creating {@link LecternObjective} instances from {@link Instruction}s.
 */
public class LecternObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the LecternObjectiveFactory.
     */
    public LecternObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        final Optional<Argument<Number>> page = instruction.number().get("page");

        final LecternObjective objective = new LecternObjective(service, location, page.orElse(null));

        service.request(PlayerInteractEvent.class)
                .priority(EventPriority.MONITOR)
                .onlineHandler(objective::onInteract)
                .player(PlayerInteractEvent::getPlayer)
                .subscribe(true);

        service.request(PlayerLecternPageChangeEvent.class)
                .priority(EventPriority.MONITOR)
                .onlineHandler(objective::onPageChange)
                .player(PlayerLecternPageChangeEvent::getPlayer)
                .subscribe(true);

        return objective;
    }
}
