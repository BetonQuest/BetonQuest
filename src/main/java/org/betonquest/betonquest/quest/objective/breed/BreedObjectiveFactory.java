package org.betonquest.betonquest.quest.objective.breed;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
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
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final EntityType type = instruction.getEnum(EntityType.class);
        final VariableNumber targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        return new BreedObjective(instruction, targetAmount, type);
    }
}
