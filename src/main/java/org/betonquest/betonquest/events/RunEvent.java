package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.event.legacy.QuestEventFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows for running multiple events with one instruction string.
 */
@SuppressWarnings("PMD.CommentRequired")
public class RunEvent extends QuestEvent {

    private final List<QuestEvent> internalEvents = new ArrayList<>();

    public RunEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        staticness = true;
        persistent = true;
        final String[] parts = instruction.getInstruction().substring(3).trim().split(" ");
        if (parts.length <= 0) {
            throw new InstructionParseException("Not enough arguments");
        }
        StringBuilder builder = new StringBuilder();
        for (final String part : parts) {
            if (!part.isEmpty() && part.charAt(0) == '^') {
                if (builder.length() != 0) {
                    internalEvents.add(createEvent(builder.toString().trim()));
                    builder = new StringBuilder();
                }
                builder.append(part.substring(1)).append(' ');
            } else {
                builder.append(part).append(' ');
            }
        }
        internalEvents.add(createEvent(builder.toString().trim()));
    }

    /**
     * Constructs an event with given instruction and returns it.
     */
    private QuestEvent createEvent(final String instruction) throws InstructionParseException {
        final String[] parts = instruction.split(" ");
        if (parts.length <= 0) {
            throw new InstructionParseException("Not enough arguments in internal event");
        }
        final QuestEventFactory eventFactory = BetonQuest.getInstance().getEventFactory(parts[0]);
        if (eventFactory == null) {
            // if it's null then there is no such type registered, log an error
            throw new InstructionParseException("Event type " + parts[0] + " is not registered, check if it's"
                    + " spelled correctly in internal event");
        }
        final Instruction eventInstruction = new Instruction(this.instruction.getPackage(), null, instruction);
        return eventFactory.parseEventInstruction(eventInstruction);
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        for (final QuestEvent event : internalEvents) {
            event.fire(profile);
        }
        return null;
    }
}
