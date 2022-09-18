package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;

/**
 * Factory for "static" events that always returns null.
 */
public class NullStaticEventFactory implements StaticEventFactory {
    /**
     * Create the factory.
     */
    public NullStaticEventFactory() {
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) {
        return null;
    }
}
