package org.betonquest.betonquest.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test {@link ThrowExceptionPlayerlessAction}.
 */
class DoNothingPlayerlessActionTest {

    @Test
    void execute_does_nothing() {
        final PlayerlessAction action = new ThrowExceptionPlayerlessAction("Test exception");
        assertThrows(QuestException.class, action::execute, "Action should thrown an exception.");
    }
}
