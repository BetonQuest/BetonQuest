package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test {@link SequentialPlayerlessEvent}.
 */
@ExtendWith(MockitoExtension.class)
class SequentialPlayerlessEventTest {
    @Test
    void testCanExecuteWithZeroEvents() {
        final PlayerlessEvent event = new SequentialPlayerlessEvent();
        assertDoesNotThrow(event::execute, "SequentialStaticEvent should not fail with no events to execute.");
    }

    @Test
    void testExecutesOneEvent(@Mock final PlayerlessEvent internal) throws QuestException {
        final PlayerlessEvent event = new SequentialPlayerlessEvent(internal);
        event.execute();
        verify(internal).execute();
    }

    @Test
    void testExecutesMultipleEvents(@Mock final PlayerlessEvent first, @Mock final PlayerlessEvent second) throws QuestException {
        final PlayerlessEvent event = new SequentialPlayerlessEvent(first, second);

        event.execute();

        final InOrder order = inOrder(first, second);
        order.verify(first).execute();
        order.verify(second).execute();
    }

    @Test
    void testFailuresArePassedOn(@Mock final PlayerlessEvent internal) throws QuestException {
        final QuestException exception = new QuestException("test exception");
        doThrow(exception).when(internal).execute();
        final PlayerlessEvent event = new SequentialPlayerlessEvent(internal);

        final QuestException thrown
                = assertThrows(QuestException.class, event::execute, "The failure of an internal event should fail the sequential event immediately.");
        assertSame(exception, thrown, "The exception should not be wrapped or exchanged.");
    }
}
