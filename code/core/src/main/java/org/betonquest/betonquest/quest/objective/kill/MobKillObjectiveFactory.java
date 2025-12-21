package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
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
        final Variable<List<EntityType>> entities = instruction.getList(DefaultArgumentParsers.forEnumeration(EntityType.class));
        final Variable<Number> targetAmount = instruction.get(DefaultArgumentParsers.NUMBER_NOT_LESS_THAN_ONE);
        final Variable<String> name = instruction.getValue("name", DefaultArgumentParsers.STRING);
        final Variable<String> marked = instruction.getValue("marked", PackageArgument.IDENTIFIER);
        return new MobKillObjective(instruction, targetAmount, entities, name, marked);
    }
}
