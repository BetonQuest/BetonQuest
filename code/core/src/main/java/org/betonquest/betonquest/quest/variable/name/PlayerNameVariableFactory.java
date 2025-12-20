package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;

/**
 * Factory to create {@link PlayerNameVariable}s from {@link DefaultInstruction}s.
 */
public class PlayerNameVariableFactory implements PlayerVariableFactory {

    /**
     * Create a PlayerName variable factory.
     */
    public PlayerNameVariableFactory() {

    }

    @Override
    public PlayerVariable parsePlayer(final DefaultInstruction instruction) throws QuestException {
        final Variable<PlayerNameType> type;
        if (instruction.hasNext()) {
            type = instruction.get(Argument.ENUM(PlayerNameType.class));
        } else {
            type = new Variable<>(PlayerNameType.NAME);
        }
        return new PlayerNameVariable(type);
    }
}
