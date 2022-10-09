package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.database.TagData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Test {@link StaticTagEvent}
 */
@ExtendWith(MockitoExtension.class)
class StaticTagEventTest {
    @Test
    void testStaticTagEvent(
            @Mock final TagData data,
            @Mock final TagChanger tagChanger) {
        final StaticTagEvent staticTagEvent = new StaticTagEvent(data, tagChanger);

        staticTagEvent.execute();
        staticTagEvent.execute();
        staticTagEvent.execute();

        verify(tagChanger, times(3)).changeTags(data);
    }
}
