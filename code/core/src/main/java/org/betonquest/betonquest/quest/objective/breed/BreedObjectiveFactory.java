package org.betonquest.betonquest.quest.objective.breed;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
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
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<EntityType> type = instruction.get(instruction.getParsers().forEnum(EntityType.class));
        final Variable<Number> targetAmount = instruction.get(DefaultArgumentParsers.NUMBER_NOT_LESS_THAN_ONE);
        return new BreedObjective(instruction, targetAmount, type);
    }
}
