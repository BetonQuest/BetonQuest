package org.betonquest.betonquest.quest.event.door;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.bukkit.Location;

import java.util.Locale;

/**
 * Factory to create door events from {@link Instruction}s.
 */
public class DoorEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Create the door event factory.
     */
    public DoorEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createDoorEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createDoorEvent(instruction);
    }

    private NullableEventAdapter createDoorEvent(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        final String action = instruction.string().get().getValue(null);
        final DoorEvent doorEvent = switch (action.toLowerCase(Locale.ROOT)) {
            case "on" -> createOpenDoorEvent(location);
            case "off" -> createCloseDoorEvent(location);
            case "toggle" -> createToggleDoorEvent(location);
            default -> throw new QuestException("Unknown door action (valid options are: on, off, toggle): " + action);
        };
        return new NullableEventAdapter(doorEvent);
    }

    private DoorEvent createOpenDoorEvent(final Argument<Location> location) {
        return new DoorEvent(location, door -> door.setOpen(true));
    }

    private DoorEvent createCloseDoorEvent(final Argument<Location> location) {
        return new DoorEvent(location, door -> door.setOpen(false));
    }

    private DoorEvent createToggleDoorEvent(final Argument<Location> location) {
        return new DoorEvent(location, door -> door.setOpen(!door.isOpen()));
    }
}
