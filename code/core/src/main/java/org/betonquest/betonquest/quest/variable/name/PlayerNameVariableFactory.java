package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.DefaultArgument;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;

/**
 * Factory to create {@link PlayerNameVariable}s from {@link Instruction}s.
 */
public class PlayerNameVariableFactory implements PlayerVariableFactory {

    /**
     * Create a PlayerName variable factory.
     */
    public PlayerNameVariableFactory() {

    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<PlayerNameType> type;
        if (instruction.hasNext()) {
            type = instruction.enumeration(PlayerNameType.class).get();
        } else {
            type = new DefaultArgument<>(PlayerNameType.NAME);
        }
        return new PlayerNameVariable(type);
    }
}
