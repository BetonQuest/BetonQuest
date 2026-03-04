package org.betonquest.betonquest.quest.action.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.data.TagHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Test {@link PlayerlessTagAction}.
 */
@ExtendWith(MockitoExtension.class)
class PlayerlessTagActionTest {

    @Test
    void test_playerless_tag_action(
            @Mock final TagHolder data,
            @Mock final TagChanger tagChanger) throws QuestException {
        final PlayerlessTagAction playerlessTagAction = new PlayerlessTagAction(data, tagChanger);

        playerlessTagAction.execute();
        playerlessTagAction.execute();
        playerlessTagAction.execute();

        verify(tagChanger, times(3)).changeTags(data, null);
    }
}
