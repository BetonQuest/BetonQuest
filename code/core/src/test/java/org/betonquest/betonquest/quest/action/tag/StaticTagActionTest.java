package org.betonquest.betonquest.quest.action.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.database.TagData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Test {@link PlayerlessTagAction}.
 */
@ExtendWith(MockitoExtension.class)
class StaticTagActionTest {

    @Test
    void testStaticTagAction(
            @Mock final TagData data,
            @Mock final TagChanger tagChanger) throws QuestException {
        final PlayerlessTagAction staticTagAction = new PlayerlessTagAction(data, tagChanger);

        staticTagAction.execute();
        staticTagAction.execute();
        staticTagAction.execute();

        verify(tagChanger, times(3)).changeTags(data, null);
    }
}
