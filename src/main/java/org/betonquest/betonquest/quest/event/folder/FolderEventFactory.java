package org.betonquest.betonquest.quest.event.folder;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Factory to create {@link FolderEvent} instances.
 */
public class FolderEventFactory implements EventFactory, StaticEventFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates a new {@link FolderEventFactory} instance.
     *
     * @param betonQuest the BetonQuest instance
     */
    public FolderEventFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return createFolderEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return createFolderEvent(instruction);
    }

    private NullableEventAdapter createFolderEvent(final Instruction instruction) throws InstructionParseException {
        final EventID[] events = instruction.getList(instruction::getEvent).toArray(new EventID[0]);
        final VariableNumber delay = instruction.getVarNum(instruction.getOptional("delay"));
        final VariableNumber period = instruction.getVarNum(instruction.getOptional("period"));
        final VariableNumber random = instruction.getVarNum(instruction.getOptional("random"));
        final TimeUnit timeUnit = getTimeUnit(instruction);
        final boolean cancelOnLogout = instruction.hasArgument("cancelOnLogout");
        return new NullableEventAdapter(new FolderEvent(betonQuest, events, delay, period, random, timeUnit, cancelOnLogout));
    }

    private TimeUnit getTimeUnit(final Instruction instruction) {
        if (instruction.hasArgument("ticks")) {
            return TimeUnit.TICKS;
        } else if (instruction.hasArgument("minutes")) {
            return TimeUnit.MINUTES;
        } else {
            return TimeUnit.SECONDS;
        }
    }
}
