package org.betonquest.betonquest.quest.event.drop;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.common.function.Selector;
import org.betonquest.betonquest.api.common.function.Selectors;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupStaticEventAdapter;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

    private static Item[] parseItemList(final Instruction instruction) throws InstructionParseException {
        final Item[] items = instruction.getItemListArgument("items");
        if (items.length == 0) {
            throw new InstructionParseException("No items to drop defined");
        }
        return items;
    }

    private static Selector<Location> parseLocationSelector(final Instruction instruction) throws InstructionParseException {
        return instruction.getLocationArgument("location")
                .map(loc -> (Selector<Location>) loc::getValue)
                .orElse(Selectors.fromPlayer(Player::getLocation));
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        if (instruction.hasArgument("location")) {
            return parseEvent(instruction);
        } else {
            return new OnlineProfileGroupStaticEventAdapter(PlayerConverter::getOnlineProfiles, parseEvent(instruction));
        }
    }

    @Override
    public ComposedEvent parseEvent(final Instruction instruction) throws InstructionParseException {
        final Item[] items = parseItemList(instruction);
        final Selector<Location> location = parseLocationSelector(instruction);

        return new PrimaryServerThreadComposedEvent(new DropEvent(items, location), data);
    }
}
