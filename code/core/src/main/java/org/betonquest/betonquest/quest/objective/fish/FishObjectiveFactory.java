package org.betonquest.betonquest.quest.objective.fish;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.bukkit.Location;

/**
 * Factory for creating {@link FishObjective} instances from {@link Instruction}s.
 */
public class FishObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the FishObjectiveFactory.
     */
    public FishObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<ItemWrapper> item = instruction.item().get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final Argument<Location> hookTargetLocation = instruction.location().get("hookLocation").orElse(null);
        final Argument<Number> range = instruction.number().get("range").orElse(null);
        return new FishObjective(instruction, targetAmount, item, hookTargetLocation, range);
    }
}
