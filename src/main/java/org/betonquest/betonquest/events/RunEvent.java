package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.item.typehandler.HandlerUtil;
import org.betonquest.betonquest.quest.event.legacy.QuestEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows for running multiple events with one instruction string.
 */
public class RunEvent extends QuestEvent {
    /**
     * Events that the run event will execute.
     */
    private final List<QuestEvent> internalEvents = new ArrayList<>();

    /**
     * Create a run event from the given instruction.
     *
     * @param instruction instruction defining the run event
     * @throws InstructionParseException if the instruction is invalid
     */
    public RunEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        staticness = true;
        persistent = true;
        final String[] parts = instruction.getAllParts();
        StringBuilder builder = new StringBuilder();
        for (final String part : parts) {
            if (part.startsWith("^")) {
                if (!builder.isEmpty()) {
                    internalEvents.add(createEvent(builder.toString().trim()));
                    builder = new StringBuilder();
                }
                builder.append(part.substring(1)).append(' ');
            } else {
                builder.append(part).append(' ');
            }
        }
        if (!builder.isEmpty()) {
            internalEvents.add(createEvent(builder.toString().trim()));
        }
    }

    /**
     * Constructs an event with given instruction and returns it.
     */
    private QuestEvent createEvent(final String instruction) throws InstructionParseException {
        final String[] parts = HandlerUtil.getNNSplit(instruction, "Not enough arguments in internal event", " ");
        final QuestEventFactory eventFactory = BetonQuest.getInstance().getQuestRegistries().getEventTypes().getFactory(parts[0]);
        if (eventFactory == null) {
            // if it's null then there is no such type registered, log an error
            throw new InstructionParseException("Event type " + parts[0] + " is not registered, check if it's"
                    + " spelled correctly in internal event");
        }
        final Instruction eventInstruction = new Instruction(BetonQuest.getInstance().getLoggerFactory().create(Instruction.class), this.instruction.getPackage(), null, instruction);
        return eventFactory.parseEventInstruction(eventInstruction);
    }

    @Override
    protected Void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        for (final QuestEvent event : internalEvents) {
            event.fire(profile);
        }
        return null;
    }
}
