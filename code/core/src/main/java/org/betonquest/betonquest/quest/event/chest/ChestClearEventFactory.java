package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.bukkit.Location;

/**
 * Factory to create chest events from {@link Instruction}s.
 */
public class ChestClearEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Create the chest clear event factory.
     */
    public ChestClearEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createChestClearEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createChestClearEvent(instruction);
    }

    private NullableEventAdapter createChestClearEvent(final Instruction instruction) throws QuestException {
        final Argument<Location> variableLocation = instruction.location().get();
        return new NullableEventAdapter(new ChestClearEvent(variableLocation));
    }
}
