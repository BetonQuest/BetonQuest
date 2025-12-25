package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test {@link DoNothingPlayerlessEvent}.
 */
class DoNothingPlayerlessEventTest {

    @Test
    void testExecuteDoesNothing() {
        final PlayerlessEvent event = new DoNothingPlayerlessEvent();
        assertDoesNotThrow(event::execute, "Doing nothing should not thrown an exception.");
    }
}
