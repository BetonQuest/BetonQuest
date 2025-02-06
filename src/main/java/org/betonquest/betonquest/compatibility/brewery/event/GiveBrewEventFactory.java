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
public class GiveBrewEventFactory implements EventFactory {
    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Give Brew Events.
     *
     * @param data the data used for primary server access.
     */
    public GiveBrewEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final VariableNumber amountVar = instruction.get(VariableNumber::new);
        final VariableNumber qualityVar = instruction.get(VariableNumber::new);
        final VariableString nameVar = instruction.get(VariableString::new);
        return new PrimaryServerThreadEvent(new GiveBrewEvent(amountVar, qualityVar, nameVar), data);
    }
}
