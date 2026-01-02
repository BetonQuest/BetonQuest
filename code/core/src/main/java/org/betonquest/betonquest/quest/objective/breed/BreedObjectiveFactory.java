package org.betonquest.betonquest.quest.objective.breed;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityBreedEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for creating {@link BreedObjective} instances from {@link Instruction}s.
 */
public class BreedObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the BreedObjectiveFactory.
     */
    public BreedObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<EntityType> type = instruction.enumeration(EntityType.class).get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final BreedObjective objective = new BreedObjective(instruction, targetAmount, type);
        service.request(EntityBreedEvent.class).handler(objective::onBreeding, this::fromBreedEvent).subscribe();
        return objective;
    }

    @Nullable
    private Player fromBreedEvent(final EntityBreedEvent event) {
        return event.getBreeder() instanceof final Player player ? player : null;
    }
}
