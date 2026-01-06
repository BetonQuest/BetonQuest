package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.kernel.processor.adapter.ActionAdapter;
import org.betonquest.betonquest.kernel.registry.quest.ActionTypeRegistry;
import org.betonquest.betonquest.quest.event.eval.EvalEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory to allows for running multiple events with one instruction string.
 */
public class RunEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * The event type registry providing factories to parse the evaluated instruction.
     */
    private final ActionTypeRegistry actionTypeRegistry;

    /**
     * Create a run event factory with the given BetonQuest instance.
     *
     * @param placeholders       the {@link Placeholders} to create and resolve placeholders
     * @param packManager        the quest package manager to get quest packages from
     * @param actionTypeRegistry the event type registry providing factories to parse the evaluated instruction
     */
    public RunEventFactory(final Placeholders placeholders, final QuestPackageManager packManager, final ActionTypeRegistry actionTypeRegistry) {
        this.placeholders = placeholders;
        this.packManager = packManager;
        this.actionTypeRegistry = actionTypeRegistry;
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
        final List<ActionAdapter> actions = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (final String part : parts) {
            if (part.startsWith("^")) {
                if (!builder.isEmpty()) {
                    actions.add(EvalEvent.createEvent(placeholders, packManager, actionTypeRegistry, instruction.getPackage(), builder.toString().trim()));
                    builder = new StringBuilder();
                }
                builder.append(part.substring(1)).append(' ');
            } else {
                builder.append(part).append(' ');
            }
        }
        if (!builder.isEmpty()) {
            actions.add(EvalEvent.createEvent(placeholders, packManager, actionTypeRegistry, instruction.getPackage(), builder.toString().trim()));
        }
        return new NullableEventAdapter(new RunEvent(actions));
    }
}
