package org.betonquest.betonquest.quest.variable.constant;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.variable.Variable;
import org.betonquest.betonquest.api.quest.variable.VariableFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A factory for creating Eval variables.
 */
public class ConstantVariableFactory implements VariableFactory {
    /**
     * Create a new Eval variable factory.
     */
    public ConstantVariableFactory() {
    }

    @Override
    public Variable parse(final Instruction instruction) throws InstructionParseException {
        final ConfigurationSection section = instruction.getPackage().getConfig().getConfigurationSection("constants");
        if (section == null) {
            throw new InstructionParseException("No 'constants' section found in the QuestPackage!");
        }
        final String constantTarget = instruction.next();
        final String constant = section.getString(constantTarget);
        if (constant == null) {
            throw new InstructionParseException("No constant with the name '" + constantTarget + "' found in the 'constants' section!");
        }
        return new ConstantVariable(constant);
    }
}
