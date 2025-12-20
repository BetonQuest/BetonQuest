package org.betonquest.betonquest.quest.objective.fish;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.InstructionIdentifierArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.bukkit.Location;

/**
 * Factory for creating {@link FishObjective} instances from {@link DefaultInstruction}s.
 */
public class FishObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the FishObjectiveFactory.
     */
    public FishObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<Item> item = instruction.get(InstructionIdentifierArgument.ITEM);
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);

        final String loc = instruction.getValue("hookLocation");
        final String range = instruction.getValue("range");
        final boolean hookIsNotNull = loc != null && range != null;
        final Variable<Location> hookTargetLocation = hookIsNotNull ? instruction.get(loc, Argument.LOCATION) : null;
        final Variable<Number> rangeVar = hookIsNotNull ? instruction.get(range, Argument.NUMBER) : null;
        return new FishObjective(instruction, targetAmount, item, hookTargetLocation, rangeVar);
    }
}
