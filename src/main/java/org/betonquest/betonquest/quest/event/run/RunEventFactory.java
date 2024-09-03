package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.typehandler.HandlerUtil;
import org.betonquest.betonquest.quest.legacy.LegacyTypeFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory to allows for running multiple events with one instruction string.
 */
public class RunEventFactory implements EventFactory, StaticEventFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Create a run event factory with the given BetonQuest instance.
     *
     * @param betonQuest the BetonQuest instance
     */
    public RunEventFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return createEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return createEvent(instruction);
    }

    private NullableEventAdapter createEvent(final Instruction instruction) throws InstructionParseException {
        final String[] parts = instruction.getAllParts();
        final List<QuestEvent> events = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (final String part : parts) {
            if (part.startsWith("^")) {
                if (!builder.isEmpty()) {
                    events.add(createEvent(builder.toString().trim(), instruction.getPackage()));
                    builder = new StringBuilder();
                }
                builder.append(part.substring(1)).append(' ');
            } else {
                builder.append(part).append(' ');
            }
        }
        if (!builder.isEmpty()) {
            events.add(createEvent(builder.toString().trim(), instruction.getPackage()));
        }
        return new NullableEventAdapter(new RunEvent(events));
    }

    /**
     * Constructs an event with given instruction and returns it.
     */
    private QuestEvent createEvent(final String instruction, final QuestPackage questPackage) throws InstructionParseException {
        final String[] parts = HandlerUtil.getNNSplit(instruction, "Not enough arguments in internal event", " ");
        final LegacyTypeFactory<QuestEvent> eventFactory = BetonQuest.getInstance().getQuestRegistries().getEventTypes().getFactory(parts[0]);
        if (eventFactory == null) {
            throw new InstructionParseException("Event type " + parts[0] + " is not registered, check if it's"
                    + " spelled correctly in internal event");
        }
        final Instruction eventInstruction = new Instruction(betonQuest.getLoggerFactory().create(Instruction.class), questPackage, null, instruction);
        return eventFactory.parseInstruction(eventInstruction);
    }
}
