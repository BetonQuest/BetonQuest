package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.bukkit.Location;

/**
 * Factory to create chest events from {@link Instruction}s.
 */
public class ChestClearEventFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Create the chest clear event factory.
     */
    public ChestClearEventFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createChestClearEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createChestClearEvent(instruction);
    }

    private NullableActionAdapter createChestClearEvent(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        return new NullableActionAdapter(new ChestClearEvent(location));
    }
}
