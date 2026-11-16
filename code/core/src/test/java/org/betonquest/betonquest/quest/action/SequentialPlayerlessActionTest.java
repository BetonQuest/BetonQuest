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
    void can_execute_with_zero_actions() {
        final PlayerlessAction action = new SequentialPlayerlessAction(List.of());
        assertDoesNotThrow(action::execute, "SequentialPlayerlessAction should not fail with no actions to execute.");
    }

    @Test
    void executes_one_action(@Mock final PlayerlessAction internal) throws QuestException {
        final PlayerlessAction action = new SequentialPlayerlessAction(List.of(internal));
        action.execute();
        verify(internal).execute();
    }

    @Test
    void executes_multiple_actions(@Mock final PlayerlessAction first, @Mock final PlayerlessAction second) throws QuestException {
        final PlayerlessAction action = new SequentialPlayerlessAction(List.of(first, second));

        action.execute();

        final InOrder order = inOrder(first, second);
        order.verify(first).execute();
        order.verify(second).execute();
    }

    @Test
    void failures_are_passed_on(@Mock final PlayerlessAction internal) throws QuestException {
        final QuestException exception = new QuestException("test exception");
        doThrow(exception).when(internal).execute();
        final PlayerlessAction action = new SequentialPlayerlessAction(List.of(internal));

        final QuestException thrown
                = assertThrows(QuestException.class, action::execute, "The failure of an internal action should fail the sequential action immediately.");
        assertSame(exception, thrown, "The exception should not be wrapped or exchanged.");
    }
}
