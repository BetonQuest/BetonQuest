package org.betonquest.betonquest.quest.action.chest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.bukkit.Location;

/**
 * Factory to create chest actions from {@link Instruction}s.
 */
public class ChestClearActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Create the chest clear action factory.
     */
    public ChestClearActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createChestClearAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createChestClearAction(instruction);
    }

    private NullableActionAdapter createChestClearAction(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        return new NullableActionAdapter(new ChestClearAction(location));
    }
}
