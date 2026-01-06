package org.betonquest.betonquest.quest.action;

import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test {@link DoNothingPlayerlessAction}.
 */
class DoNothingPlayerlessActionTest {

    @Test
    void testExecuteDoesNothing() {
        final PlayerlessAction event = new DoNothingPlayerlessAction();
        assertDoesNotThrow(event::execute, "Doing nothing should not thrown an exception.");
    }
}
