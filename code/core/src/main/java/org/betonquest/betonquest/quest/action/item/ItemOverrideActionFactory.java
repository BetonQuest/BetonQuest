package org.betonquest.betonquest.quest.action.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create {@link ItemOverrideAction}s from {@link Instruction}s.
 */
public class ItemOverrideActionFactory implements PlayerActionFactory {

    /**
     * Create the item override action factory.
     */
    public ItemOverrideActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ItemWrapper> itemWrapper = instruction.item().get();
        return new OnlineActionAdapter(new ItemOverrideAction(itemWrapper));
    }
}
