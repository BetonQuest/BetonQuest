package org.betonquest.betonquest.quest.objective.shear;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
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
        final Variable<Number> targetAmount = instruction.get(DefaultArgumentParsers.NUMBER_NOT_LESS_THAN_ONE);
        final Variable<String> name = instruction.getValue("name", DefaultArgumentParsers.STRING);
        final Variable<DyeColor> color = instruction.getValue("color", DefaultArgumentParsers.forEnum(DyeColor.class));
        return new ShearObjective(instruction, targetAmount, name, color);
    }
}
