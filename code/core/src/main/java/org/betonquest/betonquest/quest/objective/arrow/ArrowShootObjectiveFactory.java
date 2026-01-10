package org.betonquest.betonquest.quest.objective.arrow;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jspecify.annotations.Nullable;

/**
 * Factory for creating {@link ArrowShootObjective} instances from {@link Instruction}s.
 */
public class ArrowShootObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the ArrowShootObjectiveFactory.
     */
    public ArrowShootObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        final Argument<Number> range = instruction.number().get();
        final ArrowShootObjective objective = new ArrowShootObjective(service, location, range);
        service.request(ProjectileHitEvent.class).onlineHandler(objective::onArrowHit)
                .player(this::fromEvent).subscribe(true);
        return objective;
    }

    @Nullable
    private Player fromEvent(final ProjectileHitEvent event) {
        return event.getEntity().getShooter() instanceof final Player player ? player : null;
    }
}
