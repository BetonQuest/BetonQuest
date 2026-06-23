package org.betonquest.betonquest.quest.placeholder.constant;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholderAdapter;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A factory for creating constant placeholders.
 */
public class ConstantPlaceholderFactory implements PlayerPlaceholderFactory, PlayerlessPlaceholderFactory {

    /**
     * Create a new constant placeholder factory.
     */
    public ConstantPlaceholderFactory() {
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        return parseConstantPlaceholder(instruction);
    }

    @Override
    public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
        return parseConstantPlaceholder(instruction);
    }

    private NullablePlaceholderAdapter parseConstantPlaceholder(final Instruction instruction) throws QuestException {
        final ConfigurationSection section = instruction.getPackage().getConfig().getConfigurationSection("constants");
        if (section == null) {
            throw new QuestException("No 'constants' section found in the QuestPackage!");
        }
        final String constantTarget = String.join(".", instruction.getValueParts());
        if (section.isConfigurationSection(constantTarget)) {
            throw new QuestException("Path '" + constantTarget + "' is not a constant but a configuration section!");
        }
        final String constant = section.getString(constantTarget);
        if (constant == null) {
            throw new QuestException("No constant with the name '" + constantTarget + "' found in the 'constants' section!");
        }
        return new NullablePlaceholderAdapter(new ConstantPlaceholder(instruction.chainForArgument(constant).string().get()));
    }
}
