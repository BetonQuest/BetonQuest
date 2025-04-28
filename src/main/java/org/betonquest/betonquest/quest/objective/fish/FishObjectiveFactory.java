package org.betonquest.betonquest.quest.objective.fish;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;
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
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Item> item = instruction.get(PackageArgument.ITEM);
        final Variable<Number> targetAmount = instruction.getVariable(Argument.NUMBER_NOT_LESS_THAN_ONE);

        final String loc = instruction.getOptional("hookLocation");
        final String range = instruction.getOptional("range");
        final boolean hookIsNotNull = loc != null && range != null;
        final Variable<Location> hookTargetLocation = hookIsNotNull ? instruction.getVariable(loc, Argument.LOCATION) : null;
        final Variable<Number> rangeVar = hookIsNotNull ? instruction.getVariable(range, Argument.NUMBER) : null;
        return new FishObjective(instruction, targetAmount, item, hookTargetLocation, rangeVar);
    }
}
