package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.item.typehandler.HandlerUtil;
import org.betonquest.betonquest.kernel.processor.adapter.EventAdapter;
import org.betonquest.betonquest.kernel.registry.TypeFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory to allows for running multiple events with one instruction string.
 */
public class RunEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Create a run event factory with the given BetonQuest instance.
     */
    public RunEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createEvent(instruction);
    }

    private NullableEventAdapter createEvent(final Instruction instruction) throws QuestException {
        final List<String> parts = instruction.getValueParts();
        final List<EventAdapter> events = new ArrayList<>();
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
    private EventAdapter createEvent(final String instruction, final QuestPackage questPackage) throws QuestException {
        final String[] parts = HandlerUtil.getNNSplit(instruction, "Not enough arguments in internal event", " ");
        final TypeFactory<EventAdapter> eventFactory = BetonQuest.getInstance().getQuestRegistries().event().getFactory(parts[0]);
        if (eventFactory == null) {
            throw new QuestException("Event type " + parts[0] + " is not registered, check if it's"
                    + " spelled correctly in internal event");
        }
        final Instruction eventInstruction = new Instruction(questPackage, null, instruction);
        return eventFactory.parseInstruction(eventInstruction);
    }
}
