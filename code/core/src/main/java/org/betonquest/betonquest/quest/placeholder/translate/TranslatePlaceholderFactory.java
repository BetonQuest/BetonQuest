package org.betonquest.betonquest.quest.placeholder.translate;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholderAdapter;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.text.Text;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A factory for creating translatable placeholders.
 */
public class TranslatePlaceholderFactory implements PlayerPlaceholderFactory, PlayerlessPlaceholderFactory {

    /**
     * The {@link Instructions} to use.
     */
    private final Instructions instructions;

    /**
     * Create a new translatable placeholder factory.
     *
     * @param instructions the instructions instance
     */
    public TranslatePlaceholderFactory(final Instructions instructions) {
        this.instructions = instructions;
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
        final String translateTarget = String.join(".", instruction.getValueParts());
        final Argument<Text> textArgument = instructions.createSection(instruction.getPackage(), section).read().value(translateTarget).translationSection().get();
        return new NullablePlaceholderAdapter(new TranslatePlaceholder(textArgument));
    }
}
