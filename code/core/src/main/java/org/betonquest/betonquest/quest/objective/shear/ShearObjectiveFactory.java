package org.betonquest.betonquest.quest.objective.shear;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.bukkit.DyeColor;

/**
 * Factory for creating {@link ShearObjective} instances from {@link DefaultInstruction}s.
 */
public class ShearObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the ShearObjectiveFactory.
     */
    public ShearObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        final Variable<String> name = instruction.getValue("name", Argument.STRING);
        final Variable<DyeColor> color = instruction.getValue("color", Argument.ENUM(DyeColor.class));
        return new ShearObjective(instruction, targetAmount, name, color);
    }
}
