package org.betonquest.betonquest.quest.event.door;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadPlayerlessEvent;
import org.bukkit.Location;

import java.util.Locale;

/**
 * Factory to create door events from {@link Instruction}s.
 */
public class DoorEventFactory implements PlayerEventFactory, PlayerlessEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the door event factory.
     *
     * @param data the data for primary server thread access
     */
    public DoorEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createDoorEvent(instruction), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessEvent(createDoorEvent(instruction), data);
    }

    private NullableEventAdapter createDoorEvent(final Instruction instruction) throws QuestException {
        final Variable<Location> location = instruction.get(Argument.LOCATION);
        final String action = instruction.get(Argument.STRING).getValue(null);
        final DoorEvent doorEvent = switch (action.toLowerCase(Locale.ROOT)) {
            case "on" -> createOpenDoorEvent(location);
            case "off" -> createCloseDoorEvent(location);
            case "toggle" -> createToggleDoorEvent(location);
            default -> throw new QuestException("Unknown door action (valid options are: on, off, toggle): " + action);
        };
        return new NullableEventAdapter(doorEvent);
    }

    private DoorEvent createOpenDoorEvent(final Variable<Location> location) {
        return new DoorEvent(location, door -> door.setOpen(true));
    }

    private DoorEvent createCloseDoorEvent(final Variable<Location> location) {
        return new DoorEvent(location, door -> door.setOpen(false));
    }

    private DoorEvent createToggleDoorEvent(final Variable<Location> location) {
        return new DoorEvent(location, door -> door.setOpen(!door.isOpen()));
    }
}
