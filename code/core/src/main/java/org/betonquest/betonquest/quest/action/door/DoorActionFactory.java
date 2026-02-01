package org.betonquest.betonquest.quest.action.door;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.bukkit.Location;

import java.util.Locale;

/**
 * Factory to create door actions from {@link Instruction}s.
 */
public class DoorActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Create the door action factory.
     */
    public DoorActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createDoorAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createDoorAction(instruction);
    }

    private NullableActionAdapter createDoorAction(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        final String action = instruction.string().get().getValue(null);
        final DoorAction doorAction = switch (action.toLowerCase(Locale.ROOT)) {
            case "on" -> createOpenDoorAction(location);
            case "off" -> createCloseDoorAction(location);
            case "toggle" -> createToggleDoorAction(location);
            default -> throw new QuestException("Unknown door action (valid options are: on, off, toggle): " + action);
        };
        return new NullableActionAdapter(doorAction);
    }

    private DoorAction createOpenDoorAction(final Argument<Location> location) {
        return new DoorAction(location, door -> door.setOpen(true));
    }

    private DoorAction createCloseDoorAction(final Argument<Location> location) {
        return new DoorAction(location, door -> door.setOpen(false));
    }

    private DoorAction createToggleDoorAction(final Argument<Location> location) {
        return new DoorAction(location, door -> door.setOpen(!door.isOpen()));
    }
}
