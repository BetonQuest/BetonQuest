package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.database.TagData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Test {@link TagEvent}
 */
@ExtendWith(MockitoExtension.class)
class TagEventTest {
    @Test
    void testTagEventUsesAlwaysActualTagData(
            @Mock final TagData firstData,
            @Mock final TagData secondData,
            @Mock final TagChanger tagChanger,
            @Mock final Profile profile1,
            @Mock final Profile profile2
            ) {
        final Iterator<TagData> data = List.of(firstData, secondData).iterator();
        final TagEvent tagEvent = new TagEvent(player -> data.next(), tagChanger);

        tagEvent.execute(profile1);
        tagEvent.execute(profile2);

        verify(tagChanger).changeTags(firstData);
        verify(tagChanger).changeTags(secondData);
    }
}
