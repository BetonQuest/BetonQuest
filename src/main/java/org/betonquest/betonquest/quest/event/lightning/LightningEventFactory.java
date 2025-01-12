package org.betonquest.betonquest.quest.event.lightning;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;

/**
 * Factory for {@link LightningEvent} from the {@link Instruction}.
 */
public class LightningEventFactory implements EventFactory, StaticEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new LightningEventFactory.
     *
     * @param data the data for primary server thread access
     */
    public LightningEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createLightningEvent(instruction), data);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadStaticEvent(createLightningEvent(instruction), data);
    }

    private NullableEventAdapter createLightningEvent(final Instruction instruction) throws QuestException {
        final VariableLocation location = instruction.getLocation();
        final boolean noDamage = instruction.hasArgument("noDamage");
        return new NullableEventAdapter(new LightningEvent(location, noDamage));
    }
}
