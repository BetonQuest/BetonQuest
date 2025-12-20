package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.online.OnlineVariableAdapter;

/**
 * Factory to create {@link MenuVariable}s from {@link DefaultInstruction}s.
 */
public class MenuVariableFactory implements PlayerVariableFactory {

    /**
     * The empty default constructor.
     */
    public MenuVariableFactory() {
    }

    @Override
    public PlayerVariable parsePlayer(final DefaultInstruction instruction) {
        return new OnlineVariableAdapter(new MenuVariable(), profile -> "");
    }
}
