package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.entity.EntityType;

import java.util.List;

/**
 * Factory for creating {@link MobKillObjective} instances from {@link Instruction}s.
 */
public class MobKillObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the MobKillObjectiveFactory.
     */
    public MobKillObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final List<EntityType> entities = instruction.getList(mob -> instruction.getEnum(mob, EntityType.class));
        final VariableNumber targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        final String name = instruction.getOptional("name");
        final VariableIdentifier marked = instruction.get(instruction.getOptional("marked"), VariableIdentifier::new);
        return new MobKillObjective(instruction, targetAmount, entities, name, marked);
    }
}
