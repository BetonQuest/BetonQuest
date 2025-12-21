package org.betonquest.betonquest.quest.event.eval;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.kernel.processor.adapter.EventAdapter;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * An event which evaluates to another event.
 */
public class EvalEvent implements NullableEvent {

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
    private final EventTypeRegistry eventTypeRegistry;

    /**
     * The quest package to relate the event to.
     */
    private final QuestPackage pack;

    /**
     * The evaluation input.
     */
    private final Argument<String> evaluation;

    /**
     * Created a new Eval event.
     *
     * @param placeholders      the {@link Placeholders} to create and resolve placeholders
     * @param packManager       the quest package manager to get quest packages from
     * @param eventTypeRegistry the event type registry providing factories to parse the evaluated instruction
     * @param pack              the quest package to relate the event to
     * @param evaluation        the evaluation input
     */
    public EvalEvent(final Placeholders placeholders, final QuestPackageManager packManager, final EventTypeRegistry eventTypeRegistry,
                     final QuestPackage pack, final Argument<String> evaluation) {
        this.placeholders = placeholders;
        this.packManager = packManager;
        this.eventTypeRegistry = eventTypeRegistry;
        this.pack = pack;
        this.evaluation = evaluation;
    }

    /**
     * Constructs an event with a given instruction and returns it.
     *
     * @param placeholders      the {@link Placeholders} to create and resolve placeholders
     * @param packManager       the quest package manager to get quest packages from
     * @param instruction       the instruction string to parse
     * @param eventTypeRegistry the event type registry providing factories to parse the evaluated instruction
     * @param pack              the quest package to relate the event to
     * @return the event
     * @throws QuestException if the event could not be created
     */
    public static EventAdapter createEvent(final Placeholders placeholders, final QuestPackageManager packManager,
                                           final EventTypeRegistry eventTypeRegistry,
                                           final QuestPackage pack, final String instruction) throws QuestException {
        final Instruction eventInstruction = new DefaultInstruction(placeholders, packManager, pack, null, DefaultArgumentParsers.INSTANCE, instruction);
        final TypeFactory<EventAdapter> eventFactory = eventTypeRegistry.getFactory(eventInstruction.getPart(0));
        return eventFactory.parseInstruction(eventInstruction);
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        createEvent(placeholders, packManager, eventTypeRegistry, pack, evaluation.getValue(profile)).fire(profile);
    }
}
