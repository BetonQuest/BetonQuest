package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.bukkit.Location;

import java.util.List;

/**
 * Factory to create chest events from {@link Instruction}s.
 */
public class ChestTakeEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Create the chest take event factory.
     */
    public ChestTakeEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createChestTakeEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createChestTakeEvent(instruction);
    }

    private NullableEventAdapter createChestTakeEvent(final Instruction instruction) throws QuestException {
        final Variable<Location> variableLocation = instruction.location().get();
        final Variable<List<ItemWrapper>> item = instruction.item().getList();
        return new NullableEventAdapter(new ChestTakeEvent(variableLocation, item));
    }
}
