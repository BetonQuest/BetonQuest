package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test {@link DoNothingStaticEvent}.
 */
class DoNothingStaticEventTest {
    @Test
    void testExecuteDoesNothing() {
        final StaticEvent event = new DoNothingStaticEvent();
        assertDoesNotThrow(event::execute, "Doing nothing should not thrown an exception.");
    }
}
