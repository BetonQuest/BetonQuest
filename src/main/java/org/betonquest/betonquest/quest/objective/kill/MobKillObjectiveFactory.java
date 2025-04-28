package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;
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
        final Variable<List<EntityType>> entities = instruction.getList(Argument.ENUM(EntityType.class));
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        final String name = instruction.getValue("name");
        final Variable<String> marked = instruction.get(instruction.getValue("marked"), PackageArgument.IDENTIFIER);
        return new MobKillObjective(instruction, targetAmount, entities, name, marked);
    }
}
