package org.betonquest.betonquest.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test {@link SequentialPlayerlessAction}.
 */
@ExtendWith(MockitoExtension.class)
class SequentialPlayerlessActionTest {

    @Test
    void testCanExecuteWithZeroActions() {
        final PlayerlessAction event = new SequentialPlayerlessAction(List.of());
        assertDoesNotThrow(event::execute, "SequentialStaticAction should not fail with no actions to execute.");
    }

    @Test
    void testExecutesOneAction(@Mock final PlayerlessAction internal) throws QuestException {
        final PlayerlessAction event = new SequentialPlayerlessAction(List.of(internal));
        event.execute();
        verify(internal).execute();
    }

    @Test
    void testExecutesMultipleActions(@Mock final PlayerlessAction first, @Mock final PlayerlessAction second) throws QuestException {
        final PlayerlessAction event = new SequentialPlayerlessAction(List.of(first, second));

        event.execute();

        final InOrder order = inOrder(first, second);
        order.verify(first).execute();
        order.verify(second).execute();
    }

    @Test
    void testFailuresArePassedOn(@Mock final PlayerlessAction internal) throws QuestException {
        final QuestException exception = new QuestException("test exception");
        doThrow(exception).when(internal).execute();
        final PlayerlessAction event = new SequentialPlayerlessAction(List.of(internal));

        final QuestException thrown
                = assertThrows(QuestException.class, event::execute, "The failure of an internal event should fail the sequential event immediately.");
        assertSame(exception, thrown, "The exception should not be wrapped or exchanged.");
    }
}
