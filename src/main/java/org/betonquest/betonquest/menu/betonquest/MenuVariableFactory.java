package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.online.OnlineVariableAdapter;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory to create {@link MenuVariable}s from {@link Instruction}s.
 */
public class MenuVariableFactory implements PlayerVariableFactory {

    /**
     * The empty default constructor.
     */
    public MenuVariableFactory() {
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        return new OnlineVariableAdapter(new MenuVariable(), profile -> "");
    }
}
