package org.betonquest.betonquest.quest.event.drop;

import org.betonquest.betonquest.api.common.function.Selector;
import org.betonquest.betonquest.api.common.function.Selectors;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupStaticEventAdapter;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Factory to create {@link Event}s that drop items from instructions.
 */
public class DropEventFactory implements EventFactory, StaticEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Creates the drop event factory.
     *
     * @param data the data for primary server thread access
     */
    public DropEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createDropEvent(instruction), data);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadStaticEvent(createStaticDropEvent(instruction), data);
    }

    private StaticEvent createStaticDropEvent(final Instruction instruction) throws QuestException {
        final NullableEventAdapter dropEvent = createDropEvent(instruction);
        if (!instruction.hasArgument("location")) {
            return new OnlineProfileGroupStaticEventAdapter(PlayerConverter::getOnlineProfiles, dropEvent);
        }
        return dropEvent;
    }

    private NullableEventAdapter createDropEvent(final Instruction instruction) throws QuestException {
        final Item[] items = parseItemList(instruction);
        final Selector<Location> location = parseLocationSelector(instruction);
        return new NullableEventAdapter(new DropEvent(items, location));
    }

    private Item[] parseItemList(final Instruction instruction) throws QuestException {
        final Item[] items = instruction.getItemList(instruction.getOptional("items"));
        if (items.length == 0) {
            throw new QuestException("No items to drop defined");
        }
        return items;
    }

    private Selector<Location> parseLocationSelector(final Instruction instruction) throws QuestException {
        final Optional<String> location = instruction.getOptionalArgument("location");
        if (location.isPresent()) {
            return instruction.get(location.get(), VariableLocation::new)::getValue;
        } else {
            return Selectors.fromPlayer(Player::getLocation);
        }
    }
}
