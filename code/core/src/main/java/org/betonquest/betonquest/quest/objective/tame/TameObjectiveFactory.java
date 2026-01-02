package org.betonquest.betonquest.quest.objective.tame;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
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
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService eventService) throws QuestException {
        final Argument<EntityType> type = instruction.enumeration(EntityType.class)
                .validate(entityType -> entityType.getEntityClass() != null
                                && Tameable.class.isAssignableFrom(entityType.getEntityClass()),
                        "Entity cannot be tamed: '%s'")
                .get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final TameObjective objective = new TameObjective(instruction, targetAmount, type);
        eventService.request(EntityTameEvent.class).handler(objective::onTaming, this::fromEvent).subscribe(true);
        return objective;
    }

    @Nullable
    private Player fromEvent(final EntityTameEvent event) {
        return event.getOwner() instanceof final Player player ? player : null;
    }
}
