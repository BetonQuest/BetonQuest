package org.betonquest.betonquest.quest.variable.constant;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariableAdapter;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A factory for creating constant variables.
 */
public class ConstantVariableFactory implements PlayerVariableFactory, PlayerlessVariableFactory {

    /**
     * Create a new constant variable factory.
     */
    public ConstantVariableFactory() {
    }

    @Override
    public PlayerVariable parsePlayer(final DefaultInstruction instruction) throws QuestException {
        return parseConstantVariable(instruction);
    }

    @Override
    public PlayerlessVariable parsePlayerless(final DefaultInstruction instruction) throws QuestException {
        return parseConstantVariable(instruction);
    }

    private NullableVariableAdapter parseConstantVariable(final DefaultInstruction instruction) throws QuestException {
        final ConfigurationSection section = instruction.getPackage().getConfig().getConfigurationSection("constants");
        if (section == null) {
            throw new QuestException("No 'constants' section found in the QuestPackage!");
        }
        final String constantTarget = instruction.next();
        final String constant = section.getString(constantTarget);
        if (constant == null) {
            throw new QuestException("No constant with the name '" + constantTarget + "' found in the 'constants' section!");
        }
        return new NullableVariableAdapter(new ConstantVariable(instruction.get(constant, Argument.STRING)));
    }
}
