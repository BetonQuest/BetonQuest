package org.betonquest.betonquest.compatibility.brewery.event;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link GiveBrewEvent}s from {@link Instruction}s.
 */
public class TakeBrewEventFactory implements EventFactory {
    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Give Brew Events.
     *
     * @param data the data used for primary server access.
     */
    public TakeBrewEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final VariableNumber countVar = instruction.get(VariableNumber::new);
        final VariableString brewVar = instruction.get(VariableString::new);
        return new PrimaryServerThreadEvent(new TakeBrewEvent(countVar, brewVar), data);
    }
}
