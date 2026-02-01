package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.OnlinePlaceholderAdapter;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;

/**
 * Factory to create {@link MenuPlaceholder}s from {@link Instruction}s.
 */
public class MenuPlaceholderFactory implements PlayerPlaceholderFactory {

    /**
     * The empty default constructor.
     */
    public MenuPlaceholderFactory() {
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) {
        return new OnlinePlaceholderAdapter(new MenuPlaceholder(), profile -> "");
    }
}
