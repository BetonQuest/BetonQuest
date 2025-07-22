package org.betonquest.betonquest.quest.event.eval;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;

/**
 * A factory for creating Eval events.
 */
public class EvalEventFactory implements PlayerEventFactory, PlayerlessEventFactory {
    /**
     * The event type registry providing factories to parse the evaluated instruction.
     */
    private final EventTypeRegistry eventTypeRegistry;

    /**
     * Create a new Eval event factory.
     *
     * @param eventTypeRegistry the event type registry providing factories to parse the evaluated instruction
     */
    public EvalEventFactory(final EventTypeRegistry eventTypeRegistry) {
        this.eventTypeRegistry = eventTypeRegistry;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return parseEvalEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return parseEvalEvent(instruction);
    }

    private NullableEventAdapter parseEvalEvent(final Instruction instruction) throws QuestException {
        final String rawInstruction = String.join(" ", instruction.getValueParts());
        return new NullableEventAdapter(new EvalEvent(eventTypeRegistry, instruction.getPackage(),
                instruction.get(rawInstruction, Argument.STRING)));
    }
}
