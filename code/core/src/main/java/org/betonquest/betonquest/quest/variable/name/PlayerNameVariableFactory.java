package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

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
        final Variable<PlayerNameType> type;
        if (instruction.hasNext()) {
            type = instruction.get(Argument.ENUM(PlayerNameType.class));
        } else {
            type = new Variable<>(PlayerNameType.NAME);
        }
        return new PlayerNameVariable(type);
    }
}
