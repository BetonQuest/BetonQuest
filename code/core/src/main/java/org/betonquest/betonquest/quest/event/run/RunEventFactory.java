package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.kernel.processor.adapter.EventAdapter;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.betonquest.betonquest.quest.event.eval.EvalEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory to allows for running multiple events with one instruction string.
 */
public class RunEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Variable processor to create and resolve variables.
     */
    private final Variables variables;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * The event type registry providing factories to parse the evaluated instruction.
     */
    private final EventTypeRegistry eventTypeRegistry;

    /**
     * Create a run event factory with the given BetonQuest instance.
     *
     * @param variables         the variable processor to create and resolve variables
     * @param packManager       the quest package manager to get quest packages from
     * @param eventTypeRegistry the event type registry providing factories to parse the evaluated instruction
     */
    public RunEventFactory(final Variables variables, final QuestPackageManager packManager, final EventTypeRegistry eventTypeRegistry) {
        this.variables = variables;
        this.packManager = packManager;
        this.eventTypeRegistry = eventTypeRegistry;
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
                    events.add(EvalEvent.createEvent(variables, packManager, eventTypeRegistry, instruction.getPackage(), builder.toString().trim()));
                    builder = new StringBuilder();
                }
                builder.append(part.substring(1)).append(' ');
            } else {
                builder.append(part).append(' ');
            }
        }
        if (!builder.isEmpty()) {
            events.add(EvalEvent.createEvent(variables, packManager, eventTypeRegistry, instruction.getPackage(), builder.toString().trim()));
        }
        return new NullableEventAdapter(new RunEvent(events));
    }
}
