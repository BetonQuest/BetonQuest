package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
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
        final Argument<List<EntityType>> entities = instruction.enumeration(EntityType.class).getList();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final Argument<String> name = instruction.string().get("name").orElse(null);
        final Argument<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        return new MobKillObjective(instruction, targetAmount, entities, name, marked);
    }
}
