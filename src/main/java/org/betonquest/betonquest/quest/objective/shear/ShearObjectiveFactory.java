package org.betonquest.betonquest.quest.objective.shear;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.DyeColor;

/**
 * Factory for creating {@link ShearObjective} instances from {@link Instruction}s.
 */
public class ShearObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the ShearObjectiveFactory.
     */
    public ShearObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        final String name = instruction.getValue("name");
        final Variable<DyeColor> color = instruction.getValue("color", Argument.ENUM(DyeColor.class));
        return new ShearObjective(instruction, targetAmount, name, color);
    }
}
