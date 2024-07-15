package org.betonquest.betonquest.quest.event.door;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;

import java.util.Locale;

/**
 * Factory to create door events from {@link Instruction}s.
 */
public class DoorEventFactory implements ComposedEventFactory {
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
    public ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadComposedEvent(createDoorEvent(instruction), data);
    }

    private DoorEvent createDoorEvent(final Instruction instruction) throws InstructionParseException {
        final VariableLocation location = instruction.getLocation();
        final String action = instruction.next();
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "on" -> createOpenDoorEvent(location);
            case "off" -> createCloseDoorEvent(location);
            case "toggle" -> createToggleDoorEvent(location);
            default ->
                    throw new InstructionParseException("Unknown door action (valid options are: on, off, toggle): " + action);
        };
    }

    private DoorEvent createOpenDoorEvent(final VariableLocation location) {
        return new DoorEvent(location, door -> door.setOpen(true));
    }

    private DoorEvent createCloseDoorEvent(final VariableLocation location) {
        return new DoorEvent(location, door -> door.setOpen(false));
    }

    private DoorEvent createToggleDoorEvent(final VariableLocation location) {
        return new DoorEvent(location, door -> door.setOpen(!door.isOpen()));
    }
}
