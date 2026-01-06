package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.bukkit.Location;

import java.util.List;

/**
 * Factory to create chest events from {@link Instruction}s.
 */
public class ChestGiveActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Create the chest give event factory.
     */
    public ChestGiveActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createChestGiveEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createChestGiveEvent(instruction);
    }

    private NullableActionAdapter createChestGiveEvent(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        final Argument<List<ItemWrapper>> items = instruction.item().list().get();
        return new NullableActionAdapter(new ChestGiveAction(location, items)
        );
    }
}
