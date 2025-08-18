package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.IdentifierArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadPlayerlessEvent;
import org.bukkit.Location;

import java.util.List;

/**
 * Factory to create chest events from {@link Instruction}s.
 */
public class ChestGiveEventFactory implements PlayerEventFactory, PlayerlessEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the chest give event factory.
     *
     * @param data the data for primary server thread access
     */
    public ChestGiveEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createChestGiveEvent(instruction), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessEvent(createChestGiveEvent(instruction), data);
    }

    private NullableEventAdapter createChestGiveEvent(final Instruction instruction) throws QuestException {
        final Variable<Location> location = instruction.get(Argument.LOCATION);
        final Variable<List<Item>> items = instruction.getList(IdentifierArgument.ITEM);
        return new NullableEventAdapter(new ChestGiveEvent(location, items)
        );
    }
}
