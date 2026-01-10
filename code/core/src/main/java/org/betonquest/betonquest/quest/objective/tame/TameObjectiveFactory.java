package org.betonquest.betonquest.quest.objective.tame;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityTameEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for creating {@link TameObjective} instances from {@link Instruction}s.
 */
public class TameObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new TameObjectiveFactory instance.
     */
    public TameObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<EntityType> type = instruction.enumeration(EntityType.class)
                .validate(entityType -> entityType.getEntityClass() != null
                                && Tameable.class.isAssignableFrom(entityType.getEntityClass()),
                        "Entity cannot be tamed: '%s'")
                .get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final TameObjective objective = new TameObjective(service, targetAmount, type);
        service.request(EntityTameEvent.class).onlineHandler(objective::onTaming)
                .player(this::fromEvent).subscribe(true);
        return objective;
    }

    @Nullable
    private Player fromEvent(final EntityTameEvent event) {
        return event.getOwner() instanceof final Player player ? player : null;
    }
}
