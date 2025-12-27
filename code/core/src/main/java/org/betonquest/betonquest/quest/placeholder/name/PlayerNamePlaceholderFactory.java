package org.betonquest.betonquest.quest.placeholder.name;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;

/**
 * Factory to create {@link PlayerNamePlaceholder}s from {@link Instruction}s.
 */
public class PlayerNamePlaceholderFactory implements PlayerPlaceholderFactory {

    /**
     * Create a PlayerName placeholder factory.
     */
    public PlayerNamePlaceholderFactory() {

    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<PlayerNameType> type;
        if (instruction.hasNext()) {
            type = instruction.enumeration(PlayerNameType.class).get();
        } else {
            type = new DefaultArgument<>(PlayerNameType.NAME);
        }
        return new PlayerNamePlaceholder(type);
    }
}
