package org.betonquest.betonquest.quest.objective.breed;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.bukkit.entity.EntityType;

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
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<EntityType> type = instruction.enumeration(EntityType.class).get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        return new BreedObjective(instruction, targetAmount, type);
    }
}
