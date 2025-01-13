package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.exceptions.QuestException;

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
        final PlayerNameType type;
        if (instruction.hasNext()) {
            type = instruction.getEnum(PlayerNameType.class);
        } else {
            type = PlayerNameType.NAME;
        }
        return new PlayerNameVariable(type);
    }
}
