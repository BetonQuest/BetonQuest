package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.InstructionIdentifierArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadPlayerlessEvent;
import org.bukkit.Location;

import java.util.List;

/**
 * Factory to create chest events from {@link DefaultInstruction}s.
 */
public class ChestTakeEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the chest take event factory.
     *
     * @param data the data for primary server thread access
     */
    public ChestTakeEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final DefaultInstruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createChestTakeEvent(instruction), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final DefaultInstruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessEvent(createChestTakeEvent(instruction), data);
    }

    private NullableEventAdapter createChestTakeEvent(final DefaultInstruction instruction) throws QuestException {
        final Variable<Location> variableLocation = instruction.get(Argument.LOCATION);
        final Variable<List<Item>> item = instruction.getList(InstructionIdentifierArgument.ITEM);
        return new NullableEventAdapter(new ChestTakeEvent(variableLocation, item));
    }
}
